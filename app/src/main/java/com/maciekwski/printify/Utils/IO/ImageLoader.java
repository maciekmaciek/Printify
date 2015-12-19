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

    public static Bitmap loadSingleImageFromUri(Uri uri, Context context) {
        Bitmap result = null;
        try {
            result = MediaStore.Images.Media.getBitmap(context.getContentResolver(), fixUri(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap loadCompressedImageFromUri(Uri uri, int maxDim) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), options);
        options.inSampleSize = calculateScalingFactor(maxDim, options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri.getPath(), options);
    }

    private static int calculateScalingFactor(int max, int imageWidth, int imageHeight) {
        if (imageWidth > max && imageWidth > imageHeight) {
            return calculateInSampleSize(imageWidth, max);
        } else if (imageHeight > max && imageWidth < imageHeight) {
            return calculateInSampleSize(imageHeight, max);
        } else {
            return 1;
        }
    }

    private static int calculateInSampleSize(int biggerSize, int max) {
        // Raw height and width of image
        int inSampleSize = 1;
        if (biggerSize > max) {
            final int halfBigger = biggerSize / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfBigger / inSampleSize) > max) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Uri fixUri(Uri invalidUri) {
        String fix = "file:///";
        if (!invalidUri.toString().startsWith(fix)) {
            return Uri.withAppendedPath(Uri.parse(fix), invalidUri.getPath());
        } else {
            return invalidUri;
        }
    }
}
