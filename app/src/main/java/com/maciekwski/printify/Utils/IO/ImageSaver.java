package com.maciekwski.printify.Utils.IO;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 27.10.2015.
 */
public class ImageSaver {
    public static ArrayList<Uri> saveImagesReturnUris(ArrayList<Bitmap> images) {
        ArrayList<Uri> resultUris = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            saveSingleImageReturnUri(images.get(i), i);
        }
        return resultUris;
    }

    public static Uri saveSingleImageReturnUri(Bitmap curBitmap, int number) {
        String path = Environment.getExternalStorageDirectory().toString() + "/Printify";
        new File(path).mkdirs();
        String indFileName = createIndFileName(number);
        File savedImage = new File(path + "/" + indFileName);

        try {
            FileOutputStream out = new FileOutputStream(savedImage);
            curBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(savedImage);
    }

    private static String createIndFileName(int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd-HHmm", Locale.ENGLISH);
        String number = String.format("%02d", index);
        String name = sdf.format(new Date());
        name += "-" + number + ".png";
        return name;
    }

    public static void saveBitmapToGivenUri(Bitmap bitmap, Uri uri) {
        try {
            FileOutputStream out = new FileOutputStream(new File(uri.getPath()));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
