package com.sample.custom.view.widget.rv.layoutmanager

import android.graphics.Rect
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 水平布局实现 2D 画廊布局
 *
 * https://blog.csdn.net/harvic880925/article/details/86606873
 */
class CustomLinearManagerH2 : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private val mSpareItemRect = SparseArray<Rect>()
    private val mSpareItemIsLayouted = SparseBooleanArray()

    private var mTotalWidth: Int = 0
    private var itemWidth: Int = 0
    private var itemHeight: Int = 0

    private var mStartOffset: Int = 0

    private fun getIntervalWidth(): Int{
        return itemWidth / 2
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        mSpareItemRect.clear()
        mSpareItemIsLayouted.clear()

        if(itemCount <= 0){
            //离屏操作.
            detachAndScrapAttachedViews(recycler)
            return
        }
        detachAndScrapAttachedViews(recycler)

        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view, 0, 0)
        itemWidth = getDecoratedMeasuredWidth(view)
        itemHeight = getDecoratedMeasuredHeight(view)

        val intervalWidth = getIntervalWidth()

        //可见item的个数
        val visibleCount = Math.ceil(getSpaceWidth() / intervalWidth.toDouble()).toInt()

        /**
         * 起始offset
         */
//        mStartOffset = 0

        mStartOffset = width / 2 - intervalWidth

        var offsetX = 0

        for(position in 0 until itemCount){
            val rect = Rect(offsetX + mStartOffset, 0, offsetX + itemWidth + mStartOffset, itemHeight)
            mSpareItemRect.put(position, rect)
            mSpareItemIsLayouted.put(position, false)

            /**
             * 让其 item 被覆盖
             */
            offsetX += intervalWidth
        }

        for(index in 0 until visibleCount){
            val rect = mSpareItemRect.get(index)

            val childView = recycler.getViewForPosition(index)
            addView(childView)
            measureChildWithMargins(childView, 0, 0)
            layoutDecorated(childView, rect.left, rect.top, rect.right, rect.bottom)
        }

        mTotalWidth = Math.max(getSpaceWidth(), offsetX)
    }

    private fun getSpaceWidth(): Int {
        return width - paddingLeft - paddingRight
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    private var mSumDx: Int = 0

    private val mCacheRemovedView = mutableListOf<View>()

    private fun getMaxOffset(): Int{
        return (itemCount - 1) * getIntervalWidth()
    }


    /**
     * dx > 0 向左滚动
     * dx < 0 向右滚动
     */
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        var transX = dx

        /**
         * 边界检查
         */
        if(mSumDx + transX < 0){  //左边有空白
            transX = -mSumDx
        } else if(mSumDx + transX > getMaxOffset()){
            transX = getMaxOffset() - mSumDx
        }
        mSumDx += transX

        val visibleRect = getVisibleRect()

        /**
         * 回收已经不再recyclerView区域的item.
         */
        mCacheRemovedView.clear()

        for(index in 0 until childCount){
            val childView = getChildAt(index)!!
            val position = getPosition(childView)
            val rect = mSpareItemRect.get(position)
            if(Rect.intersects(visibleRect, rect)){
                layoutDecorated(childView, rect.left - mSumDx, rect.top, rect.right - mSumDx, rect.bottom)
                mSpareItemIsLayouted.put(position, true)

                handleChildView(childView, rect.left - mSumDx - mStartOffset)

            }else{
                mCacheRemovedView.add(childView)
                mSpareItemIsLayouted.put(position, false)
            }
        }

        val firstView = getChildAt(0)!!
        val lastView = getChildAt(childCount - 1)!!

        if(dx >=0) {
            /**
             * 向左滚动，RecyclerView的右边会留出空白
             */
            val firstPosition = getPosition(firstView)
            for(position in firstPosition until itemCount){
                val rect = mSpareItemRect.get(position)

                if(Rect.intersects(visibleRect, rect) && !mSpareItemIsLayouted.get(position)){
                    val childView = recycler.getViewForPosition(position)
                    addView(childView)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left - mSumDx, rect.top, rect.right - mSumDx, rect.bottom)

                    mSpareItemIsLayouted.put(position, true)

                    handleChildView(childView, rect.left - mSumDx - mStartOffset)
                }
            }

        } else {

            /**
             * 向右滚动，RecyclerView的左边会留出空白，填充RecyclerView的左边
             */
            val lastPosition = getPosition(lastView)
            for(position in lastPosition downTo 0 step  1){
                val rect = mSpareItemRect.get(position)
                if(Rect.intersects(visibleRect, rect) && !mSpareItemIsLayouted.get(position)){
                    val childView = recycler.getViewForPosition(position)
                    addView(childView, 0)
                    measureChildWithMargins(childView, 0, 0)
                    layoutDecorated(childView, rect.left - mSumDx, rect.top, rect.right - mSumDx, rect.bottom)

                    mSpareItemIsLayouted.put(position, true)

                    handleChildView(childView, rect.left - mSumDx - mStartOffset)
                }
            }
        }

        for(removedView in mCacheRemovedView){
            removeAndRecycleView(removedView, recycler)
        }

        return transX
    }

    private fun getVisibleRect(): Rect {
        return Rect(paddingLeft + mSumDx, 0, paddingLeft + getSpaceWidth() + mSumDx, itemHeight)
    }

    /**
     * @param moveX: item距离中心点的距离.
     */
    private fun handleChildView(child: View, moveX: Int){
        val radio = computeScale(moveX)

        child.scaleX = radio
        child.scaleY = radio
    }

    private fun computeScale(x: Int): Float {
        var scale = 1 - Math.abs(x * 1.0f / (8f * getIntervalWidth()))
        if (scale < 0) scale = 0f
        if (scale > 1) scale = 1f
        return scale
    }


    /**
     * 计算中间位置.
     */
    fun getCenterPosition(): Int{
        var position = mSumDx / getIntervalWidth()
        var more = mSumDx % getIntervalWidth()
        if(more > getIntervalWidth() * 0.5) position++
        return position
    }

    fun getFirstPosition(): Int{
        if(childCount <= 0){
            return 0
        }
        val firstView = getChildAt(0)!!
        return getPosition(firstView)
    }

    /**
     * 有疑问?
     */
    fun calculateDistance(velocityX: Int, distance: Double): Double {
        val extra = mSumDx % getIntervalWidth()

        return if (velocityX > 0) {
            if (distance < getIntervalWidth()) {
                (getIntervalWidth() - extra).toDouble()
            } else {
                distance - distance % getIntervalWidth() - extra.toDouble()
            }
        } else {
            if (distance < getIntervalWidth()) {
                extra.toDouble()
            } else {
                distance - distance % getIntervalWidth() + extra
            }
        }
    }

}