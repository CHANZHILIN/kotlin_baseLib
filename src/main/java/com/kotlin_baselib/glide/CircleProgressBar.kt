package com.kotlin_baselib.glide

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * Introduce :  圆形加载进度条
 * Created by CHEN_ on 2019/1/23.
 * version:1.0
 */
class CircleProgressBar
/**
 * 从xml加载时执行和应用一个特定的风格。这里有两种方式，一是从theme中获得，二是从style中获得。        
 * 第三个参数官方有这样的说明： defStyle - The default style to apply to this view. If 0,
 * no style will be applied (beyond what is included in the theme). This may
 * either be an attribute resource, whose value will be retrieved from the
 * current theme, or an explicit style resource.
 * 默认的风格会被应用到这个view上。如果是0，没有风格将会被应用
 * （除了被包含在主题中）。这个也许是一个属性的资源，它的值是从当前的主题中检索，或者是一个明确的风格资源。
 *
 * @param context      上下文
 * @param attrs        自定义的属性
 * @param defStyleAttr 自定义风格
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    /**
     * 进度条最大值，默认为100
     */
    private val maxValue = 100

    /**
     * 当前进度值
     */
    private var currentValue = 0

    /**
     * 每次扫过的角度，用来设置进度条圆弧所对应的圆心角，alphaAngle=(currentValue/maxValue)*360
     */
    private var alphaAngle: Float = 0.toFloat()

    /**
     * 底部圆弧的颜色，默认为Color.LTGRAY
     */
    private var firstColor = Color.parseColor("#F4F4F4")

    /**
     * 进度条圆弧块的颜色
     */
    private var secondColor = Color.parseColor("#87CB43")

    /**
     * 圆环的宽度
     */
    private var circleWidth = 6

    private val bgPaint: Paint

    /**
     * 画圆弧的画笔
     */
    private val circlePaint: Paint

    /**
     * 画文字的画笔
     */
    private val textPaint: Paint

    /**
     * 进度的文字颜色，默认为黑色
     */
    private var textPaintColor = Color.WHITE

    /**
     * 渐变圆周颜色数组
     */
    private var colorArray = intArrayOf(Color.parseColor("#ffffff"), Color.parseColor("#87CB43"))

    //文字大小
    private var textSize = 36

    init {

        bgPaint = Paint()
        bgPaint.isAntiAlias = true // 抗锯齿
        bgPaint.isDither = true // 防抖动
        bgPaint.color = Color.parseColor("#B2C7C5C5")
        bgPaint.style = Paint.Style.FILL

        circlePaint = Paint()
        circlePaint.isAntiAlias = true // 抗锯齿
        circlePaint.isDither = true // 防抖动
        circlePaint.strokeWidth = circleWidth.toFloat()

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {// 分别获取期望的宽度和高度，并取其中较小的尺寸作为该控件的宽和高
        val measureWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(Math.min(measureWidth, measureHeight), Math.min(measureWidth, measureHeight))
    }

    override fun onDraw(canvas: Canvas) {
        val center = this.width / 2
        val radius = center - circleWidth / 2
        canvas.drawCircle(center.toFloat(), center.toFloat(), radius.toFloat(), bgPaint)
        drawCircle(canvas, center, radius) // 绘制进度圆弧
        drawText(canvas, center)
    }

    /**
     * 绘制进度圆弧
     *
     * @param canvas 画布对象
     * @param center 圆心的x和y坐标
     * @param radius 圆的半径
     */
    private fun drawCircle(canvas: Canvas, center: Int, radius: Int) {
        circlePaint.shader = null // 清除上一次的shader
        circlePaint.color = firstColor // 设置底部圆环的颜色，这里使用第一种颜色
        circlePaint.style = Paint.Style.STROKE // 设置绘制的圆为空心
        canvas.drawCircle(center.toFloat(), center.toFloat(), radius.toFloat(), circlePaint) // 画底部的空心圆
        val oval = RectF(
            (center - radius).toFloat(),
            (center - radius).toFloat(),
            (center + radius).toFloat(),
            (center + radius).toFloat()
        ) // 圆的外接正方形

        // 绘制颜色渐变圆环
        // shader类是Android在图形变换中非常重要的一个类。Shader在三维软件中我们称之为着色器，其作用是来给图像着色。
        val linearGradient = LinearGradient(
            circleWidth.toFloat(),
            circleWidth.toFloat(),
            (measuredWidth - circleWidth).toFloat(),
            (measuredHeight - circleWidth).toFloat(),
            colorArray,
            null,
            Shader.TileMode.MIRROR
        )
        circlePaint.shader = linearGradient
        //        circlePaint.setShadowLayer(10, 10, 10, Color.RED);
        circlePaint.color = secondColor // 设置圆弧的颜色
        circlePaint.strokeCap = Paint.Cap.ROUND // 把每段圆弧改成圆角的

        alphaAngle = currentValue * 360.0f / maxValue * 1.0f // 计算每次画圆弧时扫过的角度，这里计算要注意分母要转为float类型，否则alphaAngle永远为0
        canvas.drawArc(oval, -90f, alphaAngle, false, circlePaint)
    }

    /**
     * 绘制文字
     */
    private fun drawText(canvas: Canvas, center: Int) {
        val result = currentValue * 100.0f / maxValue * 1.0f // 计算进度
        val percent = String.format("%.1f", result) + "%"

        textPaint.textAlign = Paint.Align.CENTER // 设置文字居中，文字的x坐标要注意
        textPaint.color = textPaintColor // 设置文字颜色
        textPaint.textSize = textSize.toFloat() // 设置要绘制的文字大小
        textPaint.strokeWidth = 0f // 注意此处一定要重新设置宽度为0,否则绘制的文字会重叠
        val bounds = Rect() // 文字边框
        textPaint.getTextBounds(percent, 0, percent.length, bounds) // 获得绘制文字的边界矩形
        val fontMetrics = textPaint.fontMetricsInt // 获取绘制Text时的四条线
        val baseline =
            center + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom // 计算文字的基线,方法见http://blog.csdn.net/harvic880925/article/details/50423762
        canvas.drawText(percent, center.toFloat(), baseline.toFloat(), textPaint) // 绘制表示进度的文字
    }

    /**
     * 设置圆环的宽度
     *
     * @param width
     */
    fun setCircleWidth(width: Int) {
        this.circleWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, width.toFloat(), resources
                .displayMetrics
        ).toInt()
        circlePaint.strokeWidth = circleWidth.toFloat()
        invalidate()
    }

    /**
     * 设置圆环的底色，默认为亮灰色LTGRAY
     *
     * @param color
     */
    fun setFirstColor(color: Int) {
        this.firstColor = color
        circlePaint.color = firstColor
        invalidate()
    }

    /**
     * 设置进度条的颜色，默认为绿色
     *
     * @param color
     */
    fun setSecondColor(color: Int) {
        this.secondColor = color
        circlePaint.color = secondColor
        invalidate()
    }

    /**
     * 设置进度条渐变色颜色数组
     *
     * @param colors 颜色数组，类型为int[]
     */
    fun setColorArray(colors: IntArray) {
        this.colorArray = colors
        invalidate()
    }


    fun setTextSize(textSize: Int) {
        this.textSize = textSize
        textPaint.textSize = textSize.toFloat()
        invalidate()
    }

    /**
     * 设置进度的画笔颜色
     *
     * @param color
     */
    fun setTextPaintColor(color: Int) {
        this.textPaintColor = color
        textPaint.color = textPaintColor
        invalidate()
    }

    /**
     * 按进度显示百分比
     *
     * @param progress 进度，值通常为0到100
     */
    fun setProgress(progress: Int) {

        var percent = progress * maxValue / 100
        if (percent < 0) {
            percent = 0
        }
        if (percent > 100) {
            percent = 100
        }
        this.currentValue = percent
//        invalidate()
        postInvalidate()
    }

    /**
     * 按进度显示百分比，可选择是否启用数字动画
     *
     * @param progress     进度，值通常为0到100
     * @param useAnimation 是否启用动画，true为启用
     */
    fun setProgress(progress: Int, useAnimation: Boolean) {
        var percent = progress * maxValue / 100
        if (percent < 0) {
            percent = 0
        }
        if (percent > 100) {
            percent = 100
        }
        if (useAnimation) {// 使用动画
            val animator = ValueAnimator.ofInt(0, percent)

//            post() //切换回主线程，避免发生在子线程更新UI导致崩溃的bug
//            {
                animator.addUpdateListener { animation ->
                    currentValue = animation.animatedValue as Int
                    postInvalidate()
                }
                animator.interpolator = OvershootInterpolator()
                animator.duration = 1000
                animator.start()
//            }
        } else {
            setProgress(progress)
        }
    }
}