package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.maciekwski.printify.Activities.PrintifyProcess.PrintifyActivity;
import com.maciekwski.printify.Adapters.SetVerticesPagerAdapter;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageDisposer;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;
import com.maciekwski.printify.Utils.ImageUtils.BitmapBorderCreator;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapSizeWithContentVertices;
import com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool.BitmapTransformer;

import java.util.ArrayList;

public class VerticesSetterActivity extends FragmentActivity {
    private static final double FRAMED_TO_ORIGINAL_RATIO = 1.2;
    private ViewPager verticesPager;
    private PagerAdapter verticesPagerAdapter;
    private ArrayList<Uri> imagesToDisplay;
    private ArrayList<Bitmap> newBitmaps;
    Point[][] allVertices;
    int height;
    int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.changeImages();
        setContentView(R.layout.activity_set_vertices);
        allVertices = new Point[imagesToDisplay.size()][];
        this.preparePager();

    }

    private void changeImages() {
        this.imagesToDisplay = getIntent().getParcelableArrayListExtra("imageList");
        this.imagesToDisplay = this.addBordersAndSaveNewBitmapsToGetUris(this.imagesToDisplay);
        /*newBitmaps = this.loadImagesIntoNewBitmaps(); //old way killing way
        imagesToDisplay = this.saveNewImageBitmaps(newBitmaps);*/
    }

    private ArrayList<Uri> addBordersAndSaveNewBitmapsToGetUris(ArrayList<Uri> imagesToDisplay) {
        return BitmapBorderCreator.
                addBordersToImagesSaveThemToGetUris(
                        imagesToDisplay,
                        FRAMED_TO_ORIGINAL_RATIO,
                        getApplicationContext()
        );
    }

    private ArrayList<Uri> saveNewImageBitmaps(ArrayList<Bitmap> newBitmaps) {
        return ImageSaver.saveImagesReturnUris(newBitmaps);
    }
/*
    private ArrayList<Bitmap> loadImagesIntoNewBitmaps() {
        ArrayList<Bitmap> result = ImageLoader.loadImagesFromUri(imagesToDisplay, getApplicationContext());
        return BitmapBorderCreator.addBordersToImages(result, FRAMED_TO_ORIGINAL_RATIO);
    }*/

    private void preparePager() {
        verticesPager = (ViewPager) findViewById(R.id.vertices_pager);
        verticesPagerAdapter = new SetVerticesPagerAdapter(getSupportFragmentManager(), imagesToDisplay);
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
        ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices = this.createBWCV();
        this.transformAllBitmapsAndUpdateUris(bitmapsWithContentVertices);

        intent.putParcelableArrayListExtra("imageList", imagesToDisplay);
        startActivity(intent);
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

        /*ArrayList<BitmapWithContentVertices> result = new ArrayList<>();
        result.add( new BitmapWithContentVertices(this.createVertices(), this.createBitmap()));*/
        return result;
    }

    private void transformAllBitmapsAndUpdateUris(ArrayList<BitmapSizeWithContentVertices> bitmapsWithContentVertices) {
        for(int i = 0; i < imagesToDisplay.size(); i++){
            BitmapSizeWithContentVertices b = bitmapsWithContentVertices.get(i);
            Uri uri = imagesToDisplay.get(i);
            Bitmap workerBitmap = ImageLoader.loadSingleImageFromUri(uri, getApplicationContext());
            workerBitmap = transformSingleBitmap(b, workerBitmap);
            ImageSaver.saveBitmapToGivenUri(workerBitmap, uri);
            workerBitmap.recycle();
        }
    }

    private Bitmap transformSingleBitmap(BitmapSizeWithContentVertices b, Bitmap workerBitmap) {
        BitmapTransformer bt = new BitmapTransformer(b, workerBitmap);
        return bt.transformImage();
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



   /* private Point[] createVertices() {
        Point[] vert = new Point[4];
        vert[0] = new Point(100, 100);
        vert[1] = new Point(200, 100);
        vert[2] = new Point(200, 400);
        vert[3] = new Point(100, 400);
        return vert;
    }

    private Bitmap createBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(300, 500, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        Paint p = new Paint(Color.RED);
        c.drawColor(Color.WHITE);
        c.drawCircle(100, 100, 100, p);
        return bitmap;
    }*/
}
