package com.kotlin_baselib.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager
import androidx.core.content.ContextCompat

class ScreenUtils private constructor(context: Activity?) {

    init {
        try {
            if (context != null) {
                dm = DisplayMetrics()
                val wm =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getMetrics(dm)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getAndroiodScreenProperty(context: Context) {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val height = dm.heightPixels // 屏幕高度（像素）
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        val screenWidth = (width / density).toInt() // 屏幕宽度(dp)
        val screenHeight = (height / density).toInt() // 屏幕高度(dp)
        MyLog.d("h_bl", "屏幕宽度（像素）：$width")
        MyLog.d("h_bl", "屏幕高度（像素）：$height")
        MyLog.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：$density")
        MyLog.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：$densityDpi")
        MyLog.d("h_bl", "屏幕宽度（dp）：$screenWidth")
        MyLog.d("h_bl", "屏幕高度（dp）：$screenHeight")
    }

    companion object {
        private var dm: DisplayMetrics? = null
        private var navigationBarHeight = -1
        private var dpiUtil: ScreenUtils? = null

        /**
         * ScreenUtils 第一次启动需要调用的方法
         */
        fun init(context: Activity?) {
            if (dpiUtil == null) {
                dpiUtil =
                    ScreenUtils(context)
            }
        }

        fun getNavigationBarHeight(context: Context?): Int {
            if (context == null) return 0
            if (navigationBarHeight != -1) return navigationBarHeight
            return if (!isNavigationBarShowing(context)) {
                navigationBarHeight = 0
                navigationBarHeight
            } else {
                val resources = context.resources
                val resourceId =
                    resources.getIdentifier("navigation_bar_height", "dimen", "android")
                val height = resources.getDimensionPixelSize(resourceId)
                navigationBarHeight = height
                height
            }
        }

        fun hasNavigationBar(var0: Context): Boolean {
            var var1 = false
            var var2: Int
            var var4: Resources
            if (var0.resources.also { var4 = it }.getIdentifier(
                    "config_showNavigationBar",
                    "bool",
                    "android"
                ).also { var2 = it } > 0
            ) {
                var1 = var4.getBoolean(var2)
            }
            try {
                var var5: Class<*>?
                val var6 = Class.forName("android.os.SystemProperties")
                    .also { var5 = it }.getMethod(
                        "get", *arrayOf<Class<*>>(
                            String::class.java
                        )
                    ).invoke(var5, *arrayOf<Any>("qemu.hw.mainkeys")) as String
                if ("1" == var6) {
                    var1 = false
                } else if ("0" == var6) {
                    var1 = true
                }
            } catch (var3: Exception) {
                var3.printStackTrace()
            }
            return var1
        }

        fun isNavigationBarShowing(var0: Context): Boolean {
            return if (!hasNavigationBar(var0)) {
                false
            } else {
                val var1: String
                if (Build.MANUFACTURER.toUpperCase().contains("XIAOMI")) {
                    var1 = "force_fsg_nav_bar"
                    return Settings.Global.getInt(
                        var0.contentResolver,
                        var1,
                        0
                    ) == 0
                }
                if (Build.MANUFACTURER.toUpperCase().contains("VIVO")) {
                    var1 = "navigation_gesture_on"
                    return Settings.Secure.getInt(
                        var0.contentResolver,
                        var1,
                        0
                    ) != 0
                }
                true
            }
        }

        /**
         * dpi转换成像素
         */
        fun dpi2Px(dpi: Float): Float {
            return if (dm == null) 0f else dpi * dm!!.density + 0.5f
        }

        /**
         * dp 转 px
         *
         * @param dpValue dp 值
         * @return px 值
         */
        fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * 像素转换成dpi
         */
        fun px2Dpi(context: Context, px: Int): Int {
            if (dm == null) dm =
                context.resources.displayMetrics
            return (px / dm!!.density + 0.5f).toInt()
        }

        /**
         * pt转换成px
         */
        fun pt2Px(context: Context, pt: Int): Int {
            if (dm == null) dm =
                context.resources.displayMetrics
            return TypedValue.complexToDimensionPixelSize(
                pt,
                dm
            )
        }

        /**
         * pt转换成dpi
         */
        fun pt2Dpi(context: Context, pt: Int): Int {
            if (dm == null) dm =
                context.resources.displayMetrics
            return px2Dpi(
                context,
                TypedValue.complexToDimensionPixelSize(
                    pt,
                    dm
                )
            )
        }

        /**
         * pt转换成sp
         */
        fun pt2Sp(context: Context, pt: Int): Int {
            if (dm == null) dm =
                context.resources.displayMetrics
            return px2Sp(
                context,
                TypedValue.complexToDimensionPixelSize(
                    pt,
                    dm
                ).toFloat()
            )
        }

        /**
         * 像素转换成sp
         */
        fun px2Sp(context: Context, px: Float): Int {
            if (dm == null) dm =
                context.resources.displayMetrics
            return (px / dm!!.scaledDensity + 0.5f).toInt()
        }

        /**
         * 获取当前屏幕宽度，单位是PX
         */
        val screenWidth: Int
            get() = dm?.widthPixels ?: 0

        /**
         * 获取当前屏幕高度，单位是PX
         */
        fun getScreenHeight(): Int {
            return dm?.heightPixels ?: 0
        }

        private var screenHeight = -1

        /**
         * 获取当前屏幕高度，单位是PX
         */
        fun getScreenHeight(context: Context): Int {
            if (screenHeight != -1) {
                return screenHeight
            }
            var heightPixels = 0

            // includes window decorations (statusbar bar/navigation bar)
            try {
                val wm =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getMetrics(dm)
                if (dm == null) return 0
                val d = wm.defaultDisplay
                val metrics = DisplayMetrics()
                d.getMetrics(metrics)
                // since SDK_INT = 1;
                heightPixels = metrics.heightPixels
                screenHeight = heightPixels
                val realSize = Point()
                Display::class.java.getMethod(
                    "getRealSize",
                    Point::class.java
                ).invoke(d, realSize)
                heightPixels = realSize.y
                screenHeight = heightPixels
            } catch (ignored: Exception) {
            }
            return heightPixels
        }

        /**
         * 获取屏幕宽度(dp)
         */
        fun getScreenWidthDp(context: Context?): Float {
            val displayMetrics =
                getDisplayMetrics(context!!)
            return displayMetrics.widthPixels / displayMetrics.density
        }

        /**
         * 获取屏幕高度(dp)
         */
        fun getScreenHeightDp(context: Context?): Float {
            val displayMetrics =
                getDisplayMetrics(context!!)
            return displayMetrics.heightPixels / displayMetrics.density
        }

        fun getDisplayMetrics(context: Context): DisplayMetrics {
            return context.resources.displayMetrics
        }

        /**
         * 修改NavigationBar背景颜色 可自定义颜色
         */

        fun setNavigationBarColor(activity: Activity, color: Int) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.navigationBarColor = ContextCompat.getColor(activity, color)
        }
    }

}