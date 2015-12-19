package com.maciekwski.printify.Utils.ImageUtils.Step2LocalThresholding;

import java.util.Arrays;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class BWLocalThresholder {
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

    public static int[][] threshold(int[][] colorValues){
        int [][] newColorValues;
        int localRows = colorValues.length/LOCAL;
        int localCols = colorValues[0].length/LOCAL;
        int globalAvg = calculateAvg(colorValues);
        int[][][][] localColorValues = partitionColorValues(colorValues, localRows, localCols);
        for (int[][][] partition: localColorValues) {
            for(int[][]valuesRect : partition){
                valuesRect = thresholdLocally(valuesRect, globalAvg);
            }
        }
        newColorValues = connectValuesRects(colorValues,localColorValues, localRows, localCols);
        return eliminateNonNeighbours(newColorValues);

    }

    private static int[][] eliminateNonNeighbours(int[][] colorValues) {
        int rows = colorValues.length;
        int cols = colorValues[0].length;
        int[][] newMatrix = new int[rows][cols];

        int counter = 0;
        for(int[] array: colorValues){
            newMatrix[counter++] = Arrays.copyOfRange(array, 0, array.length);
        }
        for(int row = 1; row < colorValues.length - 1; row++){
            for(int col = 1; col < colorValues[0].length - 1 ; col++) {
                if(colorValues[row][col] != 0 && colorValues[row][col] != 255){
                    if(     colorValues[row-1][col] != 0 &&
                            colorValues[row+1][col] != 0 &&
                            colorValues[row][col+1] != 0 &&
                            colorValues[row][col-1] != 0){
                        newMatrix[row][col] = 255;
                    }
                }
            }
        }
        return newMatrix;
    }

    private static int calculateAvg(int[][] colorValues) {
        long counter = 0;
        for(int[]rows: colorValues){
            for(int pixel: rows){
                counter += pixel;
            }
        }
        return (int)(counter/(colorValues.length*colorValues[0].length));
    }

    private static int[][] connectValuesRects(int[][] colorValues, int[][][][] localColorValues, int localRows, int localCols) {
        int[][] newValues = new int[colorValues.length][colorValues[0].length];
        for(int bigrow = 0; bigrow < LOCAL; bigrow++){
            for(int bigcol = 0; bigcol < LOCAL; bigcol++){
                for(int smallrow = 0; smallrow< localRows; smallrow++){
                    for(int smallcol = 0; smallcol < localCols; smallcol++){
                        newValues[bigrow*localRows + smallrow][bigcol*localCols +smallcol] = localColorValues[bigrow][bigcol][smallrow][smallcol];
                    }
                }
            }
        }
        return newValues;
    }

    private static int[][] thresholdLocally(int[][] colorValues, int globalAvg) {
        int localAvg = calculateAvg(colorValues);
        for(int row = 0; row < colorValues.length; row++){
            for(int col = 0; col < colorValues[0].length; col++){
                if(isUnderThreshold(colorValues[row][col], globalAvg, localAvg)){
                    colorValues[row][col] = 0;
                } else {
                   ; //colorValues[row][col] = 255;
                }
            }
        }
        return colorValues;
    }

    private static boolean isUnderThreshold(int color, int globalAvg, int localAvg) {
        return color < globalAvg -20 && color < localAvg - 20;
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

}
