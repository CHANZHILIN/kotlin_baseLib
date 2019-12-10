package com.kotlin_baselib.api

import com.kotlin_baselib.entity.EmptyEntity
import com.kotlin_baselib.base.ResponseData
import retrofit2.http.POST

/**
 *  Created by CHEN on 2019/6/13
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.api
 *  Introduce:
 **/
interface Api {
    companion object {
        const val BASE_URL = "http://admin.lgw.com/api/"    //线下
        const val BASE_IMAGE_URL = "http://admin.lgw.com/"    //线下图片
    }

    @POST(Url.versionUpdate)
    suspend fun getVersionData(): ResponseData<EmptyEntity>

    /*    */
    /**
     * 版本更新
     *
     * @return
     *//*
    @POST(Url.versionsUpdate)
    Observable<BaseWebEntity<AppVersion>> versionUpdated();

    */
    /**
     * 发送验证码
     *//*
    @FormUrlEncoded
    @POST(Url.sendAuth)
    Observable<BaseWebEntity<String>> sendCode(@Field("auth_user") String autu_user, @Field("auth_type") int auth_type, @Field("phone") String phone);

    */
    /**
     * 注册
     *//*
    @FormUrlEncoded
    @POST(Url.register)
    Observable<BaseWebEntity<EmptyEntity>> doRegister(@FieldMap Map<String, String> data);*/

}