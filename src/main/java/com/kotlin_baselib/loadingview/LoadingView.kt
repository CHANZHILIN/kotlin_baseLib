package com.kotlin_baselib.loadingview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*

/**
 *  Created by CHEN on 2019/6/14
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.loadingview
 *  Introduce: 加载动画
 **/
class LoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr) {
    //静止状态
    private val STATUS_STILL = 0
    //加载状态
    private val STATUS_LOADING = 1
    //线条最大长度
    private val MAX_LINE_LENGTH = 120
    //线条最短长度
    private val MIN_LINE_LENGTH = 40
    //最大间隔时长
    private val MAX_DURATION = 3000
    //最小间隔时长
    private val MIN_DURATION = 500

    private var mPaint: Paint? = null
    private val mColors = intArrayOf(-0x4f813426, -0x4f1956d4, -0x4f29feb3, -0x4fa5456c)
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //动画间隔时长
    private var mDuration = MIN_DURATION
    //线条总长度
    private var mEntireLineLength = MIN_LINE_LENGTH
    //圆半径
    private var mCircleRadius: Int = 0
    //所有动画
    private val mAnimList = ArrayList<Animator>()
    //Canvas起始旋转角度
    private val CANVAS_ROTATE_ANGLE = 60
    //动画当前状态
    private var mStatus = STATUS_STILL
    //Canvas旋转角度
    private var mCanvasAngle: Int = 0
    //线条长度
    private var mLineLength: Float = 0.toFloat()
    //半圆Y轴位置
    private var mCircleY: Float = 0.toFloat()
    //第几部动画
    private var mStep: Int = 0

    init {
        initView()
    }

