package com.google.android.gms.samples.vision.face.facetracker.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.samples.vision.face.facetracker.R;
import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;


public class GraphicOverlay extends View {
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mBackFacing = CameraSource.CAMERA_FACING_BACK;
    private int mFrontFacing = CameraSource.CAMERA_FACING_FRONT;
    private Set<Graphic> mGraphics = new HashSet<>();


    public static abstract class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }




        public abstract void draw(Canvas canvas);


        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }


        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }


        public float translateX(float x) {
            if (mOverlay.mFrontFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }


        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }


    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }


    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }


    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFrontFacing = facing;
        }
        postInvalidate();
    }

    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }
}
