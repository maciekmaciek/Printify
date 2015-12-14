package com.maciekwski.printify.Utils.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import com.maciekwski.printify.Utils.IO.ImageDisposer;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 27.10.2015.
 */
public class BitmapBorderCreator {
    public static ArrayList<Uri> addBordersToImagesSaveThemToGetUris(ArrayList<Uri> sourceUris, double ratio, Context context) {
        ArrayList<Uri> result = new ArrayList<>();
        Bitmap workerBitmap;

        for (Uri uri :
                sourceUris) {
            workerBitmap = ImageLoader.loadSingleImageFromUri(uri, context);
            workerBitmap = addBorderToSingleImage(workerBitmap, ratio);
            result.add(ImageSaver.saveSingleImageReturnUri(workerBitmap, result.size()));
            workerBitmap.recycle();
        }
        return result;
    }

    private static Bitmap addBorderToSingleImage(Bitmap image, double ratio){
        int oldWidth = image.getWidth();
        int oldHeight = image.getHeight();

        int newWidth = (int)(ratio*oldWidth);
        int newHeight = (int)(ratio*oldHeight);

        int offsetLeft = (int)((ratio - 1)/2 * oldWidth);
        int offsetTop = (int)((ratio - 1)/2 * oldHeight);

        RectF targetRect = new RectF(offsetLeft, offsetTop, offsetLeft + oldWidth, offsetTop + oldHeight);

        Bitmap result = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(result);

        bitmapCanvas.drawColor(Color.WHITE);
        bitmapCanvas.drawBitmap(image, null, targetRect, null);
        image = result;
        return image;
    }
}
