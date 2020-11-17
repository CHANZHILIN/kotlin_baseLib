package com.kotlin_baselib.recyclerview

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 *  Created by CHEN on 2019/12/10
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
abstract class AbstractAdapter<T> constructor(protected val itemList: MutableList<T>) :
    RecyclerView.Adapter<AbstractAdapter.Holder>() {

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = createItemView(parent, viewType)
        val viewHolder = Holder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }
        return viewHolder
    }


    fun updateData(items: List<T>) {
        updateAdapterWithDiffResult(calculateDiff(items))
    }

    private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(newItems: List<T>) =
        DiffUtil.calculateDiff(DiffUtilCallback(itemList, newItems))

    fun replaceData(items: List<T>) {
        if (!itemList.isNullOrEmpty()) itemList.clear()
        itemList.addAll(items)
        this.notifyDataSetChanged()
    }

    fun addData(item: List<T>) {
        itemList.addAll(item)
        notifyItemInserted(itemList.size)
    }

    fun remove(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getData(): List<T> {
        return itemList
    }

    final override fun onViewRecycled(holder: Holder) {
        super.onViewRecycled(holder)
        onViewRecycled(holder.itemView)
    }

    protected open fun onViewRecycled(itemView: View) {
    }

    protected open fun onItemClick(itemView: View, position: Int) {
    }

    protected abstract fun createItemView(parent: ViewGroup, viewType: Int): View

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val views = SparseArray<View>()

        fun <V : View> getView(viewId: Int): V {
            var view = views[viewId]
            if (view == null) {
                view = itemView.findViewById(viewId)
                views.put(viewId, view)
            }
            return view as V
        }
    }
}