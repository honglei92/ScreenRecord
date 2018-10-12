package com.example.a83661.screenrecorder.ui.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.bean.Video

class LocalVideoAdapter(val context: Context, var list: ArrayList<Video>) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        Log.d("honglei92", "execute：" + p0)
        val viewHolder: ViewHolder
        val view: View
        if (p1 == null) {
            viewHolder = ViewHolder()
            view = View.inflate(context, R.layout.item_local_video, null)
            viewHolder.tvName = view.findViewById(R.id.tvName)
            viewHolder.tvCreateTime = view.findViewById(R.id.tvCreateTime)
            viewHolder.ivVideo = view.findViewById(R.id.ivImage)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = p1.tag as ViewHolder
        }
        viewHolder.tvName.text = list.get(p0).name
        viewHolder.tvCreateTime.text = "保存于: " + list.get(p0).createTime
        viewHolder.ivVideo.setImageBitmap(list.get(p0).getThumbImg())
        return view
    }

    override fun getItem(p0: Int): Any {
        return list.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    class ViewHolder {
        lateinit var tvName: TextView
        lateinit var tvCreateTime: TextView
        lateinit var ivVideo: ImageView
    }
}
