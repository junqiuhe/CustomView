package com.sample.custom.view.widget.multi_touch

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 跟踪指定手指轨迹
 */
class MultiTouchTest : View{

    private val mDefaultPaint: Paint

    private val mPoint: PointF

    private var mHasTargetPointer: Boolean = false
    private val mTargetPointerId = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        mDefaultPaint = Paint()
        mDefaultPaint.style = Paint.Style.FILL
        mDefaultPaint.isAntiAlias = true

        mPoint = PointF()
    }

    override fun onDraw(canvas: Canvas) {

        canvas.save()
        canvas.translate(width / 2f, height / 2f)

        mDefaultPaint.color = Color.BLUE

        canvas.drawText("追踪第${mTargetPointerId + 1}个按下手指的位置", 0F, 0F, mDefaultPaint)
        canvas.restore()

        if(mHasTargetPointer){
            mDefaultPaint.color = Color.BLACK
            canvas.drawCircle(mPoint.x, mPoint.y, 20f, mDefaultPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.actionMasked){

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN->{
                val actionIndex = event.actionIndex

                /**
                 * event.getPointerId(int pointerIndex) 通过 pointerIndex 获取 手指Id
                 *
                 * event.findPointerIndex(int pointerId)
                 */
                if(event.getPointerId(actionIndex) == mTargetPointerId){
                    mHasTargetPointer = true

                    val pointerIndex = event.findPointerIndex(mTargetPointerId)
                    mPoint.set(event.getX(pointerIndex), event.getY(pointerIndex))
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP->{
                val actionIndex = event.actionIndex
                if(event.getPointerId(actionIndex) == mTargetPointerId){
                    mHasTargetPointer = false
                    mPoint.set(0f, 0f)
                }
            }

            MotionEvent.ACTION_MOVE->{
                if(mHasTargetPointer){
                    val pointerIndex = event.findPointerIndex(mTargetPointerId)
                    mPoint.set(event.getX(pointerIndex), event.getY(pointerIndex))
                }
            }
        }

        invalidate()

        return true
    }
}