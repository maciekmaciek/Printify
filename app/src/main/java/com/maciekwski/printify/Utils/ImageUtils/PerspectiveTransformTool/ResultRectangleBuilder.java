package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 14.10.2015.
 */
public class ResultRectangleBuilder {
    private final static double RECTANGLE_RATIO = 0.75;

    public static Point[] buildFromVertices(Point[] vertices){
        int width = Math.max(calculateDistance(vertices[0], vertices[1]), calculateDistance(vertices[3], vertices[2]));
        int height = calculateHeight(width);
        Point[] result = createVertices(width, height);
        return result;
    }

    private static Point[] createVertices(int width, int height) {
        Point[] result = new Point[4];
        result[0] = new Point(0,0);
        result[1] = new Point(width, 0);
        result[2] = new Point(width, height);
        result[3] = new Point(0, height);
        return result;
    }

    private static int calculateDistance(Point topLeft, Point topRight) {
        int x1 = topLeft.x;
        int x2 = topRight.x;
        int y1 = topLeft.y;
        int y2 = topRight.y;
        return (int)Math.round(Math.sqrt((x2 -= x1) * x2 + (y2 -= y1) * y2));
    }

    private static int calculateHeight(int width) {
        return (int)Math.round(width / RECTANGLE_RATIO);
    }
}
