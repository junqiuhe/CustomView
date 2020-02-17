package com.sample.custom.view.ui.rv.layoutmanager

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 如何自定义LayoutManager.
 *
 * https://blog.csdn.net/harvic880925/article/details/84789602
 */
class CustomLinearManagerV1 : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var mTotalHeight: Int = 0

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        var offsetY = 0
        for(index in 0 until itemCount){
            val view: View = recycler.getViewForPosition(index)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)
            layoutDecorated(view, 0, offsetY, width, offsetY + height)
            offsetY += height
        }

        mTotalHeight = Math.max(offsetY, getVerticallySpace())
    }

    private fun getVerticallySpace(): Int {
        return height - paddingTop - paddingBottom
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    /**
     *  dy表示每次在屏幕上滑动的位移.
     *  向下滑动 dy < 0
     *  向上滑动 dy > 0
     *
     *  一、如何判断是否已经滑倒顶部?
     *      所有的dy之和 <= 0
     *
     *  二、如何判断是否已经滑倒底部?
     *      其实我们知道需要知道所有Item的总高度 - recyclerView的一屏的高度，就得到滚动的偏移值。
     *      只要滚动的距离 > 偏移值，说明已经达到底部.
     */

    private var mSumDy = 0

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {

        var transY = dy

        if(mSumDy + transY < 0){
            transY = -mSumDy

        }else if(mSumDy + transY > mTotalHeight - getVerticallySpace()){
            transY = mTotalHeight - getVerticallySpace() - mSumDy
        }
        mSumDy += transY

        Log.d("hejq", "dy : " + transY)

        offsetChildrenVertical(-transY)

        return dy
    }
}