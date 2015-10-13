package com.maciekwski.printify.Utils.ImageUtils.ImageBinarizingTool;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Maciej Wolański
 * maciekwski@gmail.com
 * on 23.09.2015.
 */
public class ImageJoiner {
    public static Bitmap joinBlackAndWhiteImages(Bitmap img1, Bitmap img2){
        int width = img1.getWidth();
        int height = img1.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(isBlack(img1.getPixel(x, y)) || isBlack(img2.getPixel(x,y))){
                    result.setPixel(x, y, Color.BLACK);
                } else {
                    result.setPixel(x, y, Color.WHITE);
                }
            }
        }
        return result;
    }

    private static boolean isBlack(int color){
        return color == Color.BLACK;
    }
}
