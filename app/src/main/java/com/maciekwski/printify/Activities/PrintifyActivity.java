package com.maciekwski.printify.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.ImageUtils.ImageBinarizingTool.BitmapBinarizer;
import com.maciekwski.printify.Utils.ImageUtils.ImageBinarizingTool.HsvHistogramCorrector;
import com.maciekwski.printify.Utils.ImageUtils.ImageBinarizingTool.ImageJoiner;

public class PrintifyActivity extends ActionBarActivity {

    Bitmap imageToTransform;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_printify);
        imageToTransform = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.testimage));
        transformAllImages();
    }

    private void transformAllImages() {
        transformSingleImage(imageToTransform);

    }

    private void transformSingleImage(Bitmap imageToTransform) {
        binarizeImage(imageToTransform);
        Point[][]  vertices = getUsefulVertices(imageToTransform);
        transformCutImageToRectangle(vertices, imageToTransform);
    }

    private void binarizeImage(Bitmap imageToTransform) {
        Bitmap penColorsBitmap = imageToTransform.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap correctedHistogramBitmap;
        BitmapBinarizer.binarizeBitmapPenColors(penColorsBitmap);
        correctedHistogramBitmap = HsvHistogramCorrector.correctHsvHistogram(imageToTransform);
        BitmapBinarizer.binarizeBitmapCleanBackground(correctedHistogramBitmap);

        imageToTransform = joinBinarizedImages(penColorsBitmap, correctedHistogramBitmap);
    }

    private Bitmap joinBinarizedImages(Bitmap penColorsBitmap, Bitmap correctedHistogramBitmap) {
        return ImageJoiner.joinBlackAndWhiteImages(penColorsBitmap, correctedHistogramBitmap);
    }

    private Point[][] getUsefulVertices(Bitmap imageToTransform) {
        //TODO
        return null;
    }

    private void transformCutImageToRectangle(Point[][] vertices, Bitmap imageToTransform) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_printify, menu);
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
}
