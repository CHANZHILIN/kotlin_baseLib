package com.kotlin_baselib.mvvmbase

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_mvvm_library.base
 *  Introduce:服务器返回的数据，code:返回码，msg:返回附加信息，data:返回内容
 **/
data class ResponseData<out T>(val code: Int, val msg: String, val data: T)