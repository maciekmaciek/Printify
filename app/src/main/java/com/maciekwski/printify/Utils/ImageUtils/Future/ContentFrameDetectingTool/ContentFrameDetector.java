package com.maciekwski.printify.Utils.ImageUtils.Future.ContentFrameDetectingTool;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 06.10.2015.
 */
/*** Might want to use this for some kind of automatic edge detection***/
public class ContentFrameDetector {
    private final int ROWS = 40;
    private Bitmap contentToDetect;
    private ArrayList<ArrayList<DensitySquare>> densitySquaresGrid;
    private double densityThreshold;
    private Point[][] contentFrame;

    public ContentFrameDetector(Bitmap contentToDetect) {
        this.contentToDetect = contentToDetect;
    }

    public Point[] detectContentFrame() {
        this.generateSquares();
        this.findThreshold();
        this.buildContentFrame();
        return this.generateFourVertFrame();
    }

    //generate squares
    private void generateSquares() {
        contentFrame = new Point[1][1];
    }

    //find threshold
    private void findThreshold() {

    }

    //build contentFrame
    private void buildContentFrame() {
        ContentFrame frameGenerator = new ContentFrame(densitySquaresGrid.size());
        for (int i = 0; i < densitySquaresGrid.size(); i++) {
            ArrayList<DensitySquare> squaresRow = densitySquaresGrid.get(i);
            for (int j = 0; j < squaresRow.size(); i++) {
                DensitySquare square = squaresRow.get(j);
                if (square.isOverThreshold(this.densityThreshold)) {
                    frameGenerator.insertSquareToFrame(i, square);
                }
            }
        }
        this.contentFrame = frameGenerator.generatePointsFrame();
    }

    //turn into 4 edges
    private Point[] generateFourVertFrame() {
        FourVertContentFrame frame = new FourVertContentFrame(this.contentFrame);
        return frame.getFrame();
    }


}
