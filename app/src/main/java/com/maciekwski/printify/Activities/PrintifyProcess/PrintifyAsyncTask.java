package com.maciekwski.printify.Activities.PrintifyProcess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.ImageUtils.Step2LocalThresholding.BWLocalThresholder;
import com.maciekwski.printify.Utils.ImageUtils.Step2PenLines.HsvHistogramCorrector;
import com.maciekwski.printify.Utils.ImageUtils.Step2PenLines.PenLineBinarizer;
import com.maciekwski.printify.Utils.ImageUtils.Step3Joining.ImageJoiner;
import com.maciekwski.printify.Utils.Misc.ArrayToMatrixConverter;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.BWTableToARGBBufferConverter;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.BWHistogramEqualizer;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableColorReductor;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableSharpener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 16.12.2015.
 */
public class PrintifyAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    private ArrayList<int[][]> pixelTables;
    private ArrayList<Uri> imagesToDisplay;
    private Context context;
    private PrintifyActivity activity;
    View busyIndicator;
    final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    int[][] placeholderMatrix;
    public PrintifyAsyncTask(Context context, ArrayList<Uri> imagesToDisplay, PrintifyActivity activity) {
        super();
        this.activity = activity;
        this.imagesToDisplay = imagesToDisplay;
        this.context = context;
        pixelTables = new ArrayList<>();
    }

    private void initPlaceholder() {
        placeholderMatrix = new int[pixelTables.get(0).length][pixelTables.get(0)[0].length];
        int rows = placeholderMatrix.length;
        int cols = placeholderMatrix[0].length;
        for(int row = 0; row < rows; row++){
            for(int col = 0; col <cols; col++){
                placeholderMatrix[row][col] = 255;
            }
        }
    }

    @Override
    public void onPreExecute() {
        // init your dialog here;
        setupProgressBar();
        blockLayout();
    }

    private void setupProgressBar() {
        busyIndicator = activity.getLayoutInflater().inflate(R.layout.busy_indicator, null);
        busyIndicator.setBackground(context.getResources().getDrawable(R.color.TransparentPrintifyGrey));
        windowParams.gravity = Gravity.CENTER;
        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
    }

    private void blockLayout() {
        activity.getWindowManager().addView(busyIndicator, windowParams);
        activity.printifyPager.setPagingEnabled(false);
        activity.findViewById(R.id.button_continue_printify).setClickable(false);
        activity.findViewById(R.id.button_finish_printify).setClickable(false);
    }

    @Override
    public void onPostExecute(Integer result) {
        activity.onPostExexute();
    }
    @Override
    protected Integer doInBackground(Integer... arg0) {
        createPixelTables();
        initPlaceholder();
        ArrayList<int[][]> printifiedPixelTables = printifyPixelTables();

        savePixelTablesToBitmapUris(printifiedPixelTables);

        return null;
    }

    private ArrayList<int[][]> printifyPixelTables() {
        ExecutorService executor = Executors.newFixedThreadPool(imagesToDisplay.size());
        final ArrayList<int[][]> finalPixelTables = new ArrayList<>();
        for (int i = 0; i < pixelTables.size(); i++) {
            finalPixelTables.add(new int[1][1]);
        }
        for (int i = 0; i < pixelTables.size(); i++) {
            final int[][] pt = pixelTables.get(i);
            final int threadnum = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("PrintifyThread", "Thread: " + threadnum + " joined");
                    finalPixelTables.set(threadnum, printifySinglePixelTable(pt));
                    Log.d("PrintifyThread", "Thread: " + threadnum + " finished");
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
            Log.d("PrintifyThread", "Thread: All threads done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int[][] pt : pixelTables) {
            finalPixelTables.add(this.printifySinglePixelTable(pt));
        }
        return finalPixelTables;
    }
    private void createPixelTables() {
        for (Uri uri : imagesToDisplay) {
            Bitmap workerBitmap = ImageLoader.loadSingleImageFromUri(uri, context);
            int width = workerBitmap.getWidth();
            int height = workerBitmap.getHeight();
            int[] intArray = new int[width * height];
            workerBitmap.getPixels(intArray, 0, width, 0, 0, width, height);
            workerBitmap.recycle();
            pixelTables.add(ArrayToMatrixConverter.convert(intArray, width, height));
        }
    }

    private int[][] printifySinglePixelTable(int[][] pixelTable) {
        int[][] detectedPenStrokesPixels = this.recognizePenStrokes(pixelTable);
        int[][] preparedBWPixels = this.prepareForStep2BW(pixelTable);
        int[][] locallyThresholdedPixels = this.binarizeLocally(preparedBWPixels);
        int[][] edgeDetectedPixles = this.detectEdges(preparedBWPixels);
        return this.connectPixelTables(detectedPenStrokesPixels, locallyThresholdedPixels, edgeDetectedPixles);
    }

    private void savePixelTablesToBitmapUris(ArrayList<int[][]> printifiedPixelTables) {
        for (int i = 0; i < imagesToDisplay.size(); i++) {
            int[][] ppt = printifiedPixelTables.get(i);
            Uri uri = imagesToDisplay.get(i);
            saveSinglePixelTableToGivenUri(ppt, uri);
        }
    }

    private void saveSinglePixelTableToGivenUri(int[][] ppt, Uri uri) {
        Bitmap resultBitmap = Bitmap.createBitmap(ppt.length, ppt[0].length, Bitmap.Config.ARGB_8888);
        int[] pixelBuffer = BWTableToARGBBufferConverter.convertPixelTable(ppt);
        resultBitmap.setPixels(pixelBuffer, 0, ppt.length, 0, 0, ppt.length, ppt[0].length);
        ImageSaver.saveBitmapToGivenUri(resultBitmap, uri);
        resultBitmap.recycle();
    }

    private int[][] connectPixelTables(int[][] detectedPenStrokesPixels, int[][] locallyThresholdedPixels, int[][] edgeDetectedPixles) {
        return ImageJoiner.takeDarkest(locallyThresholdedPixels, detectedPenStrokesPixels, edgeDetectedPixles);
    }

    private int[][] detectEdges(int[][] preparedBWPixels) {
        return placeholderMatrix;
    }

    private int[][] binarizeLocally(int[][] preparedBWPixels) {
        return BWLocalThresholder.threshold(preparedBWPixels); //placeholderMatrix
    }

    private int[][] recognizePenStrokes(int[][] pt) {
        return placeholderMatrix;//PenLineBinarizer.binarizeByPenColor(HsvHistogramCorrector.correctHsvHistogram(pt));//placeholderMatrix
    }

    private int[][] prepareForStep2BW(int[][] pt) {
        int[][] preparedArr = new int[pt.length][];
        for (int i = 0; i < pt.length; i++) {
            preparedArr[i] = Arrays.copyOfRange(pt[i], 0, pt[i].length);
        }

        preparedArr = PixelTableColorReductor.reduceColorsFromPixelTable(preparedArr);
        preparedArr = BWHistogramEqualizer.correctHistogram(preparedArr);
        preparedArr = PixelTableSharpener.sharpenBWPixelTable(preparedArr);
        return preparedArr;
    }
}