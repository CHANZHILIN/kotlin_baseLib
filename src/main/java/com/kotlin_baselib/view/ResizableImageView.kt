package com.kotlin_baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.alibaba.android.arouter.utils.Consts.TAG
import com.kotlin_baselib.entity.GraffitiEntity
import com.kotlin_baselib.gestureDetector.TouchGestureDetector
import com.kotlin_baselib.gestureDetector.TouchGestureDetector.OnTouchGestureListener
import kotlin.math.ceil


/**
 *  Created by CHEN on 2019/12/16
 *  Email:1181785848@qq.com
 *  Introduce:  宽度固定，高度自适应的imageView
 **/
class ResizableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private val mPathList: MutableList<GraffitiEntity> = mutableListOf() // 保存涂鸦轨迹的集合
    private val paint: Paint = Paint()
    private var mTouchGestureDetector: TouchGestureDetector? = null// 触摸手势监听
    private var mLastX = 0f
    private var mLastY = 0f
    private var mCurrentGraffitiEntity: GraffitiEntity? = null  // 当前的涂鸦轨迹
    private var mSelectedGraffitiEntity: GraffitiEntity? = null// 选中的涂鸦轨迹


    var paintColor = Color.RED  //画笔颜色
        set(value) {
            field = value
            paint.color = field
            if (mCurrentGraffitiEntity == null)
                mCurrentGraffitiEntity = GraffitiEntity(paint)
            else
                mCurrentGraffitiEntity?.paint?.color = field
        }
    var paintWidth = 5f
        set(value) {
            field = value
            paint.strokeWidth = field
            if (mCurrentGraffitiEntity == null)
                mCurrentGraffitiEntity = GraffitiEntity(paint)
            else
                mCurrentGraffitiEntity?.paint?.strokeWidth = field
        }
    var isGraffiti = false  //是否启动涂鸦功能


    private var mOnClickListener: OnClickListener? = null

    override fun setOnClickListener(l: OnClickListener) {
        //默认的click会在任何点击情况下都会触发，所以搞成自己的
        mOnClickListener = l
    }


    init {

        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND

        // 由手势识别器处理手势
        mTouchGestureDetector =
            TouchGestureDetector(getContext(), object : OnTouchGestureListener() {
                var mRectF = RectF()

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (mSelectedGraffitiEntity == null) {
                        var found = false
                        for (entity in mPathList) { // 绘制涂鸦轨迹
                            entity.path.computeBounds(mRectF, true) // 计算涂鸦轨迹的矩形范围
                            mRectF.offset(entity.mX, entity.mY) // 加上偏移
                            if (mRectF.contains(e.x, e.y)) { // 判断是否点中涂鸦轨迹的矩形范围内
                                found = true
                                mSelectedGraffitiEntity = entity
                                break
                            }
                        }
                        if (!found) { // 没有点中任何涂鸦
                            mSelectedGraffitiEntity = null
                        }
                    }else{
                        mSelectedGraffitiEntity = null
                    }
                    invalidate()
                    return true
                }

        /*        override fun onSingleTapUp(e: MotionEvent): Boolean {
                    var found = false
                    for (entity in mPathList) { // 绘制涂鸦轨迹
                        entity.path.computeBounds(mRectF, true) // 计算涂鸦轨迹的矩形范围
                        mRectF.offset(entity.mX, entity.mY) // 加上偏移
                        if (mRectF.contains(e.x, e.y)) { // 判断是否点中涂鸦轨迹的矩形范围内
                            found = true
                            mSelectedGraffitiEntity = entity
                            break
                        }
                    }
                    if (!found) { // 没有点中任何涂鸦
                        mSelectedGraffitiEntity = null
                    }
                    invalidate()
                    return true
                }*/

                override fun onScrollBegin(e: MotionEvent) { // 滑动开始
                    Log.d(TAG, "onScrollBegin: ")
                    if (isGraffiti) {
                        if (mSelectedGraffitiEntity == null) {
                            if (mCurrentGraffitiEntity == null)
                                mCurrentGraffitiEntity = GraffitiEntity(paint)
                            mPathList.add(mCurrentGraffitiEntity!!) // 添加的集合中
                            mCurrentGraffitiEntity?.path?.moveTo(e.x, e.y)
                            mLastX = e.x
                            mLastY = e.y
                        }
                        invalidate() // 刷新
                    }
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean { // 滑动中
                    Log.d(TAG, "onScroll: " + e2.x + " " + e2.y)
                    if (isGraffiti) {   // 没有选中的涂鸦
                        if (mSelectedGraffitiEntity == null) { // 没有选中的涂鸦
                            mCurrentGraffitiEntity?.path?.quadTo(
                                mLastX,
                                mLastY,
                                (e2.x + mLastX) / 2,
                                (e2.y + mLastY) / 2
                            ) // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                            mLastX = e2.x
                            mLastY = e2.y

                        } else {    // 移动选中的涂鸦
                            mSelectedGraffitiEntity?.mX =
                                mSelectedGraffitiEntity?.mX?.minus(distanceX) ?: 0f
                            mSelectedGraffitiEntity?.mY =
                                mSelectedGraffitiEntity?.mY?.minus(distanceY) ?: 0f
                        }
                        invalidate() // 刷新
                    }
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    //触发点击
                    if (mOnClickListener != null) {
                        mOnClickListener?.onClick(this@ResizableImageView)
                    }
                    return true
                }

                override fun onScrollEnd(e: MotionEvent) { // 滑动结束
                    Log.d(TAG, "onScrollEnd: ")
                    if (isGraffiti) {
                        mCurrentGraffitiEntity?.path?.quadTo(
                            mLastX,
                            mLastY,
                            (e.x + mLastX) / 2,
                            (e.y + mLastY) / 2
                        ) // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                        mCurrentGraffitiEntity = null // 轨迹结束
                        invalidate() // 刷新
                    }
                }
            })


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            val width = MeasureSpec.getSize(widthMeasureSpec)
            //高度根据使得图片的宽度充满屏幕计算而得
            val height =
                ceil((width * d.intrinsicHeight / d.intrinsicWidth).toDouble())
                    .toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val consumed = mTouchGestureDetector?.onTouchEvent(event) // 由手势识别器处理手势
        return if (consumed == false) {
            super.dispatchTouchEvent(event)
        } else true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isGraffiti) {
            // 绘制涂鸦轨迹
            mPathList.forEach { entity ->
                canvas.save(); // 1.保存画布状态，下面要变换画布
                canvas.translate(entity.mX, entity.mY); // 根据涂鸦轨迹偏移值，偏移画布使其画在对应位置上
                canvas.drawPath(entity.path, entity.paint)
                canvas.restore(); // 2.恢复画布状态，绘制完一个涂鸦轨迹后取消上面的画布变换，不影响下一个
            }
        }
    }

}