    private fun initView() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = mColors[0]
        mPaint!!.strokeCap = Paint.Cap.ROUND
    }

    private fun initData() {
        mCanvasAngle = CANVAS_ROTATE_ANGLE
        mLineLength = mEntireLineLength.toFloat()
        mCircleRadius = mEntireLineLength / 5
        mPaint!!.strokeWidth = (mCircleRadius * 2).toFloat()
        mStep = 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        initData()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (mStep % 4) {
            0 -> for (i in mColors.indices) {
                mPaint!!.color = mColors[i]
                drawCRLC(
                        canvas,
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        mHeight / 2 - mLineLength,
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        (mHeight / 2 + mEntireLineLength).toFloat(),
                        mPaint!!,
                        mCanvasAngle + i * 90
                )
            }
            1 -> for (i in mColors.indices) {
                mPaint!!.color = mColors[i]
                drawCR(
                        canvas,
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        (mHeight / 2 + mEntireLineLength).toFloat(),
                        mPaint!!,
                        mCanvasAngle + i * 90
                )
            }
            2 -> for (i in mColors.indices) {
                mPaint!!.color = mColors[i]
                drawCRCC(
                        canvas,
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        mHeight / 2 + mCircleY,
                        mPaint!!,
                        mCanvasAngle + i * 90
                )
            }
            3 -> for (i in mColors.indices) {
                mPaint!!.color = mColors[i]
                drawLC(
                        canvas,
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        (mHeight / 2 + mEntireLineLength).toFloat(),
                        mWidth / 2 - mEntireLineLength / 2.2f,
                        mHeight / 2 + mLineLength,
                        mPaint!!,
                        mCanvasAngle + i * 90
                )
            }
        }

    }

    private fun drawCRLC(
            canvas: Canvas,
            startX: Float,
            startY: Float,
            stopX: Float,
            stopY: Float,
            paint: Paint,
            rotate: Int
    ) {
        canvas.rotate(rotate.toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        canvas.drawLine(startX, startY, stopX, stopY, paint)
        canvas.rotate((-rotate).toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
    }

    private fun drawCR(canvas: Canvas, x: Float, y: Float, paint: Paint, rotate: Int) {
        canvas.rotate(rotate.toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        canvas.drawCircle(x, y, mCircleRadius.toFloat(), paint)
        canvas.rotate((-rotate).toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
    }

    private fun drawCRCC(canvas: Canvas, x: Float, y: Float, paint: Paint, rotate: Int) {
        canvas.rotate(rotate.toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        canvas.drawCircle(x, y, mCircleRadius.toFloat(), paint)
        canvas.rotate((-rotate).toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
    }

    private fun drawLC(
            canvas: Canvas,
            startX: Float,
            startY: Float,
            stopX: Float,
            stopY: Float,
            paint: Paint,
            rotate: Int
    ) {
        canvas.rotate(rotate.toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        canvas.drawLine(startX, startY, stopX, stopY, paint)
        canvas.rotate((-rotate).toFloat(), (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
    }

    /**
     * Animation1
     * 动画1
     * Canvas Rotate Line Change
     * 画布旋转及线条变化动画
     */
    private fun startCRLCAnim() {

        val animList = ArrayList<Animator>()

        val canvasRotateAnim = ValueAnimator.ofInt(CANVAS_ROTATE_ANGLE + 0, CANVAS_ROTATE_ANGLE + 360)
        canvasRotateAnim.addUpdateListener { animation -> mCanvasAngle = animation.animatedValue as Int }

        animList.add(canvasRotateAnim)

        val lineWidthAnim = ValueAnimator.ofFloat(mEntireLineLength.toFloat(), (-mEntireLineLength).toFloat())
        lineWidthAnim.addUpdateListener { animation ->
            mLineLength = animation.animatedValue as Float
            invalidate()
        }

        animList.add(lineWidthAnim)

        val animationSet = AnimatorSet()
        animationSet.duration = mDuration.toLong()
        animationSet.playTogether(animList)
        animationSet.interpolator = LinearInterpolator()
        animationSet.addListener(object : AnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
//                Log.d("@=>", "动画1结束")
                if (mStatus == STATUS_LOADING) {
                    mStep++
                    startCRAnim()
                }
            }
        })
        animationSet.start()

        mAnimList.add(animationSet)
    }

    /**
     * Animation2
     * 动画2
     * Canvas Rotate
     * 画布旋转动画
     */
    private fun startCRAnim() {
        val canvasRotateAnim = ValueAnimator.ofInt(mCanvasAngle, mCanvasAngle + 180)
        canvasRotateAnim.duration = (mDuration / 2).toLong()
        canvasRotateAnim.interpolator = LinearInterpolator()
        canvasRotateAnim.addUpdateListener { animation ->
            mCanvasAngle = animation.animatedValue as Int
            invalidate()
        }
        canvasRotateAnim.addListener(object : AnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
//                Log.d("@=>", "动画2结束")
                if (mStatus == STATUS_LOADING) {
                    mStep++
                    startCRCCAnim()
                }
            }
        })
        canvasRotateAnim.start()

        mAnimList.add(canvasRotateAnim)
    }

    /**
     * Animation3
     * 动画3
     * Canvas Rotate Circle Change
     * 画布旋转圆圈变化动画
     */
    private fun startCRCCAnim() {
        val animList = ArrayList<Animator>()

        val canvasRotateAnim = ValueAnimator.ofInt(mCanvasAngle, mCanvasAngle + 90, mCanvasAngle + 180)
        canvasRotateAnim.addUpdateListener { animation -> mCanvasAngle = animation.animatedValue as Int }

        animList.add(canvasRotateAnim)

        val circleYAnim = ValueAnimator.ofFloat(
                mEntireLineLength.toFloat(),
                (mEntireLineLength / 4).toFloat(), mEntireLineLength.toFloat()
        )
        circleYAnim.addUpdateListener { animation ->
            mCircleY = animation.animatedValue as Float
            invalidate()
        }

        animList.add(circleYAnim)

        val animationSet = AnimatorSet()
        animationSet.duration = mDuration.toLong()
        animationSet.playTogether(animList)
        animationSet.interpolator = LinearInterpolator()
        animationSet.addListener(object : AnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
//                Log.d("@=>", "动画3结束")
                if (mStatus == STATUS_LOADING) {
                    mStep++
                    startLCAnim()
                }
            }
        })
        animationSet.start()

        mAnimList.add(animationSet)
    }

    /**
     * Animation4
     * 动画4
     * Line Change
     * 线条变化动画
     */
    private fun startLCAnim() {
        val lineWidthAnim = ValueAnimator.ofFloat(
                (mEntireLineLength - dp2px(context, 1f)).toFloat(),
                (-mEntireLineLength).toFloat()
        )
        lineWidthAnim.duration = mDuration.toLong()
        lineWidthAnim.interpolator = LinearInterpolator()
        lineWidthAnim.addUpdateListener { animation ->
            mLineLength = animation.animatedValue as Float
            invalidate()
        }
        lineWidthAnim.addListener(object : AnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
//                Log.d("@=>", "动画4结束")
                if (mStatus == STATUS_LOADING) {
                    mStep++
                    startCRLCAnim()
                }
            }
        })
        lineWidthAnim.start()

        mAnimList.add(lineWidthAnim)
    }

    fun setLineLength(scale: Float) {
        mEntireLineLength = (scale * (MAX_LINE_LENGTH - MIN_LINE_LENGTH)).toInt() + MIN_LINE_LENGTH
        reset()
    }

    fun setDuration(scale: Float) {
        mDuration = (scale * (MAX_DURATION - MIN_DURATION)).toInt() + MIN_DURATION
        reset()
    }

    fun start() {
        if (mStatus == STATUS_STILL) {
            mAnimList.clear()
            mStatus = STATUS_LOADING
            startCRLCAnim()
        }
    }

    fun reset() {
        if (mStatus == STATUS_LOADING) {
            mStatus = STATUS_STILL
            for (anim in mAnimList) {
                anim.cancel()
            }
        }
        initData()
        invalidate()
    }


    private fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}
