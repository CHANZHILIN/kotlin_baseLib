package com.kotlin_baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.kotlin_baselib.api.Constants.Companion.DEBUG_TAG
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


    /* private var mBitmap: Bitmap? = null
     private var mBitmapTransX = 0f
     private var mBitmapTransY = 0f
     private var mBitmapScale = 1f*/


    var paintColor = Color.RED  //画笔颜色
        set(value) {
            field = value
            paint.color = field
            if (mCurrentGraffitiEntity == null) {
                mCurrentGraffitiEntity = GraffitiEntity(paint)
                mPathList.add(mCurrentGraffitiEntity!!) // 添加的集合中
            }
            else
                mCurrentGraffitiEntity?.paint?.color = field
        }
    var paintWidth = 5f
        set(value) {
            field = value
            paint.strokeWidth = field
            if (mCurrentGraffitiEntity == null) {
                mCurrentGraffitiEntity = GraffitiEntity(paint)
                mPathList.add(mCurrentGraffitiEntity!!) // 添加的集合中
            }
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

                /*  // 缩放手势操作相关
                  var mLastFocusX: Float? = null
                  var mLastFocusY: Float? = null
                  var mTouchCentreX = 0f
                  var mTouchCentreY = 0f*/

                /*   override fun onScaleBegin(detector: ScaleGestureDetectorApi27?): Boolean {
                       Log.d(DEBUG_TAG, "onScaleBegin: ")
                       mLastFocusX = null
                       mLastFocusY = null
                       return true
                   }

                   override fun onScaleEnd(detector: ScaleGestureDetectorApi27?) {
                       Log.d(DEBUG_TAG, "onScaleEnd: ")
                   }

                   override fun onScale(detector: ScaleGestureDetectorApi27?): Boolean {
                       Log.d(DEBUG_TAG, "onScale: ")
                       // 屏幕上的焦点
                       mTouchCentreX = detector!!.focusX
                       mTouchCentreY = detector.focusY

                       if (mLastFocusX != null && mLastFocusY != null) { // 焦点改变
                           val dx = mTouchCentreX - mLastFocusX!!
                           val dy = mTouchCentreY - mLastFocusY!!
                           // 移动图片
                           mBitmapTransX += dx
                           mBitmapTransY += dy
                       }
                       // 缩放图片
                       mBitmapScale *= detector.scaleFactor
                       if (mBitmapScale < 0.1f) {
                           mBitmapScale = 0.1f
                       }
                       invalidate()

                       mLastFocusX = mTouchCentreX
                       mLastFocusY = mTouchCentreY

                       return true
                   }
   */
                override fun onDoubleTap(e: MotionEvent): Boolean {
//                    val x = toX(e.x)
//                    val y = toY(e.y)
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
                    } else {
                        mSelectedGraffitiEntity = null
                    }
                    invalidate()
                    return true
                }

                override fun onScrollBegin(e: MotionEvent) { // 滑动开始
                    Log.d(DEBUG_TAG, "onScrollBegin: ")
//                    val x = toX(e.x)
//                    val y = toY(e.y)
                    if (isGraffiti) {
                        mHandler?.sendEmptyMessage(FLAG_STOP)
                        if (mSelectedGraffitiEntity == null) {
                            if (mCurrentGraffitiEntity == null) {
                                mCurrentGraffitiEntity = GraffitiEntity(paint)
                                mPathList.add(mCurrentGraffitiEntity!!) // 添加的集合中
                            }
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
                    Log.d(DEBUG_TAG, "onScroll: " + e2.x + " " + e2.y)
//                    val x = toX(e2.x)
//                    val y = toY(e2.y)
                    if (isGraffiti) {   // 没有选中的涂鸦
                        if (mSelectedGraffitiEntity == null) { // 没有选中的涂鸦
                            mCurrentGraffitiEntity?.path?.quadTo(
                                mLastX,
                                mLastY,
                                (e2.x + mLastX) / 2,
                                (e2.y + mLastY) / 2
                            ) // 使用贝塞尔曲线 让涂鸦轨迹更圆滑


                        } else {    // 移动选中的涂鸦
                            mSelectedGraffitiEntity?.mX =
                                mSelectedGraffitiEntity?.mX?.minus(distanceX) ?: 0f
                            mSelectedGraffitiEntity?.mY =
                                mSelectedGraffitiEntity?.mY?.minus(distanceY) ?: 0f
                        }
                    }
                    mLastX = e2.x
                    mLastY = e2.y
                    invalidate() // 刷新
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
                    Log.d(DEBUG_TAG, "onScrollEnd: ")
//                    val x = toX(e.x)
//                    val y = toY(e.y)
                    if (isGraffiti) {
                        mCurrentGraffitiEntity?.path?.quadTo(
                            mLastX,
                            mLastY,
                            (e.x + mLastX) / 2,
                            (e.y + mLastY) / 2
                        ) // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                        mHandler?.sendEmptyMessageDelayed(FLAG_START,2000)
                        invalidate() // 刷新
                    }
                }
            })

        // 针对涂鸦的手势参数设置
        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mTouchGestureDetector?.setScaleSpanSlop(1); // 手势前识别为缩放手势的双指滑动最小距离值
        mTouchGestureDetector?.setScaleMinSpan(1); // 缩放过程中识别为缩放手势的双指最小距离值
        mTouchGestureDetector?.setIsScrollAfterScaled(false);
    }

