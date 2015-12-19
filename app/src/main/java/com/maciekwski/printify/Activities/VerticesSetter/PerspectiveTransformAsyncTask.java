package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.Step1PerspectiveTransform.BitmapSizeWithContentVertices;
import com.maciekwski.printify.Utils.ImageUtils.Step1PerspectiveTransform.BitmapTransformer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 16.12.2015.
 */
public class PerspectiveTransformAsyncTask extends AsyncTask<Integer, Integer, Integer> {

    ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices;
    private ArrayList<Uri> imagesToDisplay;
    private Context context;
    private VerticesSetterActivity activity;
    View busyIndicator;
    final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    private int width;
    private int height;
    private Point[][] allVertices;

    public PerspectiveTransformAsyncTask(Context context, ArrayList<Uri> imagesToDisplay, VerticesSetterActivity activity, int width, int height, Point[][] allVertices) {
        super();
        this.activity = activity;
        this.imagesToDisplay = imagesToDisplay;
        this.context = context;
        this.width = width;
        this.height = height;
        this.allVertices = allVertices;
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
        activity.verticesPager.setPagingEnabled(false);
        activity.findViewById(R.id.button_start_printify).setClickable(false);
        bitmapsWithContentVertices = this.createBWCV();
    }

    @Override
    public void onPostExecute(Integer result) {
        activity.onPostExecute();
    }

    @Override
    protected Integer doInBackground(Integer... arg0) {
        this.transformAllBitmapsAndUpdateUris(bitmapsWithContentVertices);
        return null;
    }

    private ArrayList<BitmapSizeWithContentVertices> createBWCV() {
        ArrayList<RelativeVertices> relVert = new ArrayList<>();
        for (Point[] pArr : allVertices) {
            relVert.add(new RelativeVertices(pArr, width, height));
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();

        ArrayList<BitmapSizeWithContentVertices> result = new ArrayList<>();
        for (int i = 0; i < allVertices.length; i++) {
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagesToDisplay.get(i).getPath(), opt);

            Point loadedSize = new Point(opt.outWidth, opt.outHeight);
            result.add(new BitmapSizeWithContentVertices(
                    RelativeToRealVerticesTransformer.transform(relVert.get(i), loadedSize),
                    loadedSize.x, loadedSize.y));
        }
        return result;
    }

    private void transformAllBitmapsAndUpdateUris(ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices) {
        ExecutorService executor = Executors.newFixedThreadPool(imagesToDisplay.size());
        for (int i = 0; i < imagesToDisplay.size(); i++) {
            final BitmapSizeWithContentVertices b = bitmapsWithContentVertices.get(i);
            final Uri uri = imagesToDisplay.get(i);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("PerspectiveThread", "Thread: " + uri.getPath() + " joined");
                    Bitmap workerBitmap = transformSingleBitmap(b, ImageLoader.loadSingleImageFromUri(uri, context));
                    ImageSaver.saveBitmapToGivenUri(workerBitmap, uri);
                    workerBitmap.recycle();
                    Log.d("PerspectiveThread", "Thread: " + uri.getPath() + " finished");
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
            Log.d("PerspectiveThread", "Thread: All threads done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Bitmap transformSingleBitmap(BitmapSizeWithContentVertices b, Bitmap workerBitmap) {
        BitmapTransformer bt = new BitmapTransformer(b, workerBitmap);
        return bt.transformImage();
    }
}