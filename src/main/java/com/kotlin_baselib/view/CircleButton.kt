package com.kotlin_baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageButton
import com.kotlin_baselib.R

/**
 *  Created by CHEN on 2019/7/17
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.view
 *  Introduce:
 **/
class CircleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageButton(context, attrs, defStyleAttr) {
    private var bgPaint: Paint
    private var bgOutCirclePaint: Paint
    private var textPaint: Paint

    private var tranparentBgPaint: Paint

    private var circleBackground: Int
    private var circleTextColor: Int

    var text: String

    var textSize: Float


    init {

        text = context.getString(R.string.start)

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CircleButton, defStyleAttr, 0
        )

        circleBackground = a.getColor(
            R.styleable.CircleButton_circle_background,
            context.resources.getColor(R.color.colorPrimary)
        );
        circleTextColor = a.getColor(R.styleable.CircleButton_circle_textColor, Color.WHITE)
        text = a.getText(R.styleable.CircleButton_circle_text) as String
        textSize = a.getDimension(R.styleable.CircleButton_circle_textSize, 34f)


        bgPaint = Paint()
        bgPaint.color = circleBackground
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL

        bgOutCirclePaint = Paint()
        bgOutCirclePaint.color = circleBackground
        bgOutCirclePaint.isAntiAlias = true
        bgOutCirclePaint.strokeWidth = 8f
        bgOutCirclePaint.style = Paint.Style.STROKE

        tranparentBgPaint = Paint()
        tranparentBgPaint.color = Color.TRANSPARENT
        tranparentBgPaint.isAntiAlias = true
        tranparentBgPaint.strokeWidth = 8f
        tranparentBgPaint.style = Paint.Style.STROKE

        textPaint = Paint()
        textPaint.color = circleTextColor
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize

        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(measuredWidth / 2f, measuredWidth / 2f, measuredWidth / 2f - 20f, bgPaint)
        canvas.drawCircle(
            measuredWidth / 2f,
            measuredWidth / 2f,
            measuredWidth / 2f - 18f,
            tranparentBgPaint
        )
        canvas.drawCircle(
            measuredWidth / 2f,
            measuredWidth / 2f,
            measuredWidth / 2f - 8f,
            bgOutCirclePaint
        )

        //根据中线算出baseLine的Y轴坐标，保证字体垂直居中
        val fm = textPaint.getFontMetricsInt()
        val baseLineY = measuredHeight / 2 + (fm.bottom - fm.top) / 2 - fm.bottom

        canvas.drawText(
            text,
            (measuredWidth - textPaint.measureText(text)) / 2f,
            baseLineY.toFloat(),
            textPaint
        )
    }

    fun setButtonText(text: String) {
        this.text = text
        invalidate()
    }

    fun setButtonBackground(color: Int) {
        this.circleBackground = color
        invalidate()
    }

    fun setButtonTextColor(color: Int) {
        this.circleTextColor = color
        invalidate()
    }

    fun setButtonTextSize(size: Float) {
        this.textSize = size
        invalidate()
    }
}