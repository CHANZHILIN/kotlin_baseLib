package com.soul_picture.decoration

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by CHEN on 2019/12/11
 * Email:1181785848@qq.com
 * Introduce:decoration
 */
class LinearLayoutDividerItemDecoration(
    private val context: Context,
    private val interval: Int
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //item的位置
        val position = parent.getChildAdapterPosition(view);
//        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
//         获取item在span中的下标
//        val spanIndex = params.spanIndex
        val interval = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.interval.toFloat(), context.resources.displayMetrics
        ).toInt()


        /*  if (spanIndex == PictureFragment.SPAN_COUNT - 1) {
              outRect.left = interval
              outRect.right = interval
          } else {
              outRect.left = interval
              outRect.right = 0
          }*/

        outRect.left = interval
        outRect.right = interval
        // 下方间隔
        outRect.bottom = interval
        //顶排，上顶设置高度
        if (position == 0) {
            outRect.top = interval
        }
    }
}
