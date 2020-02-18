package com.sample.custom.view.ui.rv.layoutmanager

import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 的缓存机制
 *
 * https://blog.csdn.net/harvic880925/article/details/84866486
 *
 * 仅通过此种方式改变item，平移的位置，不能改变item的透明度以及旋转度
 *
 */
class CustomLinearManagerV2 : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var mTotalHeight: Int = 0

    private var mItemWidth: Int = 0
    private var mItemHeight: Int = 0

    private val mSpareItemRects: SparseArray<Rect> = SparseArray()

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        mSpareItemRects.clear()

        if(itemCount <= 0){
            detachAndScrapAttachedViews(recycler)
            return
        }

        /**
         * 将RecyclerView中的item的 viewHolder 从 RecyclerView 中剥离
         */
        detachAndScrapAttachedViews(recycler)

        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view, 0, 0)
        mItemWidth = getDecoratedMeasuredWidth(view)
        mItemHeight = getDecoratedMeasuredHeight(view)

        /**
         * 缓存所有的item
         */
        var offsetY = 0
        for(pos in 0 until itemCount){
            val rect = Rect(0, offsetY, mItemWidth, offsetY + mItemHeight)
            mSpareItemRects.put(pos, rect)
            offsetY += mItemHeight
        }

        /**
         * 由于item的高度一致，因此可见的个数为 getVerticallySpace() / mItemHeight
         */
        var visibleCount = Math.ceil(getVerticallySpace() / mItemHeight.toDouble()).toInt()
        visibleCount = if(visibleCount > itemCount) itemCount else visibleCount

        for(index in 0 until visibleCount){
            val rect = mSpareItemRects[index]
            val childView = recycler.getViewForPosition(index)

            addView(childView)

            //addView以后，一定要先measure,然后再layout.
            measureChildWithMargins(childView, 0,0)
            layoutDecorated(childView, rect.left, rect.top, rect.right, rect.bottom)
        }

        mTotalHeight = Math.max(offsetY, getVerticallySpace())
    }

    private fun getVerticallySpace(): Int {
        return height - paddingTop - paddingBottom
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    private var mSumDy = 0

    /**
     * 处理滚动:
     *     回收滚出屏幕外的holderView, 然后再填充滚动后的空白区域.
     *
     * 处理策略:
     *      先假设滚动了dy, 然后再看回收哪些Item，需要新增哪些item. 之后再利用 offsetChildrenVertical(-dy)来实现滚动
     */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {

        var transY = dy

        //边界判断.
        if(mSumDy + transY < 0){
            transY = -mSumDy
        }else if(mSumDy + transY > mTotalHeight - getVerticallySpace()){
            transY = mTotalHeight - getVerticallySpace() - mSumDy
        }

        for(index in (childCount - 1) downTo 0 step 1){
            val childView: View = getChildAt(index)!!
            if(transY > 0){
                /**
                 * 向上滚动时，当前View的下边界移除了RecyclerView的上边界
                 */
                if(getDecoratedBottom(childView) - transY < 0){
                    removeAndRecycleView(childView, recycler)
                }
            }else if(transY < 0){
                /**
                 * 向下滚动时，当前View的上边界移除了RecyclerView的下边界
                 */
                if(getDecoratedTop(childView) - transY > height - paddingBottom){
                    removeAndRecycleView(childView, recycler)
                }
            }
        }

        val visibleRect = getVisibleRect(transY)

        if(transY >= 0){
            val lastView = getChildAt(childCount - 1)!!
            val lastPosition = getPosition(lastView) + 1
            for(pos in lastPosition until itemCount){
                val rect = mSpareItemRects.get(pos)
                if(Rect.intersects(visibleRect, rect)){
                    val childView = recycler.getViewForPosition(pos)

                    /**
                     * 添加到后面.
                     */
                    addView(childView)

                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)
                }else{
                    break
                }
            }

        }else{
            val firstView = getChildAt(0)!!
            val position = getPosition(firstView)

            val firstPosition = if(position > 0) position - 1 else 0

            for(pos in firstPosition downTo 0 step 1){
                val rect = mSpareItemRects.get(pos)
                if(Rect.intersects(visibleRect, rect)){
                    val childView = recycler.getViewForPosition(pos)

                    /**
                     * 添加到前面.
                     */
                    addView(childView, 0)

                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)
                }else{
                    break
                }
            }
        }

        mSumDy += transY

        offsetChildrenVertical(-transY)

        return transY
    }

    private fun getVisibleRect(transY: Int): Rect{
        return Rect(paddingLeft, paddingTop + mSumDy + transY,
            width - paddingRight, getVerticallySpace() + mSumDy + transY)
    }
}