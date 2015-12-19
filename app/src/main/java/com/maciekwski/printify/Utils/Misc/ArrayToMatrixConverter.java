package com.maciekwski.printify.Utils.Misc;

import java.util.Arrays;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 13.12.2015.
 */
public class ArrayToMatrixConverter {
    public static int[][] convert(int[] array, int rows, int cols){
        int[][] matrix = new int[rows][cols];

        int counter = 0;
        for(int i = 0; i < array.length-cols; i+=cols, counter++){
            matrix[counter] = Arrays.copyOfRange(array,counter*cols, counter*cols+cols);
        }
        return matrix;
    }
}
