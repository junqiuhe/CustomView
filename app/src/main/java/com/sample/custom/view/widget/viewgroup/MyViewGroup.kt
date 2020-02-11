package com.sample.custom.view.widget.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * 自定义ViewGroup。
 */
class MyViewGroup : ViewGroup{

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //循环调用以测量子View的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        /**
         * 测量自身的 ViewGroup 的大小.
         */
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        var measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED){
            measureWidth = 0
            for(index in 0 until childCount){
                val childView = getChildAt(index)
                val lp = childView.layoutParams as MarginLayoutParams
                measureWidth += childView.measuredWidth + lp.leftMargin + lp.rightMargin
            }
        }

        if(heightMode == MeasureSpec.AT_MOST || heightMeasureSpec == MeasureSpec.UNSPECIFIED){
            measureHeight = 0
            for(index in 0 until childCount){
                val childView = getChildAt(index)
                val lp = childView.layoutParams as MarginLayoutParams
                measureHeight = Math.max(measureHeight, childView.measuredHeight + lp.topMargin + lp.bottomMargin)
            }
        }

        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = 0
        var top = 0
        for(index in 0 until childCount){
            val childView = getChildAt(index)

            val lp = childView.layoutParams as MarginLayoutParams

            left += lp.leftMargin
            top  = (measuredHeight - lp.topMargin - lp.bottomMargin) / 2 - childView.measuredHeight / 2 + lp.topMargin

            childView.layout(left, top, left + childView.measuredWidth, top + childView.measuredHeight)

            left += childView.measuredWidth + lp.rightMargin
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }
}