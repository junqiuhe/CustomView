package com.sample.custom.view.widget.rv.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sample.custom.view.R

/**
 * ItemDecoration: item装饰器
 *
 * https://blog.csdn.net/harvic880925/article/details/82959754
 *
 * 备注: ItemDecoration 与 Item 的绘制顺序: ItemDecoration.onDraw -> Item 的 draw -> ItemDecoration.onDrawOver
 */
class CustomLinearItemDecoration(
    private val mContext: Context
) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 4f
    }

    /**
     * getItemOffsets的主要作用就是给item的四周撑开一定的距离，类似于给item添加margin值。
     * 撑开一定的距离后，可以利用onDraw在这个距离绘图.
     *
     * outRect[left, top, right, bottom] 分别距离左，上，右，下边的距离
     * view 当前Item的view对象
     * parent 指的是RecyclerView
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = 200
        outRect.bottom = 2
    }

    /**
     * 可以利用onDraw在getItemOffsets撑开的距离绘图.
     * onDraw绘图时，超过撑开的距离的部分，将会被覆盖
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        mPaint.color = Color.GREEN

        val manager: RecyclerView.LayoutManager? = parent.layoutManager
        manager?.let { it: RecyclerView.LayoutManager ->
            for(index in 0 until parent.childCount){
                val childView = parent.getChildAt(index)

                //获取左边的宽度
                val left = it.getLeftDecorationWidth(childView)

                val cx = left / 2f
                val cy = childView.top + childView.height / 2f

                val itemCount = it.itemCount
                val itemPosition = parent.getChildAdapterPosition(childView)
                if(itemPosition == 0){
                    c.drawLine(cx, cy, cx, cy + childView.height / 2f, mPaint)
                }else if(itemPosition == itemCount - 1){
                    c.drawLine(cx, childView.top.toFloat(), cx, cy, mPaint)
                }else{
                    c.drawLine(cx, childView.top.toFloat(), cx, childView.top.toFloat() + childView.height, mPaint)
                }
                c.drawCircle(cx, cy, 30f, mPaint)
            }
        }
    }

    /**
     * 可以利用onDrawOver在getItemOffsets撑开的距离绘图.
     * 而 onDrawOver 不会被覆盖(是绘制在Item之上)，可以利用它给 RecyclerView 添加蒙版等操作
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        mPaint.textSize = mContext.resources.getDimensionPixelSize(R.dimen.sp_24).toFloat()
        mPaint.color = Color.BLUE

        val parentWidth = parent.width
        val parentHeight = parent.height

        val rect = Rect()
        mPaint.getTextBounds(TEXT, 0, TEXT.length, rect)

        c.drawText(TEXT, (parentWidth - rect.width()) / 2f, (parentHeight - rect.height()) / 2f, mPaint)
    }

    companion object{
        private val TEXT = "onDrawOver的用法"
    }
}