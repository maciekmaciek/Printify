package com.maciekwski.printify.Activities.VerticesSetter;

import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 02.11.2015.
 */
public class RelativeVertices {
    Point[] vertices;
    int width;
    int height;

    public RelativeVertices(Point[] vertices, int width, int height) {
        this.vertices = vertices;
        this.width = width;
        this.height = height;
    }
}
