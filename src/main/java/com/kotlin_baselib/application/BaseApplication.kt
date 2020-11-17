package com.kotlin_baselib.application

import androidx.multidex.MultiDexApplication
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin_baselib.api.Constants
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

        if (Constants.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)//初始化


        Runnable {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)

            //Module类的APP初始化
            modulesApplicationInit()
            //初始化项目文件夹
            SdCardUtil.initFileDir(this);
        }


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
