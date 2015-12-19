package com.maciekwski.printify.Utils.ImageUtils.Step2PenLines;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 20.09.2015.
 */
public class PenLineBinarizer {
    static final int RED = Color.rgb(200, 0, 0);
    static final int DARK_BLUE = Color.rgb(0, 0, 150);
    static final int DARK = Color.rgb(35, 35, 35);

    public static int[][] binarizeByPenColor(int[][] pixelTable) {
        int width = pixelTable.length;
        int height = pixelTable[0].length;
        int[][] resultPixels = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = pixelTable[x][y];
                if (PenLineBinarizer.isRed(pixelColor) || PenLineBinarizer.isDarkBlue(pixelColor) || PenLineBinarizer.isDark(pixelColor)) {
                    resultPixels[x][y] = 0;
                } else {
                    resultPixels[x][y] = 255;
                }
            }
        }
        return resultPixels;
    }



    private static boolean isDarkBlue(int pixelColor) {
        return areCloseColors(DARK_BLUE, pixelColor);
    }

    private static boolean isDark(int pixelColor) {
        return areCloseColors(DARK, pixelColor);
    }

    private static boolean isRed(int pixelColor) {
        return areCloseColors(RED, pixelColor);
    }

    private static boolean areCloseColors(int first, int second) {
        return redClose(first, second) && greenClose(first, second) && blueClose(first, second);
    }

    private static boolean redClose(int first, int second) {
        return Math.abs(Color.red(first) - Color.red(second)) <= 35;
    }

    private static boolean greenClose(int first, int second) {
        return Math.abs(Color.green(first) - Color.green(second)) <= 35;
    }

    private static boolean blueClose(int first, int second) {
        return Math.abs(Color.blue(first) - Color.blue(second)) <= 35;
    }
}
