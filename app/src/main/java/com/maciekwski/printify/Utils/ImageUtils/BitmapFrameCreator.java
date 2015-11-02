package com.maciekwski.printify.Utils.ImageUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 27.10.2015.
 */
public class BitmapFrameCreator {
    public static ArrayList<Bitmap> addFramesToImages(ArrayList<Bitmap> sourceBitmaps, double ratio) {
        ArrayList<Bitmap> result = new ArrayList<>();
        for (Bitmap bitmap :
                sourceBitmaps) {
            result.add(addFrameToSingleImage(bitmap, ratio));
        }
        return result;
    }

    private static Bitmap addFrameToSingleImage(Bitmap image, double ratio){
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
        return result;
    }
}
