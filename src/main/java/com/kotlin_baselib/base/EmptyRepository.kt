package com.kotlin_baselib.base

import com.kotlin_baselib.api.ApiEngine
import com.kotlin_baselib.entity.EmptyEntity

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.mvvmbase
 *  Introduce: 主Repository
 **/
class EmptyRepository : BaseRepository() {
    suspend fun getVersionData(): ResponseData<EmptyEntity> = request {
        ApiEngine.apiService.getVersionData()
    }
}