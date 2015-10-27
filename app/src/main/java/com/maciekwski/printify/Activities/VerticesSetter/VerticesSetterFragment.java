package com.maciekwski.printify.Activities.VerticesSetter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.maciekwski.printify.R;
import com.maciekwski.printify.Utils.IO.ImageLoader;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 26.10.2015.
 */
public class VerticesSetterFragment extends Fragment {

    private Bitmap imageToDisplay;
    public VerticesSetterFragment(){
        super();
    }

    @SuppressLint("ValidFragment")
    public VerticesSetterFragment(Uri uri){
        this.imageToDisplay = ImageLoader.loadCompressedImageFromUri(uri, getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_set_vertices, container, false);

        int imageIndex = rootView.indexOfChild(getActivity().findViewById(R.id.image_view_set_vertices));
        rootView.getChildAt(imageIndex).setBackground(new BitmapDrawable(getResources(),imageToDisplay));
        return rootView;
    }


}
