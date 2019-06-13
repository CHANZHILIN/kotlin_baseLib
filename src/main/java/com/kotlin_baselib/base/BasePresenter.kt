package com.kotlin_baselib.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BasePresenter<V : BaseView, M : BaseModel> constructor(protected var mvpView: V?) {
    protected var mDisposable: CompositeDisposable? = null
    protected var mModel: M

    init {
        mModel = createModel()
    }

    protected abstract fun createModel(): M

    fun detachView() {
        this.mvpView = null
        clearDisposable()
    }

    //RXjava取消注册，以避免内存泄露
    fun clearDisposable() {
        if (mDisposable != null && mDisposable!!.size() != 0) {
            mDisposable!!.clear()
        }
    }

    fun addDisposable(disposable: Disposable) {
        if (mDisposable == null) {
            mDisposable = CompositeDisposable()
        }
        mDisposable!!.add(disposable)
    }
}