package com.maciekwski.printify.Activities.PrintifyProcess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.maciekwski.printify.Adapters.PrintifyPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.ArrayToMatrixConverter;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.BWHistogramEqualizer;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.BWTableToARGBBufferConverter;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableColorReductor;
import com.maciekwski.printify.Utils.ImageUtils.Step2Preparation.PixelTableSharpener;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class PrintifyActivity extends ActionBarActivity {

    private ViewPager printifyPager;
    private PagerAdapter printifyPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;
    private ArrayList<int[][]> pixelTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.changeImages();
        setContentView(R.layout.activity_printify);
        this.preparePager();

    }

    private void changeImages() {
        this.imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        pixelTables = new ArrayList<>();
    }
    private void preparePager() {
        printifyPager = (ViewPager) findViewById(R.id.printify_pager);
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

    public void continuePrintify(View view) {
        Intent intent = new Intent(getApplicationContext(), PostPrintifyActivity.class);
        this.createPixelTables();
        ArrayList<int[][]> printifiedPixelTables = this.printifyPixelTables();
        this.savePixelTablesToBitmapUris(printifiedPixelTables);
        intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
        startActivity(intent);
    }

    private void createPixelTables() {
        for(Uri uri: this.imagesToDisplay){
            Bitmap workerBitmap = ImageLoader.loadSingleImageFromUri(uri,getApplicationContext());
            int width = workerBitmap.getWidth();
            int height = workerBitmap.getHeight();
            int[] intArray = new int[width*height];
            workerBitmap.getPixels(intArray, 0, width, 0, 0, width, height);
            workerBitmap.recycle();
            this.pixelTables.add(ArrayToMatrixConverter.convert(intArray, width, height));
        }
    }

    private ArrayList<int[][]> printifyPixelTables() {
        ArrayList<int[][]> finalPixelTables = new ArrayList<>();
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
            saveSinglePixelTableToGivenUri(ppt, uri, i);
        }
    }

    private void saveSinglePixelTableToGivenUri(int[][] ppt, Uri uri, int urinum) {
        Bitmap resultBitmap = Bitmap.createBitmap(ppt.length, ppt[0].length, Bitmap.Config.ARGB_8888);
        int[] pixelBuffer = BWTableToARGBBufferConverter.convertPixelTable(ppt);
        resultBitmap.setPixels(pixelBuffer,0,ppt.length,0,0,ppt.length, ppt[0].length);
        imagesToDisplay.set(urinum, ImageSaver.saveSingleImageReturnUri(resultBitmap, urinum));
        //ImageSaver.saveBitmapToGivenUri(resultBitmap, uri);
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
