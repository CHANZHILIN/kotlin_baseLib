package com.kotlin_baselib.glide

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.kotlin_baselib.R

/**
 * Created by CHEN on 2019/6/14
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.glide
 * Introduce:   自定义带进度条的ImageView
 */
class ProgressImageView : FrameLayout {

    companion object {
        private val SCALE_TYPE = ImageView.ScaleType.CENTER_CROP  //图片缩放类型
    }


    var imageView: ImageView? = null      //空图片
    var circleProgressBar: CircleProgressBar? = null  //进度条

    private var progressCircleWidth = 6
    private var progressBackgroundColor = Color.parseColor("#F4F4F4")
    private var progressColor = Color.parseColor("#87CB43")
    private var progressTextColor = Color.WHITE
    private var progressSize = 120
    private var progressTextSize = 36

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        imageView = ImageView(context)
        imageView!!.scaleType = SCALE_TYPE
        circleProgressBar = CircleProgressBar(context, attrs)

        val ta = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ProgressImageView,
            defStyleAttr, 0
        )
        val n = ta.indexCount
        for (i in 0 until n) {
            val attr = ta.getIndex(i)
            if (attr == R.styleable.ProgressImageView_progressCircleWidth) {
                progressCircleWidth = ta.getDimensionPixelSize(
                    attr, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics
                    ).toInt()
                ) // 默认圆弧宽度为6dp

            } else if (attr == R.styleable.ProgressImageView_progressBackgroundColor) {
                progressBackgroundColor = ta.getColor(attr, Color.parseColor("#F4F4F4"))

            } else if (attr == R.styleable.ProgressImageView_progressColor) {
                progressColor = ta.getColor(attr, Color.parseColor("#87CB43"))

            } else if (attr == R.styleable.ProgressImageView_progressTextColor) {
                progressTextColor = ta.getColor(attr, Color.BLACK)

            } else if (attr == R.styleable.ProgressImageView_progressSize) {
                progressSize = ta.getDimensionPixelSize(
                    attr, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 120f, resources.displayMetrics
                    ).toInt()
                ) // 默认进度条的大小
            } else if (attr == R.styleable.ProgressImageView_progressTextSize) {
                progressTextSize = ta.getDimensionPixelSize(
                    attr, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics
                    ).toInt()
                ) // 默认文字的大小
            }
        }
        circleProgressBar!!.setCircleWidth(progressCircleWidth)
        circleProgressBar!!.setFirstColor(progressBackgroundColor)
        circleProgressBar!!.setSecondColor(progressColor)
        circleProgressBar!!.setTextPaintColor(progressTextColor)
        circleProgressBar!!.setTextSize(progressTextSize)
        val layoutParams = LayoutParams(progressSize, progressSize) //进度条的大小
        layoutParams.gravity = Gravity.CENTER
        circleProgressBar!!.layoutParams = layoutParams
        addView(imageView)
        addView(circleProgressBar)
    }



}
