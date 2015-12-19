package com.maciekwski.printify.Activities.PrintifyProcess;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
public class PrintifyFragment extends Fragment {

    private Bitmap imageToDisplay;


    public PrintifyFragment() {
        super();

    }

    public static PrintifyFragment newInstance(Uri uri, int position) {
        final PrintifyFragment f = new PrintifyFragment();
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
                R.layout.fragment_printify, container, false);
        this.initiateFrameView(rootView);


        return rootView;
    }

    private void initiateFrameView(ViewGroup rootView) {
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view_printify);
        this.imageToDisplay = ImageLoader.loadSingleImageFromUri((Uri) (getArguments().getParcelable("uri")), getActivity().getApplicationContext());
        imageView.setImageBitmap(imageToDisplay);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
