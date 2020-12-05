package com.kotlin_baselib.floatview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kotlin_baselib.floatview.FloatingMusicService.Companion.windowManager

/**
 *  Created by CHEN on 2019/7/6
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.floatview
 *  Introduce: 删除浮动窗口的view
 **/
class FloatingDeleteView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mPaint: Paint
    var mWidth: Float = 300f
    var mHeight: Float = 300f
    //    var oval: RectF
    var mTextPaint: Paint
    var mStartPoint: PointF
    var mEndPoint: PointF
    var mControlPoint: PointF

    var mPath: Path

    private var deleteLayoutParams: WindowManager.LayoutParams =
        WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)


    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            deleteLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        deleteLayoutParams.format = PixelFormat.RGBA_8888
        deleteLayoutParams.gravity = Gravity.START or Gravity.TOP
        deleteLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        deleteLayoutParams.windowAnimations = android.R.style.Animation_Translucent

        deleteLayoutParams.width = this.mWidth.toInt()
        deleteLayoutParams.height = this.mHeight.toInt()
        deleteLayoutParams.x = FloatingMusicService.mWidth
        deleteLayoutParams.y = FloatingMusicService.mHeight
        windowManager.addView(this, deleteLayoutParams)


        mPaint = Paint()
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL

        mStartPoint = PointF(0f, 0f)
        mEndPoint = PointF(0f, 0f)
        mControlPoint = PointF(0f, 0f)

        mPath = Path()

//        oval = RectF(0f, 0f, mWidth * 2, mHeight * 2)

        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = 32f
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mStartPoint.x = 0f
        mStartPoint.y = mHeight
        mEndPoint.x = mWidth
        mEndPoint.y = 0f
        mControlPoint.x = 0f
        mControlPoint.y = 0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.reset()
//        canvas.drawArc(oval, 180f, 90f, true, mPaint)
        mPath.moveTo(mStartPoint.x, mStartPoint.y)
        //绘制贝塞尔曲线
        mPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y)
        mPath.lineTo(mWidth, mHeight)
        mPath.lineTo(0f, mHeight)
        canvas.drawPath(mPath, mPaint)

        canvas.rotate(-45f)
        canvas.drawText("拖拽到此处关闭", 0f, mHeight / 1.2f, mTextPaint)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {        //可见
            animate().alpha(1.0f).setDuration(500).start()
        } else {
            animate().alpha(0f).setDuration(500).start()
        }
    }

    fun setBackground(isInside: Boolean) {
        if (isInside)
            mPaint.color = -0x3ef9
        else
            mPaint.color = Color.RED
        invalidate()
    }


}