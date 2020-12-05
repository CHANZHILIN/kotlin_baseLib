@file:Suppress("unused")

package com.kotlin_baselib.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import android.view.animation.CycleInterpolator
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kotlin_baselib.R


/**
 * [View.postDelayed]的舒适版本
 */
fun View.postDelay(delay: Long, action: () -> Unit) {
    try {
        postDelayed(action, delay)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * [Handler.postDelayed]的舒适版本
 */
fun Handler.postDelay(delay: Long, action: () -> Unit) {
    try {
        postDelayed(action, delay)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.safePost(action: () -> Unit) {
    try {
        post(action)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.safePostDelay(delay: Long, action: () -> Unit) {
    try {
        postDelayed(action, delay)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Handler.safePost(action: () -> Unit) {
    try {
        post(action)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


class TextChangeListener {

    internal var actionAfterTextChanged: ((text: Editable?) -> Unit)? = null
    internal var actionBeforeTextChanged: ((text: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null
    internal var actionOnTextChanged: ((text: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null

    fun afterTextChanged(action: (text: Editable?) -> Unit) {
        actionAfterTextChanged = action
    }

    fun beforeTextChanged(action: (text: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        actionBeforeTextChanged = action
    }

    fun onTextChanged(action: (text: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
        actionOnTextChanged = action
    }
}

/**
 * 简化代码
 */
fun EditText.addTextWatcher(watcher: TextChangeListener.() -> Unit) {
    val listener = TextChangeListener().also(watcher)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

            listener.actionAfterTextChanged?.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            listener.actionBeforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.actionOnTextChanged?.invoke(s, start, before, count)
        }
    })
}

class AnimatorListeners {

    internal var onAnimationRepeat: ((animation: Animator?) -> Unit)? = null
    internal var onAnimationEnd: ((animation: Animator?) -> Unit)? = null
    internal var onAnimationCancel: ((animation: Animator?) -> Unit)? = null
    internal var onAnimationStart: ((animation: Animator?) -> Unit)? = null

    fun onAnimationRepeat(action: (animation: Animator?) -> Unit) {
        onAnimationRepeat = action
    }

    fun onAnimationEnd(action: (animation: Animator?) -> Unit) {
        onAnimationEnd = action
    }

    fun onAnimationCancel(action: (animation: Animator?) -> Unit) {
        onAnimationCancel = action
    }

    fun onAnimationStart(action: (animation: Animator?) -> Unit) {
        onAnimationStart = action
    }
}

/**
 * 简化Animator 监听 代码
 */
fun ViewPropertyAnimator?.addListener(listener: AnimatorListeners.() -> Unit): ViewPropertyAnimator? {
    val listeners: AnimatorListeners? = AnimatorListeners().also(listener)
    this?.setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            listeners?.onAnimationRepeat?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            listeners?.onAnimationEnd?.invoke(animation)

        }

        override fun onAnimationCancel(animation: Animator?) {
            listeners?.onAnimationCancel?.invoke(animation)

        }

        override fun onAnimationStart(animation: Animator?) {
            listeners?.onAnimationStart?.invoke(animation)

        }
    })
    return this
}

class PageChangeListener {
    internal var actionPageScrolled: ((var1: Int, var2: Float, var3: Int) -> Unit)? = null
    internal var actionPageSelected: ((var1: Int) -> Unit)? = null
    internal var actionPageScrollStateChanged: ((var1: Int) -> Unit)? = null

    fun onPageScrolled(action: (var1: Int, var2: Float, var3: Int) -> Unit) {
        actionPageScrolled = action
    }

    fun onPageSelected(action: (var1: Int) -> Unit) {
        actionPageSelected = action
    }

    fun onPageScrollStateChanged(action: (var1: Int) -> Unit) {
        actionPageScrollStateChanged = action
    }
}

fun ViewPager.addOnPageChangeListener(l: PageChangeListener.() -> Unit) {
    val listener:PageChangeListener? = PageChangeListener().also(l)
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {
            listener?.actionPageScrollStateChanged?.invoke(p0)
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            listener?.actionPageScrolled?.invoke(p0, p1, p2)

        }

        override fun onPageSelected(p0: Int) {
            listener?.actionPageSelected?.invoke(p0)

        }
    })
}

interface OnPageChangeListener {
    fun onPageScrolled(var1: Int, var2: Float, var3: Int)
    fun onPageSelected(var1: Int)
    fun onPageScrollStateChanged(var1: Int)
}

/**
 * 过滤空格
 */
fun getTextWithoutSpace(s: String?): String? {
    return s?.replace(" ", "")
}

/**
 * View截图
 */
fun View?.toBitmap(needAlpha: Boolean = false): Bitmap? {
    return if (this == null) {
        null
    } else {
        if (width == 0 || height == 0) {
            return null
        }
        val bmp = Bitmap.createBitmap(
                width,
                height,
                if (needAlpha) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val c = Canvas(bmp)
        if (!needAlpha) {
            c.drawColor(Color.WHITE)
        }
        draw(c)
        bmp
    }
}

fun View?.stopAnimation() {
    if (this != null) {
        this.animation?.cancel()
        val animate = this.animate()
        animate.cancel()
        animate.setListener(null)
        val animator = this.getTag(R.id.animator) as? ValueAnimator?
        animator?.cancel()
        animator?.removeAllUpdateListeners()
        animator?.removeAllListeners()
    }
}

fun View.doOnPreDraw(action: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            action()
            viewTreeObserver.removeOnPreDrawListener(this)
            return false
        }
    })
}

fun View.OnGlobalLayout(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            //只需要获取一次高度，获取后移除监听器
            if (viewTreeObserver != null)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun View.doOnLayoutChangeListener(action: (Boolean) -> Unit) {
    val l = View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        if (bottom - oldBottom < -1) {
            //软键盘弹上去了,动态设置高度为0
            action.invoke(true)
        } else if (bottom - oldBottom > 1) {
            //软键盘弹下去了，动态设置高度，恢复原先控件高度
            //（"1"这个高度值可以换做：屏幕高度的1/3）
            action.invoke(false)
        }
    }
    this.removeOnLayoutChangeListener(l)
    this.addOnLayoutChangeListener(l)
}

fun View.doOnGlobalLayout(height: Int) {
    if (height > 0)
        viewTreeObserver.addOnGlobalLayoutListener(OnViewGlobalLayoutListener(this, height))
}

class OnViewGlobalLayoutListener(private val view: View, height: Int) :
        ViewTreeObserver.OnGlobalLayoutListener {
    private var maxHeight = 100

    init {
        this.maxHeight = height
    }

    override fun onGlobalLayout() {
        MyLog.e("--------view.height----${view.height}  maxHeight= $maxHeight")
        if (view.height > maxHeight) {
            view.layoutParams.height = maxHeight
        }
        if (view.viewTreeObserver != null)
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)

    }
}


/**
 * 简化代码
 */
class RecyclerViewScrollListener {
    internal var actionScrollStateChanged: ((recyclerView: RecyclerView?, newState: Int) -> Unit)? = null
    internal var actionScrolled: ((recyclerView: RecyclerView?, dx: Int, dy: Int) -> Unit)? = null
    fun onScrollStateChanged(action: (recyclerView: RecyclerView?, newState: Int) -> Unit) {
        actionScrollStateChanged = action
    }

    fun onScrolled(action: (recyclerView: RecyclerView?, dx: Int, dy: Int) -> Unit) {
        actionScrolled = action
    }
}

fun RecyclerView.addOnScrollListeners(mRecyclerViewScrollListener: RecyclerViewScrollListener.() -> Unit) {
    val listener: RecyclerViewScrollListener? = RecyclerViewScrollListener().also(mRecyclerViewScrollListener)
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            listener?.actionScrollStateChanged?.invoke(recyclerView, newState)
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
             listener?.actionScrolled?.invoke(recyclerView, dx,dy)
        }
    })
}

fun View.setPaddingTop(top: Int) {
    setPadding(paddingLeft, top, paddingRight, paddingBottom)
}

fun View.setPaddingTopBy(top: Int) {
    setPadding(paddingLeft, paddingTop + top, paddingRight, paddingBottom)
}

fun View.setPaddingLeft(left: Int) {
    setPadding(left, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingRight(right: Int) {
    setPadding(paddingLeft, paddingTop, right, paddingBottom)
}

fun View.setPaddingBottom(bottom: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, bottom)
}

//点击部分
fun View.onClick(wait: Long = 600, block: ((View) -> Unit)) {
    setOnClickListener(throttleClick(wait, block))
}

fun throttleClick(wait: Long = 600, block: ((View) -> Unit)): View.OnClickListener {

    return View.OnClickListener { v ->
        val current = System.currentTimeMillis()
        val lastClickTime = (v.getTag(R.id.ds_click_debounce_action) as? Long) ?: 0
        val d = current - lastClickTime
//        YzLog.e("----throttleClick-----current=$current  lastClickTime=$lastClickTime")
        v.setTag(R.id.ds_click_debounce_action, current)
        if (d > wait) {
            block(v)
        }
    }
}

//touch部分
fun View.onTouch(wait: Long = 300, block: ((View) -> Unit)) {
    setOnTouchListener(throttleTouch(wait, block))
}

/**
 * 抖动
 */
fun View?.shakeInput() {
    this?.animate()
            ?.setInterpolator(CycleInterpolator(2F))
            ?.translationX(10f)
            ?.setDuration(500)
            ?.start()
}

//show type 0: toLeft 1:toRight
fun View?.toShow(time: Long? = 250, type: Int? = 0, listener: (() -> Unit)? = null) {
    val view: View? = this
    if (true == view?.isShown) return
    view?.alpha = 0f
//    YzLog.e("View.toShow----------translationX= ${view?.translationX}")
    view?.translationX = if (type == 1) -800f else 800f
    view?.visibility = View.VISIBLE
    view?.animate()
            ?.addListener {
                onAnimationEnd {
                    listener?.invoke()
                    view?.visibility = View.VISIBLE
                    view?.alpha = 1f
                    view?.translationX = 0f
                }
            }
            ?.setDuration(time ?: 250)
            ?.translationX(0f)
            ?.alpha(1f)
            ?.start()

}

//hint type 0: toLeft 1:toRight
fun View?.toHint(time: Long? = 250, type: Int? = 0, listener: (() -> Unit)? = null) {
    val view: View? = this
    if (false == view?.isShown) return
//    YzLog.e("View.toHint----------translationX= ${view?.translationX}")
    view?.visibility = View.VISIBLE
    view?.alpha = 1f
    view?.translationX = 0f
    view?.animate()
            ?.addListener {
                onAnimationEnd {
                    listener?.invoke()
                    view?.visibility = View.GONE
                    view?.alpha = 0f
                    view?.translationX = if (type == 1) 800f else -800f
                }
            }
            ?.setDuration(time ?: 250)
            ?.translationX(if (type == 1) 800f else -800f)
            ?.alpha(0f)
            ?.start()
}

//show type 0: toLeft 1:toRight
fun View?.toLeftShow(time: Long? = 250) {
    toShow(time)
}

//show type 0: toLeft 1:toRight
fun View?.toRightShow(time: Long? = 250) {
    toShow(time, 1)
}

//hint type 0: toLeft 1:toRight
fun View?.toLeftHint(time: Long? = 250) {
    toHint(time)
}

fun View?.toRightHint(time: Long? = 250) {
    toHint(time, 1)
}

fun throttleTouch(wait: Long = 300, block: ((View) -> Unit)): View.OnTouchListener {

    return View.OnTouchListener { v, event ->
        val current = System.currentTimeMillis()
        val lastClickTime = (v.getTag(R.id.ds_touch_debounce_action) as? Long) ?: 0
        val d = current - lastClickTime
        if (d > wait) {
            v.setTag(R.id.ds_touch_debounce_action, current)
            block(v)
        }
        true
    }
}