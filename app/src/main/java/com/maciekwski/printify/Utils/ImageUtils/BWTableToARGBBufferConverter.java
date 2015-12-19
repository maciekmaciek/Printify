package com.maciekwski.printify.Utils.ImageUtils;

import android.graphics.Color;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class BWTableToARGBBufferConverter {
    public static int[] convertPixelTable(int[][] ppt) {
        int rows = ppt.length;
        int cols = ppt[0].length;
        int[] buffer = new int[rows*cols];
        for(int row = 0; row < rows; row++){
            for(int col = 0; col<cols; col++){
                buffer[row*cols + col] = convertIntToARGB(ppt[row][col]);
            }
        }
        return buffer;
    }

    private static int convertIntToARGB(int intval) {
        return Color.argb(255,intval, intval, intval);
    }
}