/*
    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldw: Int,
        oldh: Int
    ) { //view绘制完成时 大小确定
        super.onSizeChanged(width, height, oldw, oldh)
        val w = mBitmap?.width ?: 0
        val h = mBitmap?.height ?: 0
        val nw = w * 1f / getWidth()
        val nh = h * 1f / getHeight()
        val centerWidth: Float
        val centerHeight: Float
        // 1.计算使图片居中的缩放值
        if (nw > nh) {
            mBitmapScale = 1 / nw
            centerWidth = getWidth().toFloat()
            centerHeight = (h * mBitmapScale)
        } else {
            mBitmapScale = 1 / nh
            centerWidth = (w * mBitmapScale)
            centerHeight = getHeight().toFloat()
        }
        // 2.计算使图片居中的偏移值
        mBitmapTransX = (getWidth() - centerWidth) / 2f
        mBitmapTransY = (getHeight() - centerHeight) / 2f
        invalidate()
    }
*/


/*    */
    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     *//*
    fun toX(touchX: Float): Float {
        return (touchX - mBitmapTransX) / mBitmapScale
    }

    */
    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     *//*
    fun toY(touchY: Float): Float {
        return (touchY - mBitmapTransY) / mBitmapScale
    }*/


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

    private var mRectF = RectF()
    private val selectBouncePaint: Paint = Paint().apply {
        color = Color.parseColor("#4DFF3D00")
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画布和图片共用一个坐标系，只需要处理屏幕坐标系到图片（画布）坐标系的映射关系(toX toY)
//        canvas.translate(mBitmapTransX, mBitmapTransY);
//        canvas.scale(mBitmapScale, mBitmapScale);
//         绘制图片
//        if (mBitmap != null) {
//            Log.e(DEBUG_TAG, "bitmap=${mBitmap.toString()}")
//            canvas.drawBitmap(mBitmap!!, 0f, 0f, null);
//        }
        if (isGraffiti) {
            // 绘制涂鸦轨迹
            mPathList.forEach { entity ->
                canvas.save(); // 1.保存画布状态，下面要变换画布
                canvas.translate(entity.mX, entity.mY); // 根据涂鸦轨迹偏移值，偏移画布使其画在对应位置上
                if (mSelectedGraffitiEntity == entity) {
                    entity.path.computeBounds(mRectF, true)
                    val bounce = entity.paint.strokeWidth / 2
                    mRectF.inset(-bounce, -bounce)  //使得阴影包含所有轨迹
                    canvas.drawRect(mRectF, selectBouncePaint)
                }
                canvas.drawPath(entity.path, entity.paint)
                canvas.restore(); // 2.恢复画布状态，绘制完一个涂鸦轨迹后取消上面的画布变换，不影响下一个
            }
        }
    }

    private var mHandler: Handler? = Handler{
        when(it.what){
            FLAG_START ->{
                mCurrentGraffitiEntity = null // 轨迹结束
                Log.e(DEBUG_TAG,"1111")
            }
            FLAG_STOP ->{
                it.target.removeCallbacksAndMessages(null)
                Log.e(DEBUG_TAG,"2222")
            }
        }
        false
    }
    companion object {
        private const val FLAG_START = 0
        private const val FLAG_STOP = 1
    }


//    fun setImageUrl(path: String) {
////        GlideUtil.instance.loadImage(context, path, this)
//        mBitmap = BitmapFactory.decodeFile(path)
//        setImageBitmap(mBitmap)
//    }
}
