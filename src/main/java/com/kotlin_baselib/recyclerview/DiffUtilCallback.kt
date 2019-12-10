package com.kotlin_baselib.recyclerview

import androidx.recyclerview.widget.DiffUtil

/**
 *  Created by CHEN on 2019/12/10
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
internal class DiffUtilCallback<T>(private val oldItems: List<T>,
                                      private val newItems: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldItems[oldItemPosition] == newItems[newItemPosition]
}