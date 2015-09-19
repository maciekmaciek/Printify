package com.maciekwski.printify.Adapters;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 07.09.2015.
 */
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.maciekwski.printify.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * author: Soulwolf Created on 2015/7/13 23:49.
 * email : Ching.Soulwolf@gmail.com
 */
public class PictureAdapter extends BaseAdapter {

    Context mContext;

    List<Uri> mPictureList;

    public PictureAdapter(Context context, List<Uri> pictures){
        this.mContext = context;
        this.mPictureList = pictures;
    }

    @Override
    public int getCount() {
        return mPictureList == null ? 0 : mPictureList.size();
    }

    @Override
    public Uri getItem(int position) {
        return mPictureList == null ? null : mPictureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_picture_item,null);
            holder.mPictureView = (ImageView) convertView.findViewById(R.id.pi_picture_item_image);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        // load image
        Uri uri = getItem(position);
        Picasso.with(mContext)
                .load(new File(uri.getPath())).error(R.drawable.pd_empty_picture)
                .into(holder.mPictureView);
        return convertView;
    }

    static class ViewHolder{
        public ImageView mPictureView;
    }
}