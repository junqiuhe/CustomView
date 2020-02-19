package com.sample.custom.view.widget.rv

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.sample.custom.view.widget.rv.layoutmanager.CustomLinearManagerH2
import android.hardware.SensorManager
import android.view.ViewConfiguration

/**
 * 更改子View的绘制顺序
 * 1、setChildrenDrawingOrderEnable(true)
 * 2、重写 getChildDrawingOrder 方法
 */
class CoverRecyclerView : RecyclerView{

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        /**
         * 开启可定制绘制顺序
         */
        isChildrenDrawingOrderEnabled = true
    }

    /**
     * @param childCount: 当前屏幕上可见的item个数
     * @param i: 当前迭代的下标
     * @return 表示当前item的绘制顺序，返回值越小，越先绘制；返回值越大，越后绘制.
     */
    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        if(layoutManager is CustomLinearManagerH2){
            val manager = layoutManager as CustomLinearManagerH2

            val centerIndex = manager.getCenterPosition() - manager.getFirstPosition()
            if(i == centerIndex){
                return childCount - 1
            }else if(i < centerIndex){
                return i
            }else{
                return centerIndex + (childCount - 1 - i)
            }
        }
        return i
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        if(layoutManager is CustomLinearManagerH2){
            val manager = layoutManager as CustomLinearManagerH2

            //缩小滚动距离
            var flingX = (velocityX * 0.4f).toInt()

            /**
             * velocity 计算出距离
             */
            val distance = getSplineFlingDistance(flingX)

            val newDistance = manager.calculateDistance(velocityX, distance)

            /**
             * 根据距离计算出 velocity
             */
            val fixVelocityX = getVelocity(newDistance)

            if (velocityX > 0) {
                flingX = fixVelocityX
            } else {
                flingX = -fixVelocityX
            }
            return super.fling(flingX, velocityY)
        }

        return super.fling(velocityX, velocityY)
    }

    /**
     * 根据松手后的滑动速度计算出fling的距离
     *
     * @param velocity
     * @return
     */
    private fun getSplineFlingDistance(velocity: Int): Double {
        val l = getSplineDeceleration(velocity)
        val decelMinusOne = DECELERATION_RATE - 1.0
        return mFlingFriction.toDouble() * getPhysicalCoeff().toDouble() * Math.exp(
            DECELERATION_RATE / decelMinusOne * l
        )
    }

    /**
     * 根据距离计算出速度
     *
     * @param distance
     * @return
     */
    private fun getVelocity(distance: Double): Int {
        val decelMinusOne = DECELERATION_RATE - 1.0
        val aecel =
            Math.log(distance / (mFlingFriction * mPhysicalCoeff)) * decelMinusOne / DECELERATION_RATE
        return Math.abs((Math.exp(aecel) * (mFlingFriction * mPhysicalCoeff) / INFLEXION).toInt())
    }

    /**
     * --------------fling辅助类---------------
     */
    private val INFLEXION = 0.35f // Tension lines cross at (INFLEXION, 1)
    private val mFlingFriction = ViewConfiguration.getScrollFriction()
    private val DECELERATION_RATE = (Math.log(0.78) / Math.log(0.9)).toFloat()
    private var mPhysicalCoeff = 0f

    private fun getSplineDeceleration(velocity: Int): Double {
        val ppi = this.resources.displayMetrics.density * 160.0f
        val mPhysicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)

                * 39.37f // inch/meter

                * ppi
                * 0.84f) // look and feel tuning


        return Math.log((INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff)).toDouble())
    }

    private fun getPhysicalCoeff(): Float {
        if (mPhysicalCoeff == 0f) {
            val ppi = this.resources.displayMetrics.density * 160.0f
            mPhysicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)

                    * 39.37f // inch/meter

                    * ppi
                    * 0.84f) // look and feel tuning
        }
        return mPhysicalCoeff
    }

}