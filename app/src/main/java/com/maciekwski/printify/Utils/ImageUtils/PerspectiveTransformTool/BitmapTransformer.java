package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import Jama.Matrix;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 15.10.2015.
 */
public class BitmapTransformer {
    private Bitmap imageToTransform;
    private Point[] contentVertices;
    private Point[] finalVertices;
    private Matrix transformMatrix;

    public BitmapTransformer(Bitmap imageToTransform, Point[] contentVertices){
        this.imageToTransform = imageToTransform;
        this.contentVertices = contentVertices;
        this.finalVertices = ResultRectangleBuilder.buildFromVertices(contentVertices);
        TransformMatrixBuilder transformMatrixBuilder = new TransformMatrixBuilder(contentVertices, finalVertices);
        this.transformMatrix = transformMatrixBuilder.generateTransformMatrix();
    }

    public Bitmap transformImage() throws Exception{
        Bitmap resultBitmap = this.createNewSizedBitmap();
        for(int i = 0; i<resultBitmap.getWidth(); i++){
            this.transformSingleRow(i, resultBitmap);
        }

        return resultBitmap;
    }

    private void transformSingleRow(int row, Bitmap resultBitmap) {
        for(int j = 0; j < resultBitmap.getHeight(); j++){
            this.transformSinglePixel(row, j, resultBitmap);
        }
    }

    private void transformSinglePixel(int row, int column, Bitmap resultBitmap) {
        int tempI;
        int tempJ;
        int resultWidth = resultBitmap.getWidth();
        int resultHeight = resultBitmap.getHeight();
        Matrix tempCoord = new Matrix(3,1);
        Matrix tempResult;
        tempCoord.set(2, 0, 1);
        tempCoord.set(0, 0, row);
        tempCoord.set(1, 0, column);
        tempResult = transformMatrix.times(tempCoord);

        tempCoord.set(0,0, tempResult.get(0,0)/tempResult.get(2,0));
        tempCoord.set(1,0, tempResult.get(1,0)/tempResult.get(2,0));
        //tempI = (int)tempCoord[1][0]; było tak, dziwne!
        //tempJ = (int)tempCoord[0][0];
        double newXValue = tempCoord.get(0,0);
        double newYValue = tempCoord.get(1,0);
        tempI = (int)newXValue;
        tempJ = (int)newYValue;
        if(this.pixelInImage(tempI, tempJ, resultWidth, resultHeight)) {

            if (tempJ + 1 >= resultHeight)
                tempJ = resultHeight - 2;

            if (tempI + 1 >= resultWidth)
                tempI = resultWidth - 2;

            int topLeftColor = resultBitmap.getPixel(tempI,tempJ);
            int topRightColor = resultBitmap.getPixel(tempI + 1,tempJ);
            int bottomLeftColor = resultBitmap.getPixel(tempI, tempJ + 1);
            int bottomRightColor = resultBitmap.getPixel(tempI + 1, tempJ + 1);

            int red = this.interpolateRed(newXValue, newYValue,  topLeftColor,  topRightColor,  bottomLeftColor,  bottomRightColor);//tempCoord[0][0], tempCoord[1][0] , a.getRed(), b.getRed(), c.getRed(), d.getRed());
            int green = this.interpolateGreen(newXValue, newYValue, topLeftColor,  topRightColor,  bottomLeftColor, bottomRightColor);//tempCoord[0][0], tempCoord[1][0] , a.getRed(), b.getRed(), c.getRed(), d.getRed());
            int blue = this.interpolateBlue(newXValue, newYValue,  topLeftColor,  topRightColor,  bottomLeftColor,  bottomRightColor);//tempCoord[0][0], tempCoord[1][0] , a.getRed(), b.getRed(), c.getRed(), d.getRed());


            if (red < 0) red = 0;
            if (red > 255) red = 255;
            if (blue < 0) blue = 0;
            if (blue > 255) blue = 255;
            if (green < 0) green = 0;
            if (green > 255) green = 255;

            int resultColor = Color.rgb(red, green, blue);

            resultBitmap.setPixel(row,column, resultColor);


        } else {
            resultBitmap.setPixel(row,column, 0xffffff);
        }

    }

    private boolean pixelInImage(int tempI, int tempJ, int width, int height) {
        return tempI >= 0 && tempJ >= 0 && tempI < width && tempJ < height;
    }
    private int interpolateRed(double xCoord, double yCoord, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        return BilinearInterpolator.interpolate(xCoord, yCoord, Color.red(topLeftColor), Color.red(topRightColor), Color.red(bottomLeftColor), Color.red(bottomRightColor));
    }
    private int interpolateGreen(double xCoord, double yCoord, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        return BilinearInterpolator.interpolate(xCoord, yCoord, Color.green(topLeftColor), Color.green(topRightColor), Color.green(bottomLeftColor), Color.green(bottomRightColor));
    }

    private int interpolateBlue(double xCoord, double yCoord, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        return BilinearInterpolator.interpolate(xCoord, yCoord, Color.blue(topLeftColor), Color.blue(topRightColor), Color.blue(bottomLeftColor), Color.blue(bottomRightColor));
    }

    private Bitmap createNewSizedBitmap() {
        return Bitmap.createBitmap(finalVertices[1].x, finalVertices[2].y, Bitmap.Config.ARGB_8888);
    }
}
