package com.sample.custom.view.widget.rv.layoutmanager

import android.graphics.Rect
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 不使用offsetChildrenVertical()方法, 在滚动的时候，通过自己计算来进行布局
 *
 * 方法: 先离屏，再将从View重新布局到RecyclerView
 *
 * https://blog.csdn.net/harvic880925/article/details/84979161
 *
 */
class CustomLinearManagerV3 : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var mTotalHeight = 0
    private val mSpareItemRect = SparseArray<Rect>()

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        mSpareItemRect.clear()
        if(itemCount <= 0){
            detachAndScrapAttachedViews(recycler)
            return
        }

        detachAndScrapAttachedViews(recycler)

        val childView = recycler.getViewForPosition(0)
        measureChildWithMargins(childView, 0, 0)
        val itemWidth = getDecoratedMeasuredWidth(childView)
        val itemHeight = getDecoratedMeasuredHeight(childView)

        var offsetY = 0

        for(index in 0 until itemCount){
            val rect = Rect(paddingLeft, paddingTop + offsetY, paddingLeft + itemWidth, paddingTop + offsetY + itemHeight)
            mSpareItemRect.put(index, rect)
            offsetY += itemHeight
        }

        val visibleCount = Math.ceil(getRvHeight() / itemHeight.toDouble()).toInt()
        for(index in 0 until visibleCount){
            val rect = mSpareItemRect.get(index)

            val childView = recycler.getViewForPosition(index)
            addView(childView)

            measureChildWithMargins(childView, 0, 0)
            layoutDecorated(childView, rect.left, rect.top, rect.right, rect.bottom)
        }

        mTotalHeight = Math.max(getRvHeight(), offsetY)
    }

    private fun getRvHeight(): Int{
        return height - paddingTop - paddingBottom
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    private var mSumDy: Int = 0

    /**
     * dy: 向上滚动 dy > 0
     *     向下滚动 dy < 0
     */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if(itemCount <= 0){
            return dy
        }

        var transY = dy

        if(mSumDy + transY < 0){  //
            transY = -mSumDy

        }else if(mSumDy + transY > mTotalHeight - getRvHeight()){  //mTotalHeight - getRvHeight() 为能够滚动的最大的 offset
            transY = mTotalHeight - getRvHeight() - mSumDy
        }

        /**
         * 检测并回收移除屏幕的View.
         */
        for(index in (childCount - 1) downTo 0 step 1){
            if(transY > 0){  //向上滚动, 判断当前View的下边界是否移除了RecyclerView的上边界
                val childView = getChildAt(index)!!
                if(getDecoratedBottom(childView) - transY < 0){
                    removeAndRecycleView(childView, recycler)
                }

            }else if(transY < 0){ //向下滚动, 判断当前View的上边界是否移除了RecyclerView的下边界
                val childView = getChildAt(index)!!
                if(getDecoratedTop(childView) - transY > height - paddingBottom){
                    removeAndRecycleView(childView, recycler)
                }
            }
        }

        mSumDy += transY
        val visibleRect = getVisibleRect()

        /**
         * 必须在离屏操作前，不然获取为null.
         */
        val lastView = getChildAt(childCount - 1)!!
        val firstView = getChildAt(0)!!

        detachAndScrapAttachedViews(recycler)

        /**
         *  不采用offsetChildrenVertical，自己布局
         */
        if(transY >= 0) { //向上滚动, 底部留出空白，需要从缓存中获取item进行填充.
            val firstPosition = getPosition(firstView)
            for(pos in firstPosition until itemCount){
                val rect = mSpareItemRect.get(pos)
                if(Rect.intersects(visibleRect, rect)){
                    val childView = recycler.getViewForPosition(pos)
                    addView(childView)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)

                    childView.rotationY = childView.rotationY + 1
                }
            }

        } else {  //向下滚动，顶部留出空白，需要从缓存中获取item进行填充.

            val lastPosition = getPosition(lastView)
            for(pos in lastPosition downTo 0 step 1){
                val rect = mSpareItemRect.get(pos)
                if(Rect.intersects(visibleRect, rect)){
                    val childView = recycler.getViewForPosition(pos)
                    addView(childView, 0)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)

                    childView.rotationY = childView.rotationY - 1
                }
            }
        }

        //添加边界处理
        return transY
    }

    private fun getVisibleRect(): Rect{
        return Rect(paddingLeft, paddingTop + mSumDy,
            width - paddingRight, getRvHeight() + mSumDy)
    }
}