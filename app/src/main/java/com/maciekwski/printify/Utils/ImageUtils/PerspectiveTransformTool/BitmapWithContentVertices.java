package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 02.11.2015.
 */
public class BitmapWithContentVertices {
    Bitmap bitmap;
    Point[] contentVertices;

    public BitmapWithContentVertices(Point[] contentVertices, Bitmap bitmap) {
        this.contentVertices = contentVertices;
        this.bitmap = bitmap;
    }
}
