package com.maciekwski.printify.Utils.ImageUtils.ImageBinarizingTool;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.text.DecimalFormat;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 22.09.2015.
 */
public class HsvHistogramCorrector {

    public static Bitmap correctHsvHistogram(Bitmap bitmap){
        Bitmap correctedBitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[][][] hsvTable = bluildHsvTable(bitmap);
        correctHistogram(hsvTable);
        correctedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                correctedBitmap.setPixel(x, y, Color.HSVToColor(hsvTable[x][y]));
            }
        }
        return correctedBitmap;
    }

    private static float[][][] bluildHsvTable(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[][][] colorTable = new float[width][height][3];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Color.colorToHSV(bitmap.getPixel(x, y), colorTable[x][y]);
                colorTable[x][y][2] = roundTwoDecimals(colorTable[x][y][2]);
            }
        }
        return colorTable;
    }

    private static float roundTwoDecimals(float f) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Float.valueOf(twoDForm.format(f));
    }

    private static void correctHistogram(float [][][] colorValues){
        int[] fullDistribution = calculateVDistribution(colorValues);
        float[] newColorValues = new float[100];
        int numOfPixels = colorValues.length*colorValues[0].length;

        setNewColorValues(newColorValues, fullDistribution, numOfPixels);
        applyNewColorValues(colorValues, newColorValues);
    }

    private static void setNewColorValues(float[] newColorValues, int[] fullDistribution, int numOfPixels) {
        for(int i = 0; i < 100; i++){
            newColorValues[i] = adjustColorValue(fullDistribution[i], 100, numOfPixels);
        }
    }

    private static float adjustColorValue(int distribuant, int numOfColors, int numOfPixels) {
        return (float)Math.round((float)((distribuant - 1)/numOfPixels) * numOfColors)/100;
    }

    private static void applyNewColorValues(float[][][] colorValues, float[] newColorValues) {
        int height = colorValues[0].length;
        for (float[][] colorRow : colorValues) {
            for (int y = 0; y < height; y++) {
                colorRow[y][2] = newColorValues[(int)(colorRow[y][2] * 100)];
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

    private static void add1ToDistribution(int startingIndex, int[] distribution){
        for (int i = startingIndex; i < distribution.length; i++) {
            distribution[i]++;
        }
    }
}
