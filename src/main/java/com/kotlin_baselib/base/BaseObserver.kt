package com.kotlin_baselib.base

import android.content.Context
import android.net.ParseException
import android.util.Log
import com.kotlin_baselib.R
import com.kotlin_baselib.application.BaseApplication
import com.kotlin_baselib.entity.BaseWebEntity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *  Created by CHEN on 2019/6/13
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BaseObserver<T> constructor(private var mPresenter: BasePresenter<BaseView, BaseModel>?) :
    Observer<BaseWebEntity<T>> {
    //对应HTTP的状态码
    private val SUCCESS = 200
    private val NOT_FOUND = 404
    private val INTERNAL_SERVER_ERROR = 500
    private val UNSATISFIABLE_REQUEST = 504
    private val SERVICE_TEMPORARILY_UNAVAILABLE = 503
    private val OTHER_ERROR = 506     //其他错误

    private  var context: Context
    init {
        context = BaseApplication.instance
    }

    override fun onSubscribe(d: Disposable) {
        mPresenter!!.addDisposable(d)
        onStart()
    }

    override fun onNext(t: BaseWebEntity<T>) {
        if (t.code == SUCCESS) {//返回的code 为200 代表成功，其余的code值统一定为失败
            onSuccess(t.data!!)
        } else {
            onError(t.code, t.message!!)
        }
    }


    override fun onComplete() {
        onEnd()
    }

    override fun onError(e: Throwable) {
        var msg = ""
        if (e is HttpException) {
            val code = e.code()
            when (code) {
                NOT_FOUND ->
                    // 404
                    msg = context.getString(R.string.network_error)
                INTERNAL_SERVER_ERROR ->
                    // 500
                    msg = context.getString(R.string.network_error)
                UNSATISFIABLE_REQUEST ->
                    // 504
                    msg = context.getString(R.string.network_error)
                SERVICE_TEMPORARILY_UNAVAILABLE ->
                    // 503
                    msg = context.getString(R.string.network_error)
                else -> {
                }
            }
            onError(code, msg)
        } else if (e is UnknownHostException) {
            //没有网络
            msg = context.getString(R.string.network_error)
            onError(OTHER_ERROR, msg)
        } else if (e is SocketTimeoutException) {
            // 连接超时
            msg = context.getString(R.string.network_error)
            onError(OTHER_ERROR, msg)
        } else if (e is ConnectException) {
            msg = context.getString(R.string.network_error)
            onError(OTHER_ERROR, msg)
        } else if (e is ParseException) {
            msg = context.getString(R.string.network_error)
            onError(OTHER_ERROR, msg)
        } else {
            msg = context.getString(R.string.network_error)
            onError(OTHER_ERROR, msg)
        }
        e.printStackTrace()
        Log.d("BaseObserver", "onError: " + e.cause + "  " + e.message + "  " + e.toString())
    }

    /**
     * 开始,需要时重写
     */
    protected fun onStart() {}

    /**
     * 结束,需要时重写
     */
    protected fun onEnd() {}

    protected abstract fun onSuccess(data: T)

    protected abstract fun onError(code: Int, msg: String)
}