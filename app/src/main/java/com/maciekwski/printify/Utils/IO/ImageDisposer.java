package com.maciekwski.printify.Utils.IO;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 28.10.2015.
 */
public class ImageDisposer {
    public static void deleteImages(ArrayList<Uri> uris, Context context){
        for (Uri uriToDelete :
                uris) {
            context.getContentResolver().delete(uriToDelete, null, null);
        }
    }
}
