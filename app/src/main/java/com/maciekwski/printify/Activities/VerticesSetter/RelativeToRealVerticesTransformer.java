package com.maciekwski.printify.Activities.VerticesSetter;

import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 02.11.2015.
 */
public class RelativeToRealVerticesTransformer {
    public static Point[] transform(RelativeVertices relV, Point realSize) {
        Point[] result = new Point[4];
        double heightRatio = (double) realSize.y / relV.height;
        double widthRatio = (double) realSize.x / relV.width;

        for (int i = 0; i < 4; i++) {
            result[i] = new Point((int) (widthRatio * relV.vertices[i].x), (int) (heightRatio * relV.vertices[i].y));
        }

        return result;
    }
}
