package com.maciekwski.printify.Activities.VerticesSetter;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 26.10.2015.
 */
public class VerticesSetterFragment extends Fragment implements View.OnTouchListener, View.OnLayoutChangeListener {

    private Bitmap imageToDisplay;
    private FrameView frameView;
    private Point[] vertices;
    private static final float TOUCH_TOLERANCE = 90;


    public VerticesSetterFragment(){
        super();

    }

    public static VerticesSetterFragment newInstance(Uri uri, int position) {
        final VerticesSetterFragment f = new VerticesSetterFragment();
        final Bundle args = new Bundle();
        args.putParcelable("uri", uri);
        args.putInt("position", position);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_set_vertices, container, false);
        this.takeVerticesFromParent();
        this.initiateFrameView(rootView);


        return rootView;
    }

    private void initiateFrameView(ViewGroup rootView) {
        frameView = (FrameView)rootView.findViewById(R.id.image_view_set_vertices);
        this.imageToDisplay = ImageLoader.loadSingleImageFromUri((Uri)(getArguments().getParcelable("uri")), getActivity().getApplicationContext());
        /*Picasso.with(getActivity().getApplicationContext()).
                load(getArguments().<Uri>getParcelable("uri")).
                fit().
                into(frameView);*/
        frameView.setImageBitmap(imageToDisplay);
        frameView.addOnLayoutChangeListener(this);
        frameView.setOnTouchListener(this);
        this.setFrameViewVerticesOrUpdateFragmentVertices();
    }

    private void setFrameViewVerticesOrUpdateFragmentVertices() {
        if(this.vertices == null){
            this.vertices = frameView.vertices;
        } else {
            frameView.vertices = this.vertices;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    public void setParentVertices(Point[] vertices){
        ((VerticesSetterActivity)getActivity()).allVertices[getArguments().getInt("position")] = vertices;
    }

    private void takeVerticesFromParent() {
        this.vertices = ((VerticesSetterActivity)getActivity()).allVertices[getArguments().getInt("position")];

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.vertices = frameView.vertices;
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
        this.setParentVertices(vertices);
        this.setFrameViewVerticesOrUpdateFragmentVertices();
        v.postInvalidate();
        return true;
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
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.takeVerticesFromParent();
        this.setFrameViewVerticesOrUpdateFragmentVertices();
        if(oldLeft == 0){
            this.setParentWidthHeightInfo(right, bottom);
        }
    }

    private void setParentWidthHeightInfo(int right, int bottom) {
        ((VerticesSetterActivity)getActivity()).height = bottom;
        ((VerticesSetterActivity)getActivity()).width = right;
    }
}
