package com.maciekwski.printify.Utils.ImageUtils.Step2Preparation;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class PixelTableSharpener {

    static final int[][] MASK = {
            {0, -1, 0},
            {-1, 20, -1},
            {-0, -1, 0}
    };
    static final int NORM = 4;
    public static int[][] sharpenBWPixelTable(int[][] preparedArr) {
        int resultArr[][] = new int[preparedArr.length][preparedArr[0].length];
        Log.d("Size", preparedArr.length + " " + preparedArr[0].length);
        resultArr[0] = Arrays.copyOfRange(preparedArr[0],0, preparedArr[0].length);
        resultArr[preparedArr.length-1] = Arrays.copyOfRange(preparedArr[preparedArr.length-1],0, preparedArr[0].length);

        for(int row = 1; row < resultArr.length - 1; row++){
            for(int col = 0; col < resultArr[0].length ; col++) {
                if(col == 0 || col == resultArr[0].length -1){
                    resultArr[row][col] = preparedArr[row][col];
                } else {
                    resultArr[row][col] = filterSubMatrix(
                            createSubmatrix(preparedArr, row-1, col-1, 3),
                            MASK);
                }
            }
        }
        return resultArr;
    }

    private static int filterSubMatrix(int[][] submatrix, int[][] mask) {
        int normalizer = NORM;//calculateNormalizer(mask);
        int filtered = 0;
        for(int row = 0; row<mask.length; row++){
            for(int col = 0; col < mask[0].length; col++){
                filtered+=mask[row][col]*submatrix[row][col];
            }
        }
        filtered = filtered/normalizer;
        filtered = filtered < 0 ? 0:filtered;
        filtered = filtered > 255 ? 255:filtered;
        return filtered;
    }

    private static int calculateNormalizer(int[][] mask) {
        int normalizer = 0;
        for (int[] aMask : mask) {
            for (int anAMask : aMask) {
                normalizer += anAMask;
            }
        }
        return normalizer;
    }

    private static int[][] createSubmatrix(int[][] matrix, int row, int col, int size) {
        int[][] submatrix = new int[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                submatrix[i][j] = matrix[row+i][col+j];
            }
        }
        return submatrix;
    }
}
