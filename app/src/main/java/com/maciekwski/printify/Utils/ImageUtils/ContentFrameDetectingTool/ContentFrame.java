package com.maciekwski.printify.Utils.ImageUtils.ContentFrameDetectingTool;


import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 07.10.2015.
 */
public class ContentFrame {
    private DensitySquare[][] squaresFrame;
    private Point[][] pointsFrame;

    public ContentFrame(int height){
        this.squaresFrame = new DensitySquare[height][2];
        pointsFrame = new Point[height][2];
    }

    public void insertSquareToFrame(int row, DensitySquare candidate){
        DensitySquare[] squareRow = this.squaresFrame[row];
        if(!this.insertLeftSquare(squareRow, candidate)){
            {
                this.insertRightSquare(squareRow, candidate);
            }
            if(squareRow[1] == null){
                squareRow[1] = candidate;
            }
        }
    }

    private boolean insertLeftSquare(DensitySquare[] squareRow, DensitySquare candidate) {
        if(squareRow[0] == null){
            squareRow[0] = candidate;
            return true;
        } else {
            return false;
        }
    }

    private boolean insertRightSquare(DensitySquare[] squareRow, DensitySquare candidate) {
        if(squareRow[1] == null){
            squareRow[1] = candidate;
            return true;
        } else {    //not sure if even needed
            if(candidate.isRightTo(squareRow[1])){
                squareRow[1] = candidate;
                return true;
            }
        }
        return false;
    }

    public Point[][] generatePointsFrame(){

        int firstFullRow = this.generateFirstPointsRow();
        int lastFullRow = this.generateCenterPointsRows(firstFullRow+1);
        this.correctLastPointsRow(lastFullRow);
        this.cutEmptyRows();
        return pointsFrame;
    }

    private void cutEmptyRows() {
        ArrayList<Point[]> cutPointsFrame = new ArrayList<>();
        for(Point[] row: pointsFrame){
            if(row[0] != null || row[1] != null) {
                cutPointsFrame.add(row);
            }
        }
        pointsFrame = (Point[][])cutPointsFrame.toArray();
    }

    private int generateFirstPointsRow() {
        int firstFullRow = 0;
        boolean isFull = false;
        for(; firstFullRow < pointsFrame.length && !isFull; firstFullRow++){
            isFull = this.tryGenerateFirstPointsRow(firstFullRow);
        }
        return firstFullRow;
    }

    private boolean tryGenerateFirstPointsRow(int lastRowPosition) {
        DensitySquare left = squaresFrame[lastRowPosition][0];
        DensitySquare right = squaresFrame[lastRowPosition][1];
        if(null != left){
            if(null != right){
                this.setFirstPointsRow(left, right, lastRowPosition);
            } else {
                this.setFirstPointsRow(left, left, lastRowPosition);
            }
            return true;
        } else {
            if(null != right) {
                this.setFirstPointsRow(right, right, lastRowPosition);
                return true;
            }
        }
        return false;
    }

    private void setFirstPointsRow(DensitySquare left, DensitySquare right, int lastRowPosition) {
        Point leftPoint = new Point(left.getTopLeft());
        Point rightPoint = new Point(right.getTopRight());
        pointsFrame[lastRowPosition][0] = leftPoint;
        pointsFrame[lastRowPosition][1] = rightPoint;
    }

    private int generateCenterPointsRows(int startingRow) {
        int lastRow = startingRow;
        boolean addedPointsInLastRow;
        for(int i = startingRow; i < pointsFrame.length; i++){
            addedPointsInLastRow = generateSingleCenterRow(i);
            if(addedPointsInLastRow){
                lastRow = i;
            }
        }
        return lastRow;
    }

    private boolean generateSingleCenterRow(int lastRowPosition) {
        DensitySquare left = squaresFrame[lastRowPosition][0];
        DensitySquare right = squaresFrame[lastRowPosition][1];
        if(null != left){
            if(null != right){
                this.setCenterPointsRow(left, right, lastRowPosition);
            } else {
                this.setCenterPointsRow(left, left, lastRowPosition);
            }
            return true;
        } else {
            if(null != right) {
                this.setCenterPointsRow(right, right, lastRowPosition);
                return true;
            }
        }
        return false;
    }

    private void setCenterPointsRow(DensitySquare left, DensitySquare right, int lastRowPosition) {
        Point leftPoint = new Point(left.getCenterLeft());
        Point rightPoint = new Point(right.getCenterRight());
        pointsFrame[lastRowPosition][0] = leftPoint;
        pointsFrame[lastRowPosition][1] = rightPoint;
    }

    private void correctLastPointsRow(int lastRowPosition) {
        DensitySquare left = squaresFrame[lastRowPosition][0];
        DensitySquare right = squaresFrame[lastRowPosition][1];
        if(null != left){
            if(null != right){
                this.setLastRowPoints(left, right, lastRowPosition);
            } else {
                this.setLastRowPoints(left, left, lastRowPosition);
            }
        } else {
            if(null != right){
                this.setLastRowPoints(right, right, lastRowPosition);
            }
        }
    }

    private void setLastRowPoints(DensitySquare left, DensitySquare right, int lastRowPosition) {
        Point leftPoint = new Point(left.getBottomLeft());
        Point rightPoint = new Point(right.getBottomRight());
        pointsFrame[lastRowPosition][0] = leftPoint;
        pointsFrame[lastRowPosition][1] = rightPoint;
    }
}
