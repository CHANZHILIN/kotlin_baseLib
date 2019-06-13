package com.kotlin_baselib.entity

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.entity
 * Introduce:实体基类-用于Adapter填充数据
 */
class BaseEntity<T> {

    var type: Int = 0
    var isCheck: Boolean = false
    var isEdit: Boolean = false
    var data: T? = null

    constructor() {}

    constructor(data: T) {
        this.data = data
    }

    constructor(type: Int, isCheck: Boolean, isEdit: Boolean, data: T) {
        this.type = type
        this.isCheck = isCheck
        this.isEdit = isEdit
        this.data = data
    }

    override fun toString(): String {
        return "BaseEntity{" +
                "type=" + type +
                ", isCheck=" + isCheck +
                ", isEdit=" + isEdit +
                ", data=" + data +
                '}'.toString()
    }
}
