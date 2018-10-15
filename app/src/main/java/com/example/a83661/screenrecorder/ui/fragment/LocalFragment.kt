package com.example.a83661.screenrecorder.ui.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.base.Constants
import com.example.a83661.screenrecorder.bean.Video
import com.example.a83661.screenrecorder.ui.adapter.LocalVideoAdapter
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat

/**
 * 本地视频
 *
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
class LocalFragment : Fragment() {
    var mVideoList = arrayListOf<Video>()
    var mLocalVideoLv: ListView? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_local_video, null)
        mLocalVideoLv = view.findViewById(R.id.localVideoLv)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        initView()
        return view
    }

    private fun initView() {
        mSwipeRefreshLayout!!.setOnRefreshListener {
            initVideoLv()
        }
        initVideoLv()
    }

    private fun initVideoLv() {
        val file = File(Constants.directory)
        mVideoList = getVideoList(file)
        val adapter = LocalVideoAdapter(activity!!, mVideoList)
        mLocalVideoLv!!.adapter = adapter
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    /**
     * 获取本地视频
     */
    private fun getVideoList(file: File): ArrayList<Video> {
        val mList = arrayListOf<Video>()
        file.listFiles(object : FileFilter {
            @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
            override fun accept(file: File?): Boolean {
                var name = file!!.name
                val i = name.indexOf(".")
                if (i != -1) {
                    name = name.substring(i)
                    if (name.equals(".mp4", true)) {
                        val video = Video()
                        file.usableSpace
                        video.name = file.name
                        video.path = file.absolutePath
                        video.createTime = getTime(file)
                        mList.add(video)
                        return true
                    }
                }
                return false
            }

        })
        return mList
    }

    private fun getTime(file: File): String? {
        val time = file.lastModified()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val result = formatter.format(time)
        return result
    }

    companion object {
        fun getInstance(): LocalFragment {
            val mRecordFragment = LocalFragment()
            return mRecordFragment
        }
    }
}