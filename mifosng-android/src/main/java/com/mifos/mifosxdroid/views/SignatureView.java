package com.mifos.mifosxdroid.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.mifos.mifosxdroid.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Tarun on 28-06-2017.
 */

public class SignatureView extends View {

    private final String LOG_TAG;
    private Paint signPaint;
    private ArrayList<Float> mXCoordinateList;
    private ArrayList<Float> mYCoordinateList;
    private OnSignatureSaveListener mOnSignatureSaveListener;
    private Context mContext;

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LOG_TAG = getClass().getSimpleName();

        mContext = context;
        setDrawingCacheEnabled(true);
        Resources r = getResources();
        int strokeSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                r.getDisplayMetrics());

        signPaint = new Paint();
        signPaint.setColor(Color.BLACK);
        signPaint.setStrokeWidth(strokeSize);
        signPaint.setAntiAlias(true);
        mXCoordinateList = new ArrayList<>();
        mYCoordinateList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (mXCoordinateList.size() > 2) {
            for (int i = 1; i < mXCoordinateList.size(); i++) {
                if (!(mXCoordinateList.get(i - 1) == -1.0 || mXCoordinateList.get(i) == -1)) {
                    canvas.drawLine(mXCoordinateList.get(i - 1), mYCoordinateList.get(i - 1),
                            mXCoordinateList.get(i), mYCoordinateList.get(i), signPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mXCoordinateList.add(event.getX());
                mYCoordinateList.add(event.getY());
                break;
            }
            case MotionEvent.ACTION_UP: {
                mXCoordinateList.add((float) -1.0);
                mYCoordinateList.add((float) -1.0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mXCoordinateList.add(event.getX());
                mYCoordinateList.add(event.getY());
                invalidate();
                break;
            }
        }
        return true;
    }

    public interface OnSignatureSaveListener {
        void onSignSavedError(String errorMsg);

        void onSignSavedSuccess(String absoluteFilePath);
    }

    public void setOnSignatureSaveListener(OnSignatureSaveListener listener) {
        mOnSignatureSaveListener = listener;
    }

    public void saveSignature(int id) {
        String path = Environment.getExternalStorageDirectory() +
                mContext.getResources().getString(R.string.signature_image_directory);
        File signatureStorageDirectory = new File(path);
        File signatureFile;
        if (!signatureStorageDirectory.exists()) {
            boolean makeRequiredDirectories = signatureStorageDirectory.mkdirs();
            if (!makeRequiredDirectories) {
                mOnSignatureSaveListener.onSignSavedError(mContext.getResources().
                        getString(R.string.sign_dir_not_created_msg));
                return;
            }
        }

        try {
            Bitmap signatureBitmap = getDrawingCache();
            signatureFile = new File(signatureStorageDirectory.getPath(), id + ".jpeg");
            signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(signatureFile));
        } catch (FileNotFoundException e) {
            if (mOnSignatureSaveListener != null) {
                mOnSignatureSaveListener.onSignSavedError(e.getMessage());
            }
            return;
        }
        if (mOnSignatureSaveListener != null) {
            mOnSignatureSaveListener.onSignSavedSuccess(signatureFile.getAbsolutePath());
        }
    }


    public void clear() {
        mXCoordinateList.clear();
        mYCoordinateList.clear();
        destroyDrawingCache();
        invalidate();
    }

    public int getXCoordinateSize() {
        return mXCoordinateList.size();
    }

    public int getYCoordinateSize() {
        return mYCoordinateList.size();
    }
}