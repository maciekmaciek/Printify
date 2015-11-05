package com.maciekwski.printify.Adapters;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.maciekwski.printify.Activities.VerticesSetter.VerticesSetterFragment;

import java.util.ArrayList;


/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 26.10.2015.
 */
public class VerticesSetterPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Uri> imageUris;
        public VerticesSetterPagerAdapter(FragmentManager fm, ArrayList<Uri> imageUris) {
            super(fm);
            this.imageUris = imageUris;
        }


        @Override
        public Fragment getItem(int position) {
            VerticesSetterFragment fragment = VerticesSetterFragment.newInstance(imageUris.get(position), position);
            return fragment;
        }



    @Override
    public int getCount() {
        return imageUris == null ? 0 : imageUris.size();
    }
}