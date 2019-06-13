package com.kotlin_baselib.base


/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
interface BaseView {
    fun onSuccess(msg: String)
    fun onError(code: Int, msg: String)
}