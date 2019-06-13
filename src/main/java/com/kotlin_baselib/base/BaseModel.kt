package com.kotlin_baselib.base

import com.kotlin_baselib.api.Api
import com.kotlin_baselib.api.ApiEngine

/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BaseModel {
    protected var mApiService: Api

    init {
        mApiService = ApiEngine.instance!!.apiService
    }
}