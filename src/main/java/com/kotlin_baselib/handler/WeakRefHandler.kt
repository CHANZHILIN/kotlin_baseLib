package com.kotlin_baselib.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference

/**
 *  Created by CHEN on 2019/12/20
 *  Email:1181785848@qq.com
 *  Introduce: 实现回调弱引用的Handler
 *  防止由于内部持有导致的内存泄露
 *  注意：传入的Callback不能使用匿名实现的变量，必须与使用这个Handle的对象的生命周期一
 *  致否则会被立即释放掉了
 *  使用：
 *             val mCallback = object : Handler.Callback() {
 *               fun handleMessage(msg: Message): Boolean {
 *                  when (msg.what) {
 *                          //do something
 *                   }
 *                   return true
 *                   }
 *                 }
 *            val mHandler = WeakRefHandler(mCallback)
 **/
class WeakRefHandler : Handler {
    private var mWeakReference: WeakReference<Callback>? = null

    constructor(callback: Callback) {
        mWeakReference = WeakReference<Handler.Callback>(callback)
    }

    constructor(callback: Callback, looper: Looper) : super(looper) {
        mWeakReference = WeakReference<Handler.Callback>(callback)
    }

    override fun handleMessage(msg: Message) {
        if (mWeakReference != null && mWeakReference!!.get() != null) {
            val callback = mWeakReference!!.get()
            callback?.handleMessage(msg)
        }
    }
}