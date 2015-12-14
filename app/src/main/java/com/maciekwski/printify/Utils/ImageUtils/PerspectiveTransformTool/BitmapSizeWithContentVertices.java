package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 02.11.2015.
 */
public class BitmapSizeWithContentVertices {
    Point[] contentVertices;
    int width;
    int height;

    public BitmapSizeWithContentVertices(Point[] contentVertices, int width, int height) {
        this.contentVertices = contentVertices;
        this.width = width;
        this.height = height;
    }
}
