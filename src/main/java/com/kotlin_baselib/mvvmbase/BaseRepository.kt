package com.kotlin_baselib.mvvmbase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_mvvm_library.base
 *  Introduce:
 **/
open class BaseRepository {

    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO) { call.invoke() }.apply {
            //这儿可以对返回结果errorCode做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回errorCode 为 100，下面代码实现,再到baseActivity通过观察error来处理
            when (code) {
                100 -> throw TokenInvalidException()
                404 -> throw NetWorkInvalidException(msg)
                500 -> throw InternalServerErrorException()
            }
        }
    }

    class TokenInvalidException(msg: String = "token invalid") : Exception(msg)
    class NetWorkInvalidException(msg: String = "network invalid") : Exception(msg)
    class InternalServerErrorException(msg: String = "internal server error") : Exception(msg)
}