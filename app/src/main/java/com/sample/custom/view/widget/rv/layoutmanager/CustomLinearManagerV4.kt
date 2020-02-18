package com.sample.custom.view.widget.rv.layoutmanager

import android.graphics.Rect
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 不使用offsetChildrenVertical()方法, 在滚动的时候，通过自己计算来进行布局
 *
 * 对 CustomLinearManagerV3 进行优化, 不进行离屏操作，直接对超过屏幕的item回收，未超过的直接布局.
 *
 */
class CustomLinearManagerV4 : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var mTotalHeight = 0
    private val mSpareItemRect = SparseArray<Rect>()
    private var mSpareItemIsLayouted = SparseBooleanArray()

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        mSpareItemRect.clear()
        mSpareItemIsLayouted.clear()

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
            mSpareItemIsLayouted.put(index, false)
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

        mSumDy += transY
        val visibleRect = getVisibleRect()

        /**
         * 滚出屏幕的item进行回收, 在屏幕内的item重新布局.
         *
         * mSpareItemIsLayouted 记录的是 布局过的 item
         */
        for(index in (childCount - 1) downTo 0 step 1){
            val childView = getChildAt(index)!!
            val position = getPosition(childView)
            val rect = mSpareItemRect.get(position)
            if(Rect.intersects(visibleRect, rect)){
                layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)

//                if(transY >= 0){
//                    childView.rotationY = childView.rotationY + 1
//                }else{
//                    childView.rotationY = childView.rotationY - 1
//                }

                mSpareItemIsLayouted.put(position, true)

            }else{
                removeAndRecycleView(childView, recycler)
                mSpareItemIsLayouted.put(position, false)
            }
        }

        /**
         *  不采用offsetChildrenVertical，自己布局
         */
        if(transY >= 0) { //向上滚动, 底部留出空白，需要从缓存中获取item进行填充.
            val firstView = getChildAt(0)!!
            val firstPosition = getPosition(firstView)
            for(pos in firstPosition until itemCount){
                val rect = mSpareItemRect.get(pos)

                if(Rect.intersects(visibleRect, rect) && !mSpareItemIsLayouted.get(pos)){
                    val childView = recycler.getViewForPosition(pos)
                    addView(childView)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)

//                    childView.rotationY = childView.rotationY + 1

                    mSpareItemIsLayouted.put(pos, true)
                }
            }

        } else {  //向下滚动，顶部留出空白，需要从缓存中获取item进行填充.

            val lastView = getChildAt(childCount - 1)!!
            val lastPosition = getPosition(lastView)
            for(pos in lastPosition downTo 0 step 1){
                val rect = mSpareItemRect.get(pos)

                if(Rect.intersects(visibleRect, rect) && !mSpareItemIsLayouted.get(pos)){
                    val childView = recycler.getViewForPosition(pos)
                    addView(childView, 0)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left, rect.top - mSumDy, rect.right, rect.bottom - mSumDy)

//                    childView.rotationY = childView.rotationY - 1

                    mSpareItemIsLayouted.put(pos, true)
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