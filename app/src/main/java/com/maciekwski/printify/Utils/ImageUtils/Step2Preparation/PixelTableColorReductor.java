package com.maciekwski.printify.Utils.ImageUtils.Step2Preparation;

import android.graphics.Color;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class PixelTableColorReductor {
    static final double R_VAL = 0.21;
    static final double G_VAL = 0.72;
    static final double B_VAL = 0.07;

    public static int[][] reduceColorsFromPixelTable(int[][] pixelTable){
        for(int i = 0; i < pixelTable.length; i++){
            for(int j = 0; j < pixelTable[0].length; j++){
                pixelTable[i][j] = reducePixelColor(pixelTable[i][j]);
            }
        }
        return pixelTable;
    }

    private static int reducePixelColor(int i) {
        return (int)
                ((double) Color.red(i) * R_VAL +
                        (double) Color.green(i) * G_VAL +
                        (double) Color.blue(i) * B_VAL);
    }
}
