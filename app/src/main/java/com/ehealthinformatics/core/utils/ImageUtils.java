package com.ehealthinformatics.core.utils;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageUtils {



    public static Bitmap getBitmapFromString(Context context, String bitmapString){
        Bitmap bitmap;
        if (bitmapString.equals("false") || bitmapString.isEmpty()) {
            bitmap = BitmapUtils.getAlphabetImage(context, bitmapString);
        } else {
            bitmap = BitmapUtils.getBitmapImage(context, bitmapString);
        }
        return bitmap;
    }

}
