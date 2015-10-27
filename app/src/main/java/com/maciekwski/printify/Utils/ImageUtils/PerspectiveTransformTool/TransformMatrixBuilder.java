package com.maciekwski.printify.Utils.ImageUtils.PerspectiveTransformTool;

import Jama.Matrix;
import android.graphics.Point;


/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 14.10.2015.
 */

//buduj macież przekształcenia Z PROSTOKĄTA DO TRAPEZU
public class TransformMatrixBuilder {
    private Point[] imageVertices;
    private Point[] resultVertices;

    public TransformMatrixBuilder(Point[] imageVertices, Point[] resultVertices){
        this.imageVertices = imageVertices;
        this.resultVertices = resultVertices;
    }

    public Matrix generateTransformMatrix() throws RuntimeException{
        Matrix tempResult = this.calculateMatrixParameters();
        return this.createMatrixFromParams(tempResult);
    }

    private Matrix calculateMatrixParameters() {
        Matrix v1 = this.generateFirstMatrix(
                resultVertices[0], imageVertices[0],
                resultVertices[1], imageVertices[1],
                resultVertices[2], imageVertices[2],
                resultVertices[3], imageVertices[3]);
        Matrix v2 = this.generateSecondMatrix(
                resultVertices[0], imageVertices[0],
                resultVertices[1], imageVertices[1],
                resultVertices[2], imageVertices[2],
                resultVertices[3], imageVertices[3]);
        v1 = v1.inverse();
       return v1.times(v2);
    }

    private Matrix generateFirstMatrix(Point v11, Point v12, Point v21, Point v22, Point v31, Point v32, Point v41, Point v42) {
        double[][] v1arr = {
                {v41.x, v41.y, 1, 0, 0, 0, -v12.x*v11.x, -v12.x*v11.y},
                {v21.x, v21.y, 1, 0, 0, 0, -v22.x*v21.x, -v22.x*v21.y},
                {v11.x, v11.y, 1, 0, 0, 0, -v32.x*v31.x, -v32.x*v31.y},
                {v31.x, v31.y, 1, 0, 0, 0, -v42.x*v41.x, -v42.x*v41.y},
                {0, 0, 0, v11.x, v11.y, 1, -v12.y*v11.x, -v12.y*v11.y},
                {0, 0, 0, v21.x, v21.y, 1, -v22.y*v21.x, -v22.y*v21.y},
                {0, 0, 0, v31.x, v31.y, 1, -v32.y*v31.x, -v32.y*v31.y},
                {0, 0, 0, v41.x, v41.y, 1, -v42.y*v41.x, -v42.y*v41.y}
        };
        return new Matrix(v1arr);
    }

    private Matrix generateSecondMatrix(Point v11, Point v12, Point v21, Point v22, Point v31, Point v32, Point v41, Point v42) {
        double[][] v2arr = {
                {v12.x},
                {v22.x},
                {v32.x},
                {v42.x},
                {v12.y},
                {v22.y},
                {v32.y},
                {v42.y}
        };
        return new Matrix(v2arr);
    }

    private Matrix createMatrixFromParams(Matrix tempResult) {
        double[][] resArr = {
                {tempResult.get(0,0), tempResult.get(1,0), tempResult.get(2,0)},
                {tempResult.get(3,0), tempResult.get(4,0), tempResult.get(5,0)},
                {tempResult.get(6,0), tempResult.get(7,0), 1}
        };
        return new Matrix(resArr);
    }
}
