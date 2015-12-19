package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.ImageUtils.BitmapBorderCreator;
import com.maciekwski.printify.Utils.ImageUtils.BitmapResizer;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 16.12.2015.
 */

public class ResizeAndAddBordersAsyncTask extends AsyncTask<Integer, Integer, Integer> {
    private ArrayList<Uri> imagesToDisplay;
    private Context context;
    private VerticesSetterActivity activity;
    View busyIndicator;
    final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    private static final double FRAMED_TO_ORIGINAL_RATIO = 1.2;

    public ResizeAndAddBordersAsyncTask(Context context, ArrayList<Uri> imagesToDisplay, VerticesSetterActivity activity) {
        super();
        this.activity = activity;
        this.imagesToDisplay = imagesToDisplay;
        this.context = context;
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
        //activity.verticesPager.setPagingEnabled(false);
        activity.findViewById(R.id.button_start_printify).setClickable(false);
    }

    @Override
    public void onPostExecute(Integer result) {
        unlockLayout();
        activity.allVertices = new Point[imagesToDisplay.size()][];
        activity.imagesToDisplay = this.imagesToDisplay;
        activity.preparePager();
    }

    private void unlockLayout() {
        activity.getWindowManager().removeView(busyIndicator);
        activity.findViewById(R.id.button_start_printify).setClickable(true);
    }

    @Override
    protected Integer doInBackground(Integer... arg0) {
        imagesToDisplay = rescaleBitmapsIfTooBig(imagesToDisplay);
        addBordersToBitmaps(imagesToDisplay);

        return null;
    }

    private ArrayList<Uri> rescaleBitmapsIfTooBig(ArrayList<Uri> imagesToDisplay) {
        return BitmapResizer.resizeBitmapsFromUris(imagesToDisplay);
    }

    private void addBordersToBitmaps(ArrayList<Uri> imagesToDisplay) {
        BitmapBorderCreator.
                addBordersToImages(
                        imagesToDisplay,
                        FRAMED_TO_ORIGINAL_RATIO,
                        context
                );

    }
}
