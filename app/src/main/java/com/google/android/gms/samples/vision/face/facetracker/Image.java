package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by intern on 12/28/2017.
 */

public class Image implements Serializable {
    public Bitmap getBitmap() {
        Log.d("Image", "getBitmap Called");
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        Log.d("Image", "Bitmap set");
    }

    private Bitmap bitmap;
}
