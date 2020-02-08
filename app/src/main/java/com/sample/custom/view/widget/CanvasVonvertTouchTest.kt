package com.sample.custom.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CanvasVonvertTouchTest : View{

    private val mDefaultPaint: Paint

    private val originMatrix: Matrix
    private val invertMatrix: Matrix

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        mDefaultPaint = Paint()
        mDefaultPaint.style = Paint.Style.FILL
        mDefaultPaint.color = Color.BLUE

        originMatrix = Matrix()
        invertMatrix = Matrix()
    }

    private var downX: Float = -1f
    private var downY: Float = -1f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE->{
                downX = event.x
                downY = event.y

                invalidate()
            }

            MotionEvent.ACTION_UP->{
                downX = -1f
                downY = -1f

                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(downX == -1f && downY == -1f){
            return
        }

        canvas.save()

        originMatrix.reset()
        originMatrix.setTranslate(width / 2f, height / 2f)

        invertMatrix.reset()
        originMatrix.invert(invertMatrix)

        canvas.matrix = originMatrix

        val points = floatArrayOf(downX, downY)
        invertMatrix.mapPoints(points)

//        canvas.drawCircle(downX, downY, 20f, mDefaultPaint)

        canvas.drawCircle(points[0], points[1], 20f, mDefaultPaint)
        canvas.restore()
    }
}