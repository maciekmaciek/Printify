package com.maciekwski.printify.Utils.ImageUtils.Step1PerspectiveTransform;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 15.10.2015.
 */
public class BilinearInterpolator {

    public static int interpolate(double x, double y, int a, int b, int c, int d) {
        int i = (int) x, j = (int) y;
        double alpha = x - i, beta = y - j;

        double Xab = alpha * b + (1 - alpha) * a;
        double Xcd = alpha * d + (1 - alpha) * c;

        return (int) Math.round(beta * Xcd + (1 - beta) * Xab);
    }
}
