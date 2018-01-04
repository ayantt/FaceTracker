package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;


    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private Resources maskResource;

    public Intent intent;
    public Image image = new Image();
    public Bitmap maskBitmapGraphic;
    public Matrix rotateMatrix = new Matrix();
    float prevWidth = 0;
    float prevHeight = 0;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        Image image = FaceTrackerActivity.getImageFromActivity();
        maskBitmapGraphic = image.getBitmap();

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }



    void setId(int id) {
        mFaceId = id;
    }


    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
        float faceWidth;
        float faceHeight;
        float threshP = prevWidth +((prevWidth*5)/100);
        float threshN = prevWidth -((prevWidth*5)/100);
        if (threshN>face.getWidth() || face.getWidth()>threshP){
            faceWidth = face.getWidth();
            faceHeight = face.getHeight();
        }else {
            faceWidth = prevWidth;
            faceHeight = prevHeight;
        }

        float rotationY = face.getEulerY();
        float rotationZ = face.getEulerZ();
        float alignX = rotationZ*4;
        float alignY = rotationZ*4;
        Log.d("Face",Math.round(faceHeight)+"x"+Math.round(faceWidth));

        float x = translateX(face.getPosition().x + faceWidth / 2);
        float y = translateY(face.getPosition().y + faceHeight / 2);

        float xOffset = scaleX(faceWidth / 3.0f);
        float yOffset = scaleY(faceHeight / 3.0f);
        float leftMask = (x - (xOffset-alignX));
        float topMask = (y - (yOffset+alignY));
        Log.d("X,Y: ",leftMask+", "+topMask);

        //adding mask on face
        Bitmap maskBitmapGraphicScaled, rotatedBitmap;
        if (maskBitmapGraphic!=null) {
            rotateMatrix.postRotate(rotationZ);
            maskBitmapGraphicScaled = Bitmap.createScaledBitmap(maskBitmapGraphic, (int) Math.round(faceWidth * 1.5),
                    Math.round(faceHeight * 2), false);
            rotatedBitmap = Bitmap.createBitmap(maskBitmapGraphicScaled,0,0,
                    maskBitmapGraphicScaled.getWidth(),maskBitmapGraphicScaled.getHeight(),rotateMatrix,true);
            float rotatedOffsetX = rotatedBitmap.getWidth()-maskBitmapGraphicScaled.getWidth();
            float rotatedOffsetY = rotatedBitmap.getHeight()-maskBitmapGraphicScaled.getHeight();

                canvas.drawBitmap(rotatedBitmap, leftMask-rotatedOffsetX, topMask, null);
                prevWidth= faceWidth;
                prevHeight = faceHeight;
            Log.d("R",maskBitmapGraphicScaled.getHeight()+"x"+maskBitmapGraphicScaled.getWidth()
                    +", "+rotatedBitmap.getHeight()+"x"+rotatedBitmap.getWidth());
            //Log.d("R", String.valueOf(rotationZ));
            rotateMatrix.postRotate(-rotationZ);
        } else {
            Log.d("FaceGraphic", "No bitmap");
        }
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);

    }

}
