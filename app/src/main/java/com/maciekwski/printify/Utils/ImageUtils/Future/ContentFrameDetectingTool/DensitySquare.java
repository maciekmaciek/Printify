package com.maciekwski.printify.Utils.ImageUtils.Future.ContentFrameDetectingTool;

import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 06.10.2015.
 */
public class DensitySquare {
    private Point topLeft;
    private double density;
    private int length;

    public DensitySquare(int length, Point topLeft) {
        this.length = length;
        this.topLeft = topLeft;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getTopRight() {
        return new Point(topLeft.x + length, topLeft.y);
    }

    public Point getBottomLeft() {
        return new Point(topLeft.x, topLeft.y + length);
    }

    public Point getBottomRight() {
        return new Point(topLeft.x + length, topLeft.y + length);
    }

    public Point getCenterLeft() {
        return new Point(topLeft.x, topLeft.y + length / 2);
    }

    public Point getCenterRight() {
        return new Point(topLeft.x + length, topLeft.y + length / 2);
    }

    public int getLength() {
        return length;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getDensity() {
        return density;
    }

    public boolean isOverThreshold(double givenThreshold) {
        return this.density >= givenThreshold;
    }

    public boolean isRightTo(DensitySquare candidate) {
        return this.topLeft.x > candidate.getTopLeft().x;
    }
}
