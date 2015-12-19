package com.maciekwski.printify.Utils.ImageUtils.Step3Joining;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 23.09.2015.
 */
public class ImageJoiner {
    private static boolean isBlack(int color) {
        return color == Color.BLACK;
    }

    public static int[][] takeDarkest(int[][] locallyThresholdedPixels, int[][] detectedPenStrokesPixels, int[][] edgeDetectedPixles) {

        int rows = locallyThresholdedPixels.length;
        int cols = locallyThresholdedPixels[0].length;
        int[][] result = new int[rows][cols];
        for(int row = 0; row < rows; row++){
            for(int col = 0; col <cols; col++){
                result[row][col] = minOf3(locallyThresholdedPixels[row][col],detectedPenStrokesPixels[row][col], edgeDetectedPixles[row][col]);
            }
        }
        return result;
    }

    public static int minOf3(int a, int b, int c){
        return Math.min(a, Math.min(b,c));
    }
}
