package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.maciekwski.printify.Activities.PrintifyActivity;
import com.maciekwski.printify.Adapters.VerticesSetterPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageDisposer;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.BitmapFrameCreator;

import java.util.ArrayList;

public class VerticesSetterActivity extends FragmentActivity {
    private static final double FRAMED_TO_ORIGINAL_RATIO = 1.2;
    private ViewPager verticesPager;
    private PagerAdapter verticesPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.changeImages();
        setContentView(R.layout.activity_set_vertices);
        this.preparePager();

    }

    private void changeImages() {
        this.imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        ArrayList<Bitmap> newBitmaps = this.loadImagesIntoNewBitmaps();
        imagesToDisplay = this.saveNewImageBitmaps(newBitmaps);
    }

    private ArrayList<Uri> saveNewImageBitmaps(ArrayList<Bitmap> newBitmaps) {
        return ImageSaver.saveImagesReturnUris(newBitmaps);
    }

    private ArrayList<Bitmap> loadImagesIntoNewBitmaps() {
        ArrayList<Bitmap> result = ImageLoader.loadImagesFromUri(imagesToDisplay, getApplicationContext());
        return BitmapFrameCreator.addFramesToImages(result, FRAMED_TO_ORIGINAL_RATIO);
    }

    private void preparePager() {
        verticesPager = (ViewPager) findViewById(R.id.vertices_pager);
        verticesPagerAdapter = new VerticesSetterPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
        verticesPager.setAdapter(verticesPagerAdapter);
        PageChangeListener pageChangeListener = new PageChangeListener();
        verticesPager.addOnPageChangeListener(pageChangeListener);
        ((TextView)findViewById(R.id.text_view_page_num)).setText(1 + " / " +verticesPagerAdapter.getCount());

    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_vertices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startPrintify(View view) {
        //TODO add options
        Intent intent = new Intent(getApplicationContext(), PrintifyActivity.class);
        intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
        startActivity(intent);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        TextView indicator = (TextView)findViewById(R.id.text_view_page_num);
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            indicator.setText((position+1) + " / " + verticesPagerAdapter.getCount());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
