package com.maciekwski.printify.Utils.IO;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 27.10.2015.
 */
public class ImageLoader {

    public static ArrayList<Bitmap> loadImagesFromUri(ArrayList<Uri> imageUris, Context context){
        ArrayList<Bitmap> result = new ArrayList<>();
        for(Uri uri : imageUris){
            result.add(loadSingleImageFromUri(uri, context));
        }
        return result;
    }
    private  static Bitmap loadSingleImageFromUri(Uri uri, Context context) {
        Bitmap result = null;
        try {
            result = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap loadCompressedImageFromUri(Uri uri, Context context){
        Bitmap result;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        result = BitmapFactory.decodeFile(uri.getPath(), options);

        return result;
    }
}
