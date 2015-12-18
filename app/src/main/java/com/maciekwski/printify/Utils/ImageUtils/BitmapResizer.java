package com.maciekwski.printify.Utils.ImageUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Pair;
import com.maciekwski.printify.Utils.IO.ImageLoader;
import com.maciekwski.printify.Utils.IO.ImageSaver;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 16.12.2015.
 */
public class BitmapResizer {
    final static int MAX_DIM = 1600;
    public static ArrayList<Uri> resizeBitmapsFromUris(ArrayList<Uri> sourceUris) {
        ArrayList<Uri> result = new ArrayList<>();

        for (Uri uri :
                sourceUris) {
            result.add(resizeAndSaveBitmap(uri, result.size()));
        }
        return result;
    }
    public static Uri resizeAndSaveBitmap(Uri uri, int number){
        Bitmap workerBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        workerBitmap = ImageLoader.loadCompressedImageFromUri(uri, MAX_DIM);
        Uri res = ImageSaver.saveSingleImageReturnUri(workerBitmap, number);
        workerBitmap.recycle();
        return res;
    }

}
