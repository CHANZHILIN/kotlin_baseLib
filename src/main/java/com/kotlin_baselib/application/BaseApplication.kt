package com.kotlin_baselib.application

import android.support.multidex.BuildConfig
import android.support.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.application
 * Introduce:   主Application
 */
class BaseApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)//初始化

        //Module类的APP初始化
        modulesApplicationInit()

    }

    private fun modulesApplicationInit() {
        for (moduleImpl in ModuleConfig.MODULESLIST) {
            try {
                val clazz = Class.forName(moduleImpl)
                val obj = clazz.newInstance()
                if (obj is IComponentApplication) {
                    obj.onCreate(instance)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        @get:Synchronized
        lateinit var instance: BaseApplication
            private set
    }
}
