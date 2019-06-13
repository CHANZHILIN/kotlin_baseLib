package com.kotlin_baselib.base

/**
 *  Created by CHEN on 2019/6/13
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:  空的presenter
 **/
class EmptyPresenterImpl(mvpView: EmptyView) : BasePresenter<EmptyView, EmptyModelImpl>(mvpView) {


    override fun createModel(): EmptyModelImpl {
        return EmptyModelImpl()
    }

}