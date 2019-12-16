package com.kotlin_baselib.utils

import android.app.Activity
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.kotlin_baselib.application.BaseApplication


/**
 *  Created by CHEN on 2019/12/13
 *  Email:1181785848@qq.com
 *  Introduce:  屏幕工具类
 **/
class ScreenUtils {

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = ScreenUtils()
    }

    /**
     * 获取顶部status高度
     */
    fun getStatusBarHeight(): Int {
        val resources = BaseApplication.instance.getResources()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 获取navigationbar的高度
     */
    fun getNavigationBarHeight(): Int {
        val resources = BaseApplication.instance.getResources()
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 检查是否有navigationbar虚拟导航栏
     */
    fun checkDeviceHasNavigationBar(): Boolean {
        var hasNavigationBar = false
        val rs = BaseApplication.instance.getResources()
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
            //do something
        }

        return hasNavigationBar
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        val dm = BaseApplication.instance.getResources().getDisplayMetrics()
        return dm.heightPixels
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        val dm = BaseApplication.instance.getResources().getDisplayMetrics()
        return dm.widthPixels
    }

    /**
     * 修改NavigationBar背景颜色 可自定义颜色
     */

    fun setNavigationBarColor(activity: Activity, color: Int) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.navigationBarColor = ContextCompat.getColor(activity, color)
    }

}