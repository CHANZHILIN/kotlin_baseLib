package com.kotlin_baselib.application

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin_baselib.BuildConfig
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.utils.MyLog
import com.kotlin_baselib.utils.SdCardUtil
import kotlin.concurrent.thread

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

        MyLog.setDebug(BuildConfig.DEBUG)

        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)//初始化
        // 生命周期
        registerLifeCycleCallback()

        Runnable {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            //Module类的APP初始化
            modulesApplicationInit()
            //初始化项目文件夹
            SdCardUtil.initFileDir(this);
        }


    }

    private fun registerLifeCycleCallback() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                MyLog.e("打开:${activity?.javaClass?.name}")
            }

        })
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
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
