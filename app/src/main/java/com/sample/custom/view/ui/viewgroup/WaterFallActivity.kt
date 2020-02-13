package com.sample.custom.view.ui.viewgroup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.sample.custom.view.R
import com.sample.custom.view.widget.viewgroup.WaterFallLayoutParams
import com.sample.custom.view.widget.viewgroup.WaterFallLayoutV1
import com.sample.custom.view.widget.viewgroup.WaterFallLayoutV2
import java.util.*

class WaterFallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_fall)

        val waterFallView1 = findViewById<WaterFallLayoutV1>(R.id.water_fall_layout_v1)

        val waterFallView2 = findViewById<WaterFallLayoutV2>(R.id.water_fall_layout_v2)

        val addViewBtn = findViewById<Button>(R.id.add_view_btn)
        addViewBtn.setOnClickListener{
            addImageViewToWaterFallLayoutV2(waterFallView2)
        }
    }

    private fun addImageViewToWaterFallLayoutV1(waterFallLayoutV1: WaterFallLayoutV1) {
        var random = Random()
        var num = random.nextInt(14)

//        var layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

        val imageView = ImageView(this)
        imageView.setImageResource(getResource(this, num))
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        waterFallLayoutV1.addView(imageView)
    }

    private fun addImageViewToWaterFallLayoutV2(waterFallLayoutV2: WaterFallLayoutV2) {
        var random = Random()
        var num = random.nextInt(14)

//        var layoutParams = WaterFallLayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

        val imageView = ImageView(this)
        imageView.setImageResource(getResource(this, num))
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        waterFallLayoutV2.addView(imageView)
    }

    private fun getResource(context: Context, index: Int): Int {
        return context.resources.getIdentifier("picture_$index", "drawable", context.packageName)
    }
}
