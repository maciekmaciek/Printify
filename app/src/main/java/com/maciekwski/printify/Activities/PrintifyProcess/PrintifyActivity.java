package com.maciekwski.printify.Activities.PrintifyProcess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.maciekwski.printify.Adapters.PrintifyPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.ArrayToMatrixConverter;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapSizeWithContentVertices;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.BWHistogramEqualizer;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.BWTableToARGBBufferConverter;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableColorReductor;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableSharpener;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrintifyActivity extends ActionBarActivity {

    private PrintifyProcessViewPager printifyPager;
    private PagerAdapter printifyPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;
    private ArrayList<int[][]> pixelTables;
    View busyIndicator;
    final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busyIndicator = getLayoutInflater().inflate(R.layout.busy_indicator, null);
        this.setupProgressBar();
        this.changeImages();
        setContentView(R.layout.activity_printify);
        this.preparePager();

    }
    private void setupProgressBar(){
        busyIndicator.setBackground(getResources().getDrawable(R.color.TransparentPrintifyGrey));
        windowParams.gravity = Gravity.CENTER;
        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
    }


    private void changeImages() {
        this.imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        pixelTables = new ArrayList<>();
    }
    private void preparePager() {
        printifyPager = (PrintifyProcessViewPager) findViewById(R.id.printify_pager);
        printifyPagerAdapter = new PrintifyPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
        printifyPager.setAdapter(printifyPagerAdapter);
        PageChangeListener pageChangeListener = new PageChangeListener();
        printifyPager.addOnPageChangeListener(pageChangeListener);
        ((TextView)findViewById(R.id.text_view_page_num)).setText(1 + " / " + printifyPagerAdapter.getCount());

    }

    @Override
    public void onBackPressed() {
        if (printifyPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            printifyPager.setCurrentItem(printifyPager.getCurrentItem() - 1);
        }
    }


    public void continuePrintifyAsync(View view) {
        new CreatePixelTablesAsyncTask().execute();
    }

    private class CreatePixelTablesAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        public void onPreExecute() {
            // init your dialog here;
            getWindowManager().addView(busyIndicator, windowParams);
            printifyPager.setPagingEnabled(false);
            findViewById(R.id.button_continue_printify).setClickable(false);
            findViewById(R.id.button_finish_printify).setClickable(false);
            imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        }

        public void onPostExecute(Integer result) {

            new PrintifyAsyncTask().execute();
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // do your things here
            createPixelTables();
            return null;
        }
        private void createPixelTables() {
            for(Uri uri: imagesToDisplay){
                Bitmap workerBitmap = ImageLoader.loadSingleImageFromUri(uri,getApplicationContext());
                int width = workerBitmap.getWidth();
                int height = workerBitmap.getHeight();
                int[] intArray = new int[width*height];
                workerBitmap.getPixels(intArray, 0, width, 0, 0, width, height);
                workerBitmap.recycle();
                pixelTables.add(ArrayToMatrixConverter.convert(intArray, width, height));
            }
        }
    }
    private class PrintifyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        public void onPreExecute() {
            // init your dialog here;
        }

        public void onPostExecute(Integer result) {
            Intent intent = new Intent(getApplicationContext(), PostPrintifyActivity.class);
            intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
            startActivity(intent);
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            ArrayList<int[][]> printifiedPixelTables = printifyPixelTables();
            savePixelTablesToBitmapUris(printifiedPixelTables);

            return null;
        }
    }


    private ArrayList<int[][]> printifyPixelTables() {
        ExecutorService executor = Executors.newFixedThreadPool(imagesToDisplay.size());
        final ArrayList<int[][]> finalPixelTables = new ArrayList<>();
        for(int i = 0; i < pixelTables.size(); i++){
            finalPixelTables.add(new int[1][1]);
        }
        for(int i = 0; i < pixelTables.size(); i++){
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
        for(int[][] pt: pixelTables){
            finalPixelTables.add(this.printifySinglePixelTable(pt));
        }
        return finalPixelTables;
    }

    private int[][] printifySinglePixelTable(int[][] pixelTable) {
        int[][] detectedPenStrokesPixels = this.recognizePenStrokes(pixelTable);
        int[][] preparedBWPixels = this.prepareForStep2BW(pixelTable);
        int[][] locallyThresholdedPixels = this.binarizeLocally(preparedBWPixels);
        int[][] edgeDetectedPixles = this.detectEdges(preparedBWPixels);
        return this.connectPixelTables(detectedPenStrokesPixels,locallyThresholdedPixels,edgeDetectedPixles);
    }

    private void savePixelTablesToBitmapUris(ArrayList<int[][]> printifiedPixelTables) {
        for(int i = 0; i < imagesToDisplay.size(); i++){
            int[][] ppt =  printifiedPixelTables.get(i);
            Uri uri = imagesToDisplay.get(i);
            saveSinglePixelTableToGivenUri(ppt, uri);
        }
    }

    private void saveSinglePixelTableToGivenUri(int[][] ppt, Uri uri) {
        Bitmap resultBitmap = Bitmap.createBitmap(ppt.length, ppt[0].length, Bitmap.Config.ARGB_8888);
        int[] pixelBuffer = BWTableToARGBBufferConverter.convertPixelTable(ppt);
        resultBitmap.setPixels(pixelBuffer,0,ppt.length,0,0,ppt.length, ppt[0].length);
        //imagesToDisplay.set(urinum, ImageSaver.saveSingleImageReturnUri(resultBitmap, urinum));
        ImageSaver.saveBitmapToGivenUri(resultBitmap, uri);
        resultBitmap.recycle();
    }

    private int[][] connectPixelTables(int[][] detectedPenStrokesPixels, int[][] locallyThresholdedPixels, int[][] edgeDetectedPixles) {
        return locallyThresholdedPixels;
    }

    private int[][] detectEdges(int[][] preparedBWPixels) {
        return new int[preparedBWPixels.length][preparedBWPixels[0].length];
    }

    private int[][] binarizeLocally(int[][] preparedBWPixels) {
        return preparedBWPixels;
    }

    private int[][] recognizePenStrokes(int[][] pt) {
        return new int[pt.length][pt[0].length];
    }

    private  int[][] prepareForStep2BW(int[][] pt) {
        int[][] preparedArr = new int[pt.length][];
        for(int i = 0; i < pt.length; i++){
            preparedArr[i] = Arrays.copyOfRange(pt[i], 0, pt[i].length);
        }

        preparedArr = PixelTableColorReductor.reduceColorsFromPixelTable(preparedArr);
        preparedArr = BWHistogramEqualizer.correctHistogram(preparedArr);
        preparedArr = PixelTableSharpener.sharpenBWPixelTable(preparedArr);
        return preparedArr;
    }

    public void finishPrintify(View view) {
        finish();
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        TextView indicator = (TextView)findViewById(R.id.text_view_page_num);
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indicator.setText((position + 1) + " / " + printifyPagerAdapter.getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
