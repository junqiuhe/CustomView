package com.sample.custom.view.ui.rv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.custom.view.R
import com.sample.custom.view.widget.rv.itemdecoration.CustomLinearItemDecoration
import com.sample.custom.view.widget.rv.layoutmanager.CustomLinearManagerV4

class RvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv)

//        simpleRecyclerView()
//        learnItemDecoration()
        useCustomLayoutManager()
    }

    /**
     * RecyclerView的简单使用
     */
    private fun simpleRecyclerView(){
        /**
         * 系统自定义的ItemDecoration
         */
        val itemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)

        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SimpleRvAdapter(getDataList())
        rv.addItemDecoration(itemDecoration)
    }

    private fun learnItemDecoration(){
        /**
         * 系统自定义的ItemDecoration
         */
        val itemDecoration = CustomLinearItemDecoration(this)

        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SimpleRvAdapter(getDataList())
        rv.addItemDecoration(itemDecoration)
    }

    private fun useCustomLayoutManager(){
        /**
         * 系统自定义的ItemDecoration
         */
        val itemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)

        val rv = findViewById<RecyclerView>(R.id.rv)
//        rv.layoutManager = LinearLayoutManager(this)
        rv.layoutManager = CustomLinearManagerV4()
        rv.adapter = SimpleRvAdapter(getDataList())
        rv.addItemDecoration(itemDecoration)
    }

    private fun getDataList(): List<String>{
        val list = mutableListOf<String>()
        for(index in 0 until 200){
            list.add("第 ${index + 1} item")
        }
        return list
    }
}

class SimpleRvAdapter(
    private val mDataList: List<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mCreateViewHolderNum = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mCreateViewHolderNum++
        Log.d("hejq", "onCreateViewHolder count : $mCreateViewHolderNum")

        val itemView  = if(viewType == ITEM_TYPE_TITLE){
            LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false)
        }
        return DefaultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("hejq", "onBindViewHolder")
        holder.itemView.findViewById<TextView>(R.id.tv).text = mDataList[position]
    }

    override fun getItemViewType(position: Int): Int {
        return if((position + 1) % 5 == 0){
            ITEM_TYPE_TITLE
        }else{
            ITEM_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int = mDataList.size

    companion object{
        private val ITEM_TYPE_NORMAL = 1
        private val ITEM_TYPE_TITLE = 2
    }
}

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
