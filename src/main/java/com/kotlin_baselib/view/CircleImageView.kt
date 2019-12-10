package com.kotlin_baselib.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.kotlin_baselib.R

/**
 *  Created by CHEN on 2019/7/9
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.view
 *  Introduce:  圆形图片
 **/
class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {


    companion object {

        private val SCALE_TYPE = ScaleType.CENTER_CROP  //缩放类型

        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888 //图片质量
        private val COLORDRAWABLE_DIMENSION = 1

        private val DEFAULT_BORDER_WIDTH = 0      //默认不带边框
        private val DEFAULT_BORDER_COLOR = Color.BLACK    //默认边框颜色
    }

    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()

    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()

    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH

    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0

    private var mDrawableRadius: Float = 0.toFloat()
    private var mBorderRadius: Float = 0.toFloat()

    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false

    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            if (borderColor == mBorderColor) {
                return
            }

            mBorderColor = borderColor
            mBorderPaint.color = mBorderColor
            invalidate()
        }

    var borderWidth: Int
        get() = mBorderWidth
        set(borderWidth) {
            if (borderWidth == mBorderWidth) {
                return
            }

            mBorderWidth = borderWidth
            setup()
        }

    init {

        val a = context.obtainStyledAttributes(attrs,
                R.styleable.CircleImageView, defStyle, 0)

        mBorderWidth = a.getDimensionPixelSize(
                R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH)
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
                DEFAULT_BORDER_COLOR)

        a.recycle()    //记得回收

        initSetup()
    }

    private fun initSetup() {
        super.setScaleType(SCALE_TYPE)
        mReady = true

        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format(
                    "ScaleType %s not supported.", scaleType))
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }

        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mDrawableRadius,
                mBitmapPaint)
        if (mBorderWidth != 0) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mBorderRadius,
                    mBorderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            val bitmap: Bitmap

            if (drawable is ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                if (drawable.intrinsicWidth > 0) {
                    bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                            drawable.intrinsicHeight, BITMAP_CONFIG)
                } else {
                    bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                            COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
                }
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            return null
        }

    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }

        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP)

        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader

        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()

        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width

        mBorderRect.set(0f, 0f, width.toFloat(), height.toFloat())
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
                (mBorderRect.width() - mBorderWidth) / 2)

        mDrawableRect.set(mBorderWidth.toFloat(), mBorderWidth.toFloat(), mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth)
        mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                mDrawableRect.width() / 2)

        updateShaderMatrix()
        invalidate()
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f

        mShaderMatrix.set(null)

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }

        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(((dx + 0.5f).toInt() + mBorderWidth).toFloat(),
                ((dy + 0.5f).toInt() + mBorderWidth).toFloat())

        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

}