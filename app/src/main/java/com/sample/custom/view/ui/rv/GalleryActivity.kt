package com.sample.custom.view.ui.rv

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.custom.view.R
import com.sample.custom.view.widget.rv.layoutmanager.CustomLinearManagerH1
import com.sample.custom.view.widget.rv.layoutmanager.CustomLinearManagerH2
import com.sample.custom.view.widget.rv.layoutmanager.CustomLinearManagerV4
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val adapter = GalleryAdapter()

        rv.layoutManager = CustomLinearManagerH2()
        rv.adapter = adapter
    }
}

class GalleryAdapter : RecyclerView.Adapter<GalleryViewHolder>() {

    private var viewCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        viewCount++
        Log.d("hejq", "onCreateViewHolder create count : $viewCount")

        return GalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false))
    }

    override fun getItemCount(): Int = 14

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        Log.d("hejq", "onBindViewHolder")

        holder.itemView.findViewById<TextView>(R.id.item_title_tv).text = "第${position + 1}个item"
        holder.itemView.findViewById<ImageView>(R.id.item_img_iv).setImageResource(getResource(holder.itemView.context, position))
    }

    private fun getResource(context: Context, index: Int): Int {
        return context.resources.getIdentifier("picture_$index", "drawable", context.packageName)
    }
}

class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
