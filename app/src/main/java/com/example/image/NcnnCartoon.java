package com.example.image;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.view.Surface;

public class NcnnCartoon {
    public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
    public native Bitmap cartoon(Bitmap bitmap);
    static {
        System.loadLibrary("ncnncartoon");
    }
}
