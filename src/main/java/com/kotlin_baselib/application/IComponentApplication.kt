package com.kotlin_baselib.application

import android.app.Application

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.application
 * Introduce:  所有模块Application继承这个interface
 */
interface IComponentApplication {

    val application: Application

    fun onCreate(application: BaseApplication)
}
