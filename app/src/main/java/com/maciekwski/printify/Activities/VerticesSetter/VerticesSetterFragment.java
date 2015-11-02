package com.maciekwski.printify.Activities.VerticesSetter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageLoader;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 26.10.2015.
 */
public class VerticesSetterFragment extends Fragment {

    private Bitmap imageToDisplay;
    private FrameView frameView;
    public VerticesSetterFragment(){
        super();
    }


    public static VerticesSetterFragment newInstance(Uri uri) {
        final VerticesSetterFragment f = new VerticesSetterFragment();
        final Bundle args = new Bundle();
        args.putParcelable("uri", uri);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_set_vertices, container, false);

        frameView = (FrameView)rootView.findViewById(R.id.frame_view_set_vertices);
        this.imageToDisplay = ImageLoader.loadCompressedImageFromUri((Uri)(getArguments().getParcelable("uri")), getActivity().getApplicationContext());
        frameView.setImageBitmap(imageToDisplay);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
