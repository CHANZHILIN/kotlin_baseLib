package com.kotlin_baselib.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *  Created by CHEN on 2019/12/10
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
class SingleAdapter<T>(
    items: MutableList<T>,
    private val layoutResId: Int,
    private val bindHolder: (position: Int, Holder, T) -> Unit
) : AbstractAdapter<T>(items) {

    private var itemClick: ((position: Int, T) -> Unit)? = null

    constructor(
        items: MutableList<T>,
        layoutResId: Int,
        bindHolder: (position: Int, Holder, T) -> Unit,
        itemClick: ((position: Int, T) -> Unit)? = null
    ) : this(items, layoutResId, bindHolder) {
        this.itemClick = itemClick
    }

    override fun createItemView(parent: ViewGroup, viewType: Int): View {
        /* if (view.tag?.toString()?.contains("layout/") == true) {
             DataBindingUtil.bind<ViewDataBinding>(view)
         }*/
        return parent inflate layoutResId
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        bindHolder(position, holder, itemList[position])
    }

    override fun onItemClick(itemView: View, position: Int) {
        itemClick?.invoke(position, itemList[position])
    }
}


class MultiAdapter<T : IListItem>(
    private val items: MutableList<T>,
    private val bindHolder: (position: Int, Holder, T) -> Unit
) : AbstractAdapter<T>(items) {

    private var itemClick: ((position: Int, T) -> Unit)? = null
    private lateinit var listItems: Array<out ListItem<T>>

    constructor(
        items: MutableList<T>,
        listItems: Array<out ListItem<T>>,
        bindHolder: (position: Int, Holder, T) -> Unit,
        itemClick: ((position: Int, T) -> Unit)? = null
    ) : this(items, bindHolder) {
        this.itemClick = itemClick
        this.listItems = listItems
    }

    override fun createItemView(parent: ViewGroup, viewType: Int): View {
//        parent.inflate(viewType)
        return parent inflate getLayoutId(viewType)
    }

    private fun getLayoutId(viewType: Int): Int {
        var layoutId = -1
        listItems.forEach {
            if (it.layoutResId == viewType) {
                layoutId = it.layoutResId
                return@forEach
            }
        }
        return layoutId
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getType()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        bindHolder(position, holder, itemList[position])
    }

    override fun onItemClick(itemView: View, position: Int) {
        itemClick?.invoke(position, itemList[position])
    }
}

/**
 * 拓展函数
 * 单个布局
 */
fun <T> RecyclerView.setSingleItemUp(
    items: MutableList<T>,  //数据
    layoutResId: Int,       //item布局id
    bindHolder: (position: Int, AbstractAdapter.Holder, T) -> Unit,//绑定布局
    manager: RecyclerView.LayoutManager? = null,
    itemClick: ((position: Int, T) -> Unit)? = null //点击事件

): SingleAdapter<T> {
    val singleAdapter by lazy {
        SingleAdapter(items, layoutResId, { position, holder, item ->
            bindHolder(position, holder, item)
        }, { position, it ->
            itemClick?.invoke(position, it)
        })
    }
    layoutManager = manager ?: LinearLayoutManager(this.context)
    adapter = singleAdapter
    return singleAdapter
}

/**
 * 多个布局
 */
fun <T : IListItem> RecyclerView.setMultiItemUp(
    items: MutableList<T>,
    manager: RecyclerView.LayoutManager? = null,
    vararg listItems: ListItem<T>
): MultiAdapter<T> {

    val multiAdapter by lazy {
        MultiAdapter(items, listItems,
            { position, holder, item ->
                val listItem: ListItem<T>? = getListItem(listItems, item)
                listItem?.bindHolder?.invoke(position, holder, item)
            },
            { position, item ->
                val listItem: ListItem<T>? = getListItem(listItems, item)
                listItem?.itemClick?.invoke(position, item)
            })
    }
    layoutManager = manager ?: LinearLayoutManager(this.context)
    adapter = multiAdapter
    return multiAdapter
}

private fun <T : IListItem> getListItem(listItems: Array<out ListItem<T>>, item: T): ListItem<T>? {
    var listItem: ListItem<T>? = null
    listItems.forEach {
        if (it.layoutResId == item.getType()) {
            listItem = it
            return@forEach
        }
    }
    return listItem
}

class ListItem<T>(
    val layoutResId: Int,
    val bindHolder: (position: Int, holder: AbstractAdapter.Holder, item: T) -> Unit,
    val itemClick: ((position: Int, T) -> Unit)? = null
)


interface IListItem {
    fun getType(): Int = 0
}

/**
 * 多种布局数据需要经过这个类处理
 */
class ListItemAdapter<T>(var data: T, private val viewType: Int) : IListItem {

    override fun getType(): Int {
        return viewType
    }
}

/**
 * 中缀调用函数,添加布局
 */
infix fun ViewGroup.inflate(layoutResId: Int): View =
    LayoutInflater.from(this.context).inflate(layoutResId, this, false)
