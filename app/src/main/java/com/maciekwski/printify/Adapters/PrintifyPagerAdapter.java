package com.maciekwski.printify.Adapters;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.maciekwski.printify.Activities.PrintifyProcess.PrintifyFragment;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 05.11.2015.
 */
public class PrintifyPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Uri> imageUris;

    public PrintifyPagerAdapter(FragmentManager fm, ArrayList<Uri> imageUris) {
        super(fm);
        this.imageUris = imageUris;
    }


    @Override
    public Fragment getItem(int position) {
        PrintifyFragment fragment = PrintifyFragment.newInstance(imageUris.get(position), position);
        return fragment;
    }


    @Override
    public int getCount() {
        return imageUris == null ? 0 : imageUris.size();
    }
}