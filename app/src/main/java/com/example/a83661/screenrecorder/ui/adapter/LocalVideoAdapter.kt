package com.example.a83661.screenrecorder.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.bean.Video
import com.example.a83661.screenrecorder.ui.VideoPlayActivity
import java.io.File

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
            viewHolder.tvDurationTime = view.findViewById(R.id.tvDurationTime)
            viewHolder.ivVideo = view.findViewById(R.id.ivImage)
            viewHolder.ivShare = view.findViewById(R.id.ivShare)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = p1.tag as ViewHolder
        }
        viewHolder.tvName.text = list[p0].name
        viewHolder.tvCreateTime.text = "保存于: " + list[p0].createTime
        viewHolder.tvDurationTime.text = "时长: " + list[p0].getDurationTime()
        viewHolder.ivVideo.setImageBitmap(list[p0].getThumbImg())
        viewHolder.ivVideo.setOnClickListener {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("path", list[p0].path)
            context.startActivity(intent)
        }
        viewHolder.ivShare.setOnClickListener {

            /* val textIntent = Intent(Intent.ACTION_SEND)
             textIntent.type = "text/plain"
             textIntent.putExtra(Intent.EXTRA_TEXT, "视频路径:" + list.get(0).path)*/
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(list[p0].path)))
            context.startActivity(Intent.createChooser(shareIntent, "分享"))

        }
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
        lateinit var tvDurationTime: TextView
        lateinit var ivVideo: ImageView
        lateinit var ivShare: ImageView
    }
}
