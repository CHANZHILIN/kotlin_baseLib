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
    items: List<T>,
    private val layoutResId: Int,
    private val bindHolder: (Holder, T) -> Unit
) : AbstractAdapter<T>(items) {

    private var itemClick: (T) -> Unit = {}

    constructor(
        items: List<T>,
        layoutResId: Int,
        bindHolder: (Holder, T) -> Unit,
        itemClick: (T) -> Unit = {}
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
        bindHolder(holder, itemList[position])
    }

    override fun onItemClick(itemView: View, position: Int) {
        itemClick(itemList[position])
    }
}


class MultiAdapter<T : IListItem>(
    private val items: List<T>,
    private val bindHolder: (Holder, T) -> Unit
) : AbstractAdapter<T>(items) {

    private var itemClick: (T) -> Unit = {}
    private lateinit var listItems: Array<out ListItem<T>>

    constructor(
        items: List<T>,
        listItems: Array<out ListItem<T>>,
        bindHolder: (Holder, T) -> Unit,
        itemClick: (T) -> Unit = {}
    ) : this(items, bindHolder) {
        this.itemClick = itemClick
        this.listItems = listItems
    }

    override fun createItemView(parent: ViewGroup, viewType: Int): View {
        parent.inflate(viewType)
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
        bindHolder(holder, itemList[position])
    }

    override fun onItemClick(itemView: View, position: Int) {
        itemClick(itemList[position])
    }
}

/**
 * 拓展函数
 * 单个布局
 */
fun <T> RecyclerView.setSingleUp(
    items: List<T>,
    layoutResId: Int,
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    bindHolder: (AbstractAdapter.Holder, T) -> Unit,
    itemClick: (T) -> Unit = {}

): AbstractAdapter<T> {
    val singleAdapter by lazy {
        SingleAdapter(items, layoutResId, { holder, item ->
            bindHolder(holder, item)
        }, {
            itemClick(it)
        })
    }
    layoutManager = manager
    adapter = singleAdapter
    return singleAdapter
}

/**
 * 多个布局
 */
fun <T : IListItem> RecyclerView.setMutiUp(
    items: List<T>,
    manager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    vararg listItems: ListItem<T>
): AbstractAdapter<T> {

    val multiAdapter by lazy {
        MultiAdapter(items, listItems, { holder, item ->
            val listItem: ListItem<T>? = getListItem(listItems, item)
            listItem?.bindHolder?.invoke(holder, item)
        }, { item ->
            val listItem: ListItem<T>? = getListItem(listItems, item)
            listItem?.itemClick?.invoke(item)
        })
    }
    layoutManager = manager
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
    val bindHolder: (holder: AbstractAdapter.Holder, item: T) -> Unit,
    val itemClick: (item: T) -> Unit = {}
)


interface IListItem {
    fun getType(): Int
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
 * 中缀调用函数
 */
infix fun ViewGroup.inflate(layoutResId: Int): View =
    LayoutInflater.from(context).inflate(layoutResId, this, false)