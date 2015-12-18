package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.maciekwski.printify.Activities.PrintifyProcess.PrintifyActivity;
import com.maciekwski.printify.Adapters.SetVerticesPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageDisposer;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.BitmapBorderCreator;
import com.maciekwski.printify.Utils.ImageUtils.BitmapResizer;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapSizeWithContentVertices;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapTransformer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VerticesSetterActivity extends FragmentActivity {
    private static final double FRAMED_TO_ORIGINAL_RATIO = 1.2;
    private VerticesSetterViewPager verticesPager;
    private PagerAdapter verticesPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;
    Point[][] allVertices;
    int height;
    int width;
    View busyIndicator;
    final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();

    @Override
    public void onBackPressed() {
        if (verticesPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            ImageDisposer.deleteImages(imagesToDisplay, getApplicationContext());
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            verticesPager.setCurrentItem(verticesPager.getCurrentItem() - 1);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busyIndicator = getLayoutInflater().inflate(R.layout.busy_indicator, null);
        this.setupProgressBar();
        setContentView(R.layout.activity_set_vertices);
        this.changeImagesAsync();
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

    public void changeImagesAsync() {
        new ResizeAndAddBordersAsyncTask().execute();
    }
    private class ResizeAndAddBordersAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        public void onPreExecute() {
            // init your dialog here;
            getWindowManager().addView(busyIndicator, windowParams);
            findViewById(R.id.button_start_printify).setClickable(false);
            imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        }

        public void onPostExecute(Integer result) {
            getWindowManager().removeView(busyIndicator);
            findViewById(R.id.button_start_printify).setClickable(true);
            allVertices = new Point[imagesToDisplay.size()][];
            preparePager();// process result;
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // do your things here
            imagesToDisplay = rescaleBitmapsIfTooBig(imagesToDisplay);
            addBordersToBitmaps(imagesToDisplay);

            return null;
        }
    }
    private ArrayList<Uri> rescaleBitmapsIfTooBig(ArrayList<Uri> imagesToDisplay) {
        return BitmapResizer.resizeBitmapsFromUris(imagesToDisplay);
    }

    private void addBordersToBitmaps(ArrayList<Uri> imagesToDisplay) {
        BitmapBorderCreator.
                addBordersToImages(
                        imagesToDisplay,
                        FRAMED_TO_ORIGINAL_RATIO,
                        getApplicationContext()
                );
    }

    private void preparePager() {
        verticesPager = (VerticesSetterViewPager) findViewById(R.id.vertices_pager);
        verticesPagerAdapter = new SetVerticesPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
        verticesPager.setAdapter(verticesPagerAdapter);
        PageChangeListener pageChangeListener = new PageChangeListener();
        verticesPager.addOnPageChangeListener(pageChangeListener);
        ((TextView)findViewById(R.id.text_view_page_num)).setText(1 + " / " + verticesPagerAdapter.getCount());

    }
    public void startPrintify(View view) {
        new PerspectiveTransformAsyncTask().execute();
    }


    private class PerspectiveTransformAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices;
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        public void onPreExecute() {
            // init your dialog here;
            getWindowManager().addView(busyIndicator, windowParams);
            findViewById(R.id.button_start_printify).setClickable(false);
            verticesPager.setPagingEnabled(false);
            bitmapsWithContentVertices = this.createBWCV();
        }

        public void onPostExecute(Integer result) {
            getWindowManager().removeView(busyIndicator);
            findViewById(R.id.button_start_printify).setClickable(true);
            Intent intent = new Intent(getApplicationContext(), PrintifyActivity.class);
            intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
            startActivity(intent);
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            this.transformAllBitmapsAndUpdateUris(bitmapsWithContentVertices);
            return null;
        }

        private ArrayList<BitmapSizeWithContentVertices> createBWCV() {
            ArrayList<RelativeVertices> relVert = new ArrayList<>();
            for(Point[] pArr : allVertices){
                relVert.add(new RelativeVertices(pArr, width, height));
            }

            BitmapFactory.Options opt = new BitmapFactory.Options();

            ArrayList<BitmapSizeWithContentVertices> result = new ArrayList<>();
            for(int i = 0; i< allVertices.length; i++){
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagesToDisplay.get(i).getPath(), opt);

                Point loadedSize = new Point(opt.outWidth, opt.outHeight);
                result.add(new BitmapSizeWithContentVertices(
                        RelativeToRealVerticesTransformer.transform(relVert.get(i),loadedSize),
                        loadedSize.x, loadedSize.y));
            }
            return result;
        }
        private void transformAllBitmapsAndUpdateUris(ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices) {
            ExecutorService executor = Executors.newFixedThreadPool(imagesToDisplay.size());
            for(int i = 0; i < imagesToDisplay.size(); i++){
                final BitmapSizeWithContentVertices b = bitmapsWithContentVertices.get(i);
                final Uri uri = imagesToDisplay.get(i);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("PerspectiveThread", "Thread: " + uri.getPath() + " joined");
                        Bitmap workerBitmap = transformSingleBitmap(b,ImageLoader.loadSingleImageFromUri(uri, getApplicationContext()));
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

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        TextView indicator = (TextView)findViewById(R.id.text_view_page_num);
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indicator.setText((position + 1) + " / " + verticesPagerAdapter.getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
