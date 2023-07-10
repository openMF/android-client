package com.mifos.mifosxdroid.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Environment
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.mifos.mifosxdroid.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Created by Tarun on 28-06-2017.
 */
class SignatureView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val LOG_TAG: String = javaClass.simpleName
    private val signPaint: Paint
    private val mXCoordinateList: ArrayList<Float>
    private val mYCoordinateList: ArrayList<Float>
    private var mOnSignatureSaveListener: OnSignatureSaveListener? = null
    private val mContext: Context

    init {
        mContext = context
        isDrawingCacheEnabled = true
        val r = resources
        val strokeSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f,
            r.displayMetrics
        ).toInt()
        signPaint = Paint()
        signPaint.color = Color.BLACK
        signPaint.strokeWidth = strokeSize.toFloat()
        signPaint.isAntiAlias = true
        mXCoordinateList = ArrayList()
        mYCoordinateList = ArrayList()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        if (mXCoordinateList.size > 2) {
            for (i in 1 until mXCoordinateList.size) {
                if (!(mXCoordinateList[i - 1].toDouble() == -1.0 || mXCoordinateList[i] == -1f)) {
                    canvas.drawLine(
                        mXCoordinateList[i - 1], mYCoordinateList[i - 1],
                        mXCoordinateList[i], mYCoordinateList[i], signPaint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mXCoordinateList.add(event.x)
                mYCoordinateList.add(event.y)
            }

            MotionEvent.ACTION_UP -> {
                mXCoordinateList.add(-1.0.toFloat())
                mYCoordinateList.add(-1.0.toFloat())
            }

            MotionEvent.ACTION_MOVE -> {
                mXCoordinateList.add(event.x)
                mYCoordinateList.add(event.y)
                invalidate()
            }
        }
        return true
    }

    interface OnSignatureSaveListener {
        fun onSignSavedError(errorMsg: String)
        fun onSignSavedSuccess(absoluteFilePath: String)
    }

    fun setOnSignatureSaveListener(listener: OnSignatureSaveListener?) {
        mOnSignatureSaveListener = listener
    }

    fun saveSignature(id: Int) {
        val path = Environment.getExternalStorageDirectory().toString() +
                mContext.resources.getString(R.string.signature_image_directory)
        val signatureStorageDirectory = File(path)
        val signatureFile: File
        if (!signatureStorageDirectory.exists()) {
            val makeRequiredDirectories = signatureStorageDirectory.mkdirs()
            if (!makeRequiredDirectories) {
                mOnSignatureSaveListener?.onSignSavedError(mContext.resources.getString(R.string.sign_dir_not_created_msg))
                return
            }
        }
        try {
            val signatureBitmap = drawingCache
            signatureFile = File(signatureStorageDirectory.path, "$id.jpeg")
            signatureBitmap.compress(
                Bitmap.CompressFormat.JPEG, 100,
                FileOutputStream(signatureFile)
            )
        } catch (e: FileNotFoundException) {
            if (mOnSignatureSaveListener != null) {
                e.message?.let { mOnSignatureSaveListener?.onSignSavedError(it) }
            }
            return
        }
        if (mOnSignatureSaveListener != null) {
            mOnSignatureSaveListener?.onSignSavedSuccess(signatureFile.absolutePath)
        }
    }

    fun clear() {
        mXCoordinateList.clear()
        mYCoordinateList.clear()
        destroyDrawingCache()
        invalidate()
    }

    val xCoordinateSize: Int
        get() = mXCoordinateList.size
    val yCoordinateSize: Int
        get() = mYCoordinateList.size
}