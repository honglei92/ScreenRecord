package com.example.a83661.screenrecorder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.a83661.screenrecorder.R
import java.io.File

/**
 * @author:honglei92
 * @time:2018/7/9
 */

class LocalGifAdapter(var context: Context, var gifPaths: ArrayList<String>) : Adapter<LocalGifAdapter.ViewHolder>() {
    lateinit var mOnItemClickListener: OnItemClickListener
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_local_gif, p0, false))
    }

    override fun getItemCount(): Int {
        return gifPaths.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(context).load(File(gifPaths[p1])).asGif().into(p0.mGifIv)
        p0.mShareGifiv.setOnClickListener {
            mOnItemClickListener.onItemClick(it, p1)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mGifIv: ImageView = itemView.findViewById(R.id.gifIv)
        var mShareGifiv: ImageView = itemView.findViewById(R.id.ivShareGif)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}