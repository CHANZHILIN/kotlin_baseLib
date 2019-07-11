package com.kotlin_baselib.floatview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.kotlin_baselib.R
import com.kotlin_baselib.floatview.FloatingMusicService.Companion.mFloatDeleteView
import com.kotlin_baselib.floatview.FloatingMusicService.Companion.windowManager
import kotlinx.android.synthetic.main.layout_floating_play_music.view.*


/**
 *  Created by CHEN on 2019/7/5
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.floatview
 *  Introduce: 播放音频view
 **/
class FloatingPlayMusicView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var originalWidth: Int = 0       //原始尺寸，这是个bug,为了解决onMeasure不回调问题
    var originalHeight: Int = 0


    var playView: View

    var touchX: Int = 0      //点击的x,y位置
    var touchY: Int = 0

    var isDrag: Boolean = false


    private var floatPlayMusicParams: WindowManager.LayoutParams

    init {

        playView = LayoutInflater.from(context).inflate(R.layout.layout_floating_play_music, null)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        playView.layoutParams = layoutParams
        addView(playView)


        floatPlayMusicParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatPlayMusicParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            floatPlayMusicParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        floatPlayMusicParams.format = PixelFormat.RGBA_8888
        floatPlayMusicParams.gravity = Gravity.START or Gravity.TOP
        floatPlayMusicParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        floatPlayMusicParams.windowAnimations = android.R.style.Animation_Translucent

        windowManager.addView(this, floatPlayMusicParams)
        //设置悬浮窗口的位置
        floatPlayMusicParams.x = 0
        floatPlayMusicParams.y = FloatingMusicService.mHeight / 2


        //播放音频时候动画
        val rotate = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f, 360f)
        rotate.duration = 5000
        rotate.interpolator = LinearInterpolator()
        rotate.repeatCount = -1
        rotate.repeatMode = ObjectAnimator.RESTART


        setOnLongClickListener {
            isDrag = true
            true
        }


        play.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (rotate.isStarted) {
                    rotate.resume()
                } else {
                    rotate.start()
                }
            } else {
                rotate.start()
            }
            mListener?.onPlay()
        }
        pause.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                rotate.pause()
            } else {
                rotate.cancel()
            }
            mListener?.onPause()
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        floatPlayMusicParams.width = playView.measuredWidth
        floatPlayMusicParams.height = playView.measuredHeight
        if (originalWidth == 0) {   //保存第一次的宽高,这是个bug
            originalWidth = playView.measuredWidth
        }
        if (originalHeight == 0) {
            originalHeight = playView.measuredHeight
        }

        windowManager.updateViewLayout(this, floatPlayMusicParams)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX.toInt()
                touchY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrag) {   //长按
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    touchX = nowX
                    touchY = nowY

                    mFloatDeleteView.visibility = View.VISIBLE
                    if (touchX >= FloatingMusicService.mWidth - mFloatDeleteView.mWidth && touchY >= FloatingMusicService.mHeight - mFloatDeleteView.mHeight) {     //在销毁区域
                        mFloatDeleteView.setBackground(true)
                    } else {
                        mFloatDeleteView.setBackground(false)
                    }
                    floatPlayMusicParams.x = nowX - floatPlayMusicParams.width / 2
                    floatPlayMusicParams.y = nowY - floatPlayMusicParams.height / 2
                    windowManager.updateViewLayout(this, floatPlayMusicParams)

                }
            }
            MotionEvent.ACTION_UP -> {
                val upX = event.x
                val upY = event.y
                touchX = event.rawX.toInt()
                touchY = event.rawY.toInt()
                if (!isDrag && upX >= circleImageView.left && upX <= circleImageView.right && upY >= circleImageView.top && upY <= circleImageView.bottom) {
                    if (container_control.visibility == View.GONE) {
                        container_control.visibility = View.VISIBLE
                        if (originalWidth != 0 && originalHeight != 0) {    //也是解决那个bug
                            floatPlayMusicParams.width = originalWidth
                            floatPlayMusicParams.height = originalHeight
                        }
                    } else if (container_control.visibility == View.VISIBLE) {
                        container_control.visibility = View.GONE
                        floatPlayMusicParams.width = originalHeight     //解决在右边点击收缩，不靠边的情况，主要设置缩小的宽度和高度相等，懒得重新计算缩小的宽度
                        val halfOfScreenWidth = FloatingMusicService.mWidth / 2  //屏幕中间线的位置
                        //在右边缩小的情况
                        if (touchX > halfOfScreenWidth) {
                            isDrag = true       //解决在右边点击收缩，不靠边的情况，需要靠边，所以设置为true
                        }
                    }
                    windowManager.updateViewLayout(this, floatPlayMusicParams)
                }
                if (isDrag) {   //只有长按才可以移动，才需要进行贴边处理
                    //悬浮界面贴边
                    val halfOfScreenWidth = FloatingMusicService.mWidth / 2  //屏幕中间线的位置
                    if (touchX > halfOfScreenWidth) { //右侧
                        if (touchX >= FloatingMusicService.mWidth - mFloatDeleteView.mWidth && touchY >= FloatingMusicService.mHeight - mFloatDeleteView.mHeight) {     //在销毁区域
                            windowManager.removeViewImmediate(this)
                            windowManager.removeViewImmediate(mFloatDeleteView)
                            FloatingMusicService.isStarted = false
                            mListener?.onFinishService()
                        } else {
                            floatPlayMusicParams.x = FloatingMusicService.mWidth - floatPlayMusicParams.width
                            windowManager.updateViewLayout(this, floatPlayMusicParams)
                        }

                    } else {      //左侧
                        floatPlayMusicParams.x = 0
                        windowManager.updateViewLayout(this, floatPlayMusicParams)
                    }
                    mFloatDeleteView.visibility = View.GONE
                    isDrag = false
                }
            }

        }
        return super.onTouchEvent(event)
    }

    var mListener: onStatusChange? = null

    interface onStatusChange {
        fun onPlay()
        fun onPause()
        fun onFinishService()
    }

    fun setOnStatusChangeListener(listener: onStatusChange) {
        mListener = listener
    }


}