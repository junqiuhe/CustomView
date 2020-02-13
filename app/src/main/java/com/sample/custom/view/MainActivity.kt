package com.sample.custom.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sample.custom.view.ui.CanvasVonvertTouchTestActivity
import com.sample.custom.view.ui.RegionClickedActivity
import com.sample.custom.view.ui.multi_touch.MultiTouchTestActivity
import com.sample.custom.view.ui.viewgroup.MyViewGroupActivity
import com.sample.custom.view.ui.viewgroup.WaterFallActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun useRegionClicked(view: View){
        startActivity(Intent(this, RegionClickedActivity::class.java))
    }

    fun useCanvasVonvertTouchTest(view: View){
        startActivity(Intent(this, CanvasVonvertTouchTestActivity::class.java))
    }

    fun useMultiTouchTest(view: View){
        startActivity(Intent(this, MultiTouchTestActivity::class.java))
    }

    fun useViewGroupTest(view: View){
        startActivity(Intent(this, MyViewGroupActivity::class.java))
    }

    fun useWaterFallTest(view: View){
        startActivity(Intent(this, WaterFallActivity::class.java))
    }
}
