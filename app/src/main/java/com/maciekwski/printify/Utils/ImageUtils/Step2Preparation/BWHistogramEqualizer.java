package com.maciekwski.printify.Utils.ImageUtils.Step2Preparation;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class BWHistogramEqualizer {
    final static int LOCAL = 10;
    private static int[][] createSubmatrix(int[][] matrix, int row, int col, int rowsize, int colsize) {
        int[][] submatrix = new int[rowsize][colsize];
        for(int i = 0; i < rowsize; i++){
            for(int j = 0; j < colsize; j++){
                submatrix[i][j] = matrix[row+i][col+j];
            }
        }
        return submatrix;
    }

    public static int[][] correctHistogram(int [][] colorValues){
        int localRows = colorValues.length/LOCAL;
        int localCols = colorValues[0].length/LOCAL;
        int[][][][] localColorValues = partitionColorValues(colorValues, localRows, localCols);
        for (int[][][] partition: localColorValues) {
            for(int[][]valuesRect : partition){
                valuesRect = correctLocalHistogram(valuesRect);
            }
        }
        colorValues = connectValuesRects(colorValues,localColorValues, localRows, localCols);
        return colorValues;
    }

    private static int[][] connectValuesRects(int[][] colorValues, int[][][][] localColorValues, int localRows, int localCols) {
        for(int bigrow = 0; bigrow < LOCAL; bigrow++){
            for(int bigcol = 0; bigcol < LOCAL; bigcol++){
                for(int smallrow = 0; smallrow< localRows; smallrow++){
                    for(int smallcol = 0; smallcol < localCols; smallcol++){
                        colorValues[bigrow*localRows + smallrow][bigcol*localCols +smallcol] = localColorValues[bigrow][bigcol][smallrow][smallcol];
                    }
                }
            }
        }
        return colorValues;
    }

    private static int[][] correctLocalHistogram(int[][] colorValues) {
        int[] fullDistribution = calculateDistribution(colorValues);
        int[] newColorTable = new int[256];
        int numOfPixels = colorValues.length*colorValues[0].length;

        setNewColorValues(newColorTable, fullDistribution, numOfPixels);
        applyNewColorValues(colorValues, newColorTable);
        return colorValues;
    }

    private static int[][][][] partitionColorValues(int[][] colorValues, int localRows, int localCols) {
        int [][][][] result = new int[LOCAL][LOCAL][localRows][localCols];
        for(int i = 0; i<LOCAL; i++){
            for(int j = 0; j < LOCAL; j++){
                result[i][j] = createSubmatrix(colorValues, localRows*i, localCols*j, localRows, localCols);
            }
        }
        return result;
    }

    private static void setNewColorValues(int[] newColorValues, int[] fullDistribution, int numOfPixels) {
        int mindistr = 0;
        boolean isZero = true;
        for(int i = 0; i<fullDistribution.length && isZero; i++){
            if((mindistr = fullDistribution[i]) != 0){
                isZero = false;
            }
        }
        for(int i = 0; i < 256; i++){
            newColorValues[i] = adjustColorValue(fullDistribution[i], mindistr, 256, numOfPixels);
        }
    }

    private static int adjustColorValue(int distribuant, int mindistribuant, int numOfColors, int numOfPixels) {
        return (int)
                ((float)(distribuant - mindistribuant)/
                        (numOfPixels-mindistribuant)*
                        numOfColors);
    }

    private static void applyNewColorValues(int[][] colorValues, int[] newColorValues) {
        int height = colorValues[0].length;
        for (int[] colorRow : colorValues) {
            for (int y = 0; y < height; y++) {
                colorRow[y] = newColorValues[colorRow[y]];
            }
        }
    }

    private static int[] calculateDistribution(int[][] colorValues) {
        int[] distribution = new int[256];
        int height = colorValues[0].length;
        for (int[] colorRow : colorValues) {
            for (int y = 0; y < height; y++) {
                int newPixelValue = colorRow[y];
                add1ToDistribution(newPixelValue, distribution);
            }
        }
        return distribution;
    }

    private static void add1ToDistribution(int startingIndex, int[] distribution){
        for (int i = startingIndex; i < distribution.length; i++) {
            distribution[i]++;
        }
    }
}
