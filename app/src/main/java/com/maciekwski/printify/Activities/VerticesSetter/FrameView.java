package com.maciekwski.printify.Activities.VerticesSetter;

import android.content.Context;
import android.graphics.*;
import android.media.Image;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.TouchImageView;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 29.10.2015.
 */
public class FrameView extends ImageView {
    private static final float TOUCH_TOLERANCE = 50;
    Point[] vertices;
    private Bitmap mBitmap;
    private Bitmap underlyingImage;
    private Canvas mCanvas;
    private Paint framePaint;
    private Paint insidePaint;
    private Paint thePaint;

    public FrameView(Context context) {
        super(context);
        this.initialize();
    }
    public FrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    private void initialize(){
        this.initializePaints();
    }

    private void initializePaints() {
        thePaint = new Paint(Paint.DITHER_FLAG);

        framePaint = new Paint(Paint.DITHER_FLAG);
        framePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        framePaint.setStrokeJoin(Paint.Join.BEVEL);
        framePaint.setColor(getResources().getColor(R.color.SeaGreen));
        framePaint.setStrokeWidth(15f);

        insidePaint = new Paint(Paint.DITHER_FLAG);
        insidePaint.setColor(getResources().getColor(R.color.TransparentSeaGreen));
        insidePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        insidePaint.setStrokeJoin(Paint.Join.BEVEL);
    }

    private void initializeVertices() {
        vertices = new Point[4];
        vertices[0] = new Point(0,0);
        vertices[1] = new Point(getWidth(), 0);
        vertices[2] = new Point(getWidth(),getHeight());
        vertices[3] = new Point(0, getHeight());
    }

    private void drawFigure() {
        Path polygonPath = new Path();
        for(int i = 0; i<vertices.length; i++){
            Point vertice = vertices[i];
            Point prev;
            if(i != 0){
                prev = vertices[i-1];
            } else {
                prev = vertices[vertices.length -1];
                polygonPath.moveTo(prev.x, prev.y);
            }
            polygonPath.lineTo(vertice.x, vertice.y);

            mCanvas.drawLine(prev.x, prev.y, vertice.x, vertice.y, framePaint);
            mCanvas.drawCircle(vertice.x, vertice.y, 50, framePaint);
        }
        mCanvas.drawPath(polygonPath, insidePaint);

    }
    private int currentPoint = -1;

    private int touch_start(float x, float y) {

        for(int i = 0; i < vertices.length; i++){
            if(this.isClose(x,y, vertices[i])){
                return i;
            }
        }
        return -1;
    }

    private boolean isClose(float x, float y, Point vertice) {
        float dx = Math.abs(x - vertice.x);
        float dy = Math.abs(y - vertice.y);
        return dx <= TOUCH_TOLERANCE && dy <= TOUCH_TOLERANCE;
    }

    private void touch_move(float x, float y) {
        vertices[currentPoint].x = (int)x;
        vertices[currentPoint].y = (int)y;
    }

    private void touch_up(float x, float y) {
        vertices[currentPoint].x = (int)x;
        vertices[currentPoint].y = (int)y;
        currentPoint = -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPoint = touch_start(x, y);
                return currentPoint != -1;
            case MotionEvent.ACTION_MOVE:
                if (currentPoint != -1) {
                    touch_move(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPoint != -1) {
                    touch_up(x, y);
                }
        }

        postInvalidate();
        return true;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.initializeVertices();
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.underlyingImage = Bitmap.createScaledBitmap(underlyingImage, this.getWidth(), this.getHeight(), true);

        mCanvas = new Canvas(mBitmap);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.drawImage();

        this.drawFigure();
        canvas.drawBitmap(mBitmap, 0, 0, thePaint);
    }

    private void drawImage() {
        mCanvas.drawBitmap(underlyingImage,0,0, thePaint);
    }

    @Override
    public void setImageBitmap(Bitmap imageBitmap) {
        super.setImageBitmap(imageBitmap);
        this.underlyingImage = imageBitmap;
    }
}