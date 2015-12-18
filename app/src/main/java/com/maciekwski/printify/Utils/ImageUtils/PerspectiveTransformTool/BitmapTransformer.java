package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import Jama.Matrix;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import com.maciekwski.printify.Utils.ArrayToMatrixConverter;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 15.10.2015.
 */
public class BitmapTransformer {
    private Bitmap imageToTransform;
    private int[][] pixelsToTransform;
    private Point[] contentVertices;
    private Point[] finalVertices;
    private Matrix transformMatrix;
    private int[] sourcePixels;
    private int sourceWidth;
    private int sourceHeight;
    private boolean needsNoMatrix;


    public BitmapTransformer(BitmapSizeWithContentVertices bswcv, Bitmap bitmap) {
        this.imageToTransform = bitmap;
        this.contentVertices = bswcv.contentVertices;
        if(this.isNearRectangle()){
            needsNoMatrix = true;
        } else {
            this.finalVertices = ResultRectangleBuilder.buildFromVertices(contentVertices);
            this.sourceWidth = imageToTransform.getWidth();
            this.sourceHeight = imageToTransform.getHeight();
            sourcePixels = new int[sourceWidth*sourceHeight];
            imageToTransform.getPixels(sourcePixels, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
            pixelsToTransform = ArrayToMatrixConverter.convert(sourcePixels, sourceWidth, sourceHeight);
            TransformMatrixBuilder transformMatrixBuilder = new TransformMatrixBuilder(contentVertices, finalVertices);
            this.transformMatrix = transformMatrixBuilder.generateTransformMatrix();
        }
    }

    public Bitmap transformImage(){
        Bitmap resultBitmap;
        int[] resultPixels;
        if(needsNoMatrix){
            resultBitmap = this.transformRectangle();
        } else {
            resultBitmap = this.createNewSizedBitmap();
           /* for (int i = 0; i < resultBitmap.getWidth(); i++) {
                this.transformSingleRow(i, resultBitmap);
            }*/
            resultPixels = new int[resultBitmap.getWidth()*resultBitmap.getHeight()];
            this.transformPixels(resultPixels, resultBitmap.getWidth());
            resultBitmap.setPixels(resultPixels, 0, resultBitmap.getWidth(), 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());
        }

        return resultBitmap;
    }

    private void transformPixels(int[] resultPixels, int width) {
        for(int i = 0; i < resultPixels.length; i++){
            int curW = i % width;
            int curH = i / width;
            transformSinglePixel(curW, curH, resultPixels, i);
        }
    }

    private Bitmap transformRectangle() {
        Point topLeft = new Point();
        Point bottomRight = new Point();
        topLeft.x = contentVertices[0].x < contentVertices[3].x ? contentVertices[0].x : contentVertices[3].x;
        topLeft.y = contentVertices[0].y < contentVertices[3].y ? contentVertices[0].y : contentVertices[3].y;
        bottomRight.x = contentVertices[1].x > contentVertices[2].x ? contentVertices[1].x : contentVertices[2].x;
        bottomRight.y = contentVertices[1].y > contentVertices[2].y ? contentVertices[1].y : contentVertices[2].y;
        return Bitmap.createBitmap(imageToTransform,topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
    }
/*
    private void transformSingleRow(int row, Bitmap resultBitmap) {
        for(int j = 0; j < resultBitmap.getHeight(); j++){
            this.transformSinglePixel(row, j, resultBitmap);
        }
    }*/

    private void transformSinglePixel(int row, int column, int[] resultPixels, int currentPixel){// Bitmap resultBitmap) {
        int tempI;
        int tempJ;
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
        if(this.pixelInImage(tempI, tempJ)) {

            if (tempJ + 1 >= sourceHeight)
                tempJ = sourceHeight - 2;

            if (tempI + 1 >= sourceWidth)
                tempI = sourceWidth - 2;

            int topLeftColor = sourcePixels[sourceWidth*tempJ +tempI];
            int topRightColor = sourcePixels[sourceWidth*(tempJ+1) + tempI];
            int bottomLeftColor = sourcePixels[sourceWidth*tempJ + tempI +1];
            int bottomRightColor = sourcePixels[sourceWidth*tempJ+1 +tempI+1];
/*

            int topLeftColor = imageToTransform.getPixel(tempI,tempJ);
            int topRightColor = imageToTransform.getPixel(tempI + 1,tempJ);
            int bottomLeftColor = imageToTransform.getPixel(tempI, tempJ + 1);
            int bottomRightColor = imageToTransform.getPixel(tempI + 1, tempJ + 1);
*/

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

            resultPixels[currentPixel] = resultColor;


        } else {
            resultPixels[currentPixel] = 0xffffff;
        }

    }

    private boolean pixelInImage(int tempI, int tempJ) {
        return tempI >= 0 && tempJ >= 0 && tempI < sourceWidth && tempJ < sourceHeight;
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

    static final double RECT_MARGIN = 0.02;
    public boolean isNearRectangle() {
        boolean topRect = Math.abs(contentVertices[0].y - contentVertices[1].y) < RECT_MARGIN * imageToTransform.getHeight();
        boolean botRect = Math.abs(contentVertices[2].y - contentVertices[3].y) < RECT_MARGIN * imageToTransform.getHeight();
        boolean leftRect = Math.abs(contentVertices[0].x - contentVertices[3].x) < RECT_MARGIN * imageToTransform.getWidth();
        boolean rightRect = Math.abs(contentVertices[1].x - contentVertices[2].x) < RECT_MARGIN * imageToTransform.getWidth();

        return topRect && botRect & leftRect && rightRect;
    }
}
