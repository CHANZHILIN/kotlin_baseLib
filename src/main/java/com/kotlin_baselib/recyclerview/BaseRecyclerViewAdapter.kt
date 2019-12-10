package com.kotlin_baselib.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *  Created by CHEN on 2019/6/17
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.recyclerview
 *  Introduce:
 **/
abstract class BaseRecyclerViewAdapter<T> constructor(data: MutableList<T>?, resouce: Int) :
    RecyclerView.Adapter<BaseRecyclerViewHolder<T>>() {

    private var mItemListener: OnItemListener? = null
    private var mData: MutableList<T>? = null
    private var mResouce: Int
    protected lateinit var mContext: Context

    init {
        mData = data
        mResouce = resouce
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseRecyclerViewHolder<T> {
        mContext = viewGroup.context
        val view = LayoutInflater.from(mContext).inflate(mResouce, viewGroup, false)
        return holder(view, viewType)
    }


    override fun onBindViewHolder(baseHolder: BaseRecyclerViewHolder<T>, position: Int) {
        baseHolder.setOnItemClickListener(mItemListener!!)
        baseHolder.onBindView(mData!!.get(position))
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    abstract fun holder(view: View, viewType: Int): BaseRecyclerViewHolder<T>

    /**
     * item点击事件
     */
    fun setOnItemClickListener(itemClickListener: OnItemListener) {
        this.mItemListener = itemClickListener
    }

    /**
     * 添加数据
     */
    fun addDataList(list: MutableList<T>, isClear: Boolean) {
        if (isClear) {
            mData!!.clear()
        }
        val position = mData!!.size

        mData!!.addAll(list)
        if (isClear) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeInserted(position, list.size)
        }
    }

}