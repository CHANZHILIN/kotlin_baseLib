package com.kotlin_baselib.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 *  Created by CHEN on 2019/6/17
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.recyclerview
 *  Introduce:
 **/
abstract class BaseRecyclerViewHolder<T>(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private var itemListener: OnItemListener? = null
    private var mContext: Context? = null

    init {
        mContext = context
        itemView.setOnClickListener { v ->
            itemListener!!.onItem(v, layoutPosition)
        }
    }

    abstract fun onBindView(baseEntity: T)

    fun setOnItemClickListener(itemListener: OnItemListener) {
        this.itemListener = itemListener
    }
}