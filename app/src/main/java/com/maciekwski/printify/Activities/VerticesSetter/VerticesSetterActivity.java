package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.maciekwski.printify.Activities.PrintifyProcess.PrintifyActivity;
import com.maciekwski.printify.Adapters.SetVerticesPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageDisposer;

import java.util.ArrayList;

public class VerticesSetterActivity extends FragmentActivity {
    VerticesSetterViewPager verticesPager;
    private PagerAdapter verticesPagerAdapter;
    ArrayList<Uri> imagesToDisplay;
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


    private void setupProgressBar() {
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
        imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        new ResizeAndAddBordersAsyncTask(getApplicationContext(), imagesToDisplay, this).execute();
    }


    void preparePager() {
        verticesPager = (VerticesSetterViewPager) findViewById(R.id.vertices_pager);
        verticesPagerAdapter = new SetVerticesPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
        verticesPager.setAdapter(verticesPagerAdapter);
        PageChangeListener pageChangeListener = new PageChangeListener();
        verticesPager.addOnPageChangeListener(pageChangeListener);
        ((TextView) findViewById(R.id.text_view_page_num)).setText(1 + " / " + verticesPagerAdapter.getCount());

    }

    public void startPrintify(View view) {
        new PerspectiveTransformAsyncTask(getApplicationContext(), imagesToDisplay, this, width, height, allVertices).execute();
    }

    public void onPostExecute() {
        Intent intent = new Intent(getApplicationContext(), PrintifyActivity.class);
        intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
        startActivity(intent);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        TextView indicator = (TextView) findViewById(R.id.text_view_page_num);

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
