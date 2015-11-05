package com.maciekwski.printify.Activities.VerticesSetter;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 02.11.2015.
 */
public class RelativeToRealVerticesTransformer {
    public static Point[] transform(RelativeVertices relV, Point realSize){
        Point[] result = new Point[4];
        double ratio = (double)realSize.y/relV.height;

        for(int i = 0; i < 4; i++){
            result[i] = new Point((int)(ratio*relV.vertices[i].x), (int)(ratio*relV.vertices[i].y));
        }

        return result;
    }
}
