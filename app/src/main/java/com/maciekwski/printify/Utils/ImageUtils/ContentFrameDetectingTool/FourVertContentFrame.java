package com.maciekwski.printify.Utils.ImageUtils.ContentFrameDetectingTool;

import android.graphics.Point;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 07.10.2015.
 */
public class FourVertContentFrame {
    Point[] frame;
    public FourVertContentFrame(Point[][] contentFrame){
        frame = new Point[4];
        generateFrame(contentFrame);
    }

    private void generateFrame(Point[][] contentFrame){
        // TODO ale będzie!!
    }

    public Point[] getFrame(){
        Point[] newFrame = new Point[4];
        for(int i = 0; i < 4; i++){
            newFrame[i] = new Point(frame[i]);
        }
        return newFrame;
    }
}
