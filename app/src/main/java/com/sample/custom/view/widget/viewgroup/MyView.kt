package com.sample.custom.view.widget.viewgroup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MyView : View{

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.BLUE
        mPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //默认的实现,
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var sMeasureWidth = MeasureSpec.getSize(widthMeasureSpec)
        var sMeasureHeight = MeasureSpec.getSize(heightMeasureSpec)

        if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED){
            sMeasureWidth = 200
        }

        if(heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED){
            sMeasureHeight = 200
        }
        setMeasuredDimension(sMeasureWidth, sMeasureHeight)
    }

    private val mPaint: Paint

    override fun onDraw(canvas: Canvas) {
        val radius = Math.min(width, height) / 2f
        canvas.drawCircle(width /2f, height/ 2f, radius, mPaint)
    }
}