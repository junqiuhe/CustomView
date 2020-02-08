package com.sample.custom.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class RegionClickedView : View{

    private val circlePath: Path
    private val circleRegion: Region

    private val globalRegion: Region

    private val translateMatrix: Matrix
    private val invertMatrix: Matrix

    private val defaultPaint: Paint

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){

        defaultPaint = Paint()
        defaultPaint.style = Paint.Style.FILL
        defaultPaint.color = Color.BLACK

        circlePath = Path()
        circleRegion = Region()

        globalRegion = Region()

        translateMatrix = Matrix()
        invertMatrix = Matrix()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                val points = floatArrayOf(event.x, event.y)

                invertMatrix.mapPoints(points)

                if(circleRegion.contains(points[0].toInt(), points[1].toInt())){
                    Toast.makeText(context, "被点击了", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        val transX = width / 2f
        val transY = height / 2f

        translateMatrix.reset()
        translateMatrix.setTranslate(transX, transY)

        /**
         * 计算矩阵的逆矩阵.
         */
        invertMatrix.reset()
        translateMatrix.invert(invertMatrix)

        canvas.matrix = translateMatrix

        circlePath.reset()
        circlePath.addCircle(0f, 0f, 100f, Path.Direction.CCW)

        /**
         * circleRegion区域是相对于屏幕的坐标系.
         */
        globalRegion.set(-width, -height, width, height)
        circleRegion.setPath(circlePath, globalRegion)

        canvas.drawPath(circlePath, defaultPaint)
        canvas.restore()
    }
}