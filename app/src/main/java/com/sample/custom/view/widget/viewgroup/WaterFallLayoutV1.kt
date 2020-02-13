package com.sample.custom.view.widget.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * 自定义瀑布流布局
 */
class WaterFallLayoutV1 : ViewGroup{

    /**
     * 水平，垂直item之间的间距
     */
    private val mHSpace: Int = 10
    private val mVSpace: Int = 10

    /**
     * 列数
     */
    private val mColumns: Int = 3

    /**
     * 记录每列到顶部的距离
     */
    private val mTops: IntArray = intArrayOf(0, 0 , 0)

    private var childWidth: Int = 0

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(childCount == 0){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)

        childWidth = (sizeWidth - (mColumns - 1) * mHSpace) / mColumns

        /**
         * 计算控件的宽度
         */
        val wrapWidth = if(childCount < mColumns){
            childWidth * childCount + mHSpace * (childCount - 1)
        }else{
            sizeWidth
        }

        clearTops()
        for(index in 0 until childCount){
            val childView = getChildAt(index)

            val childHeight = childView.measuredHeight * childWidth / childView.measuredWidth
            val minColumn = getMinHeightColumn()
            mTops[minColumn] += mVSpace + childHeight
        }

        val wrapHeight = getMaxHeight()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        setMeasuredDimension(
            if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) wrapWidth else sizeWidth,
            wrapHeight
        )
    }

    private fun clearTops(){
        for(index in 0 until mColumns){
            mTops[index] = 0
        }
    }

    private fun getMinHeightColumn(): Int {
        var minColumn = 0
        for(index in 0 until mTops.size){
            if(mTops[minColumn] > mTops[index]){
                minColumn = index
            }
        }
        return minColumn
    }

    private fun getMaxHeight(): Int {
        var maxHeight = 0
        for(index in 0 until mTops.size){
            if(mTops[index] > maxHeight){
                maxHeight = mTops[index]
            }
        }
        return maxHeight
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        clearTops()

        for(index in 0 until childCount){
            val childView: View = getChildAt(index)

            val childHeight = childView.measuredHeight * childWidth / childView.measuredWidth

            val minColumn = getMinHeightColumn()

            val left = (mHSpace  + childWidth) * minColumn
            val top = mTops[minColumn]

            childView.layout(left, top, left + childWidth, top + childHeight)

            mTops[minColumn] += mVSpace + childHeight
        }
    }
}