package com.kotlin_baselib.recyclerview

import android.view.View

/**
 *  Created by CHEN on 2019/6/17
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.recyclerview
 *  Introduce:
 **/
interface OnItemListener {
    fun onItem(view: View, position: Int)
}