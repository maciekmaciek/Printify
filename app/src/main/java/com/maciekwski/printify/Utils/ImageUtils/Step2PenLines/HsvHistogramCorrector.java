package com.maciekwski.printify.Utils.ImageUtils.Step2PenLines;

import android.graphics.Color;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 22.09.2015.
 */
public class HsvHistogramCorrector {

    public static int[][] correctHsvHistogram(int[][] pixelTable) {
        int width = pixelTable.length;
        int height = pixelTable[0].length;
        int[][] resultPixels = new int[width][height];
        float[][][] hsvTable = buildHsvTable(pixelTable);
        correctHistogram(hsvTable);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                resultPixels[x][y] = Color.HSVToColor(hsvTable[x][y]);
            }
        }
        return resultPixels;
    }

    private static float[][][] buildHsvTable(int[][] pixelTable) {
        int width = pixelTable.length;
        int height = pixelTable[0].length;
        float[][][] colorTable = new float[width][height][3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color.colorToHSV(pixelTable[x][y], colorTable[x][y]);
                colorTable[x][y][2] = roundTwoDecimals(colorTable[x][y][2]);
            }
        }
        return colorTable;
    }

    private static float roundTwoDecimals(float f) {
        String.format(Locale.US, "%f", 3.141592);
        DecimalFormat twoDForm = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
        return Float.valueOf(twoDForm.format(f));
    }

    private static void correctHistogram(float[][][] colorValues) {
        int[] fullDistribution = calculateVDistribution(colorValues);
        float[] newColorValues = new float[100];
        int numOfPixels = colorValues.length * colorValues[0].length;

        setNewColorValues(newColorValues, fullDistribution, numOfPixels);
        applyNewColorValues(colorValues, newColorValues);
    }

    private static void setNewColorValues(float[] newColorValues, int[] fullDistribution, int numOfPixels) {
        for (int i = 0; i < 100; i++) {
            newColorValues[i] = adjustColorValue(fullDistribution[i], 100, numOfPixels);
        }
    }

    private static float adjustColorValue(int distribuant, int numOfColors, int numOfPixels) {
        return (float) Math.round((float) ((distribuant - 1) / numOfPixels) * numOfColors) / 100;
    }

    private static void applyNewColorValues(float[][][] colorValues, float[] newColorValues) {
        int height = colorValues[0].length;
        for (float[][] colorRow : colorValues) {
            for (int y = 0; y < height; y++) {
                colorRow[y][2] = newColorValues[(int) (colorRow[y][2] * 100)];
            }
        }
    }

    private static int[] calculateVDistribution(float[][][] colorValues) {
        int[] distribution = new int[100];
        int height = colorValues[0].length;
        for (float[][] colorRow : colorValues) {
            for (int y = 0; y < height; y++) {
                int newPixelValue = (int) (colorRow[y][2] * 100);
                add1ToDistribution(newPixelValue, distribution);
            }
        }
        return distribution;
    }

    private static void add1ToDistribution(int startingIndex, int[] distribution) {
        for (int i = startingIndex; i < distribution.length; i++) {
            distribution[i]++;
        }
    }
}
