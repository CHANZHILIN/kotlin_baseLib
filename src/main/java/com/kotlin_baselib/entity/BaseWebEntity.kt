package com.kotlin_baselib.entity

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.base
 * Introduce:用于联网返回时  Observable的实体类
 */
class BaseWebEntity<T> {
    /**
     * code : 200
     * message : ok
     * data : data里面的数据实体类
     */

    var code: Int = 0
    var message: String? = null
    var data: T? = null
}
