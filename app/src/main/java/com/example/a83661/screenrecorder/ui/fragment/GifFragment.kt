package com.example.a83661.screenrecorder.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.base.Constants
import com.example.a83661.screenrecorder.bean.Video
import com.example.a83661.screenrecorder.ui.adapter.LocalGifAdapter
import java.io.File
import java.io.FileFilter

/**
 * 转gif
 *
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
class GifFragment : Fragment(), LocalGifAdapter.OnItemClickListener {
    var mAdapter: LocalGifAdapter? = null
    override fun onItemClick(view: View, position: Int) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(mGifList[position])))
        activity!!.startActivity(Intent.createChooser(shareIntent, "分享"))
    }

    var mGifList = arrayListOf<String>()
    var mRefreshLayout: SwipeRefreshLayout? = null
    var mRecycleView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_local_gif, null)
        mRefreshLayout = view.findViewById(R.id.swipeRefresh)
        mRecycleView = view.findViewById(R.id.localGifRv)
        initView()
        return view
    }

    private fun initView() {
        mRefreshLayout!!.setOnRefreshListener {
            initGifRv()
        }
        initGifRv()
    }

    private fun initGifRv() {
        val file = File(Constants.directory)
        mGifList = getGifList(file)
        mAdapter = LocalGifAdapter(activity!!, mGifList)
        mAdapter!!.setOnItemClickListener(this)
        mRecycleView!!.adapter = mAdapter
        mRecycleView!!.layoutManager = object : GridLayoutManager(activity, 3) {}
        mRecycleView!!.itemAnimator = DefaultItemAnimator()
        mRefreshLayout!!.isRefreshing = false
    }

    private fun getGifList(file: File): ArrayList<String> {
        val mList = arrayListOf<String>()
        file.listFiles(FileFilter { file ->
            var name = file!!.name
            val i = name.indexOf(".")
            if (i != -1) {
                name = name.substring(i)
                if (name.equals(".gif", true)) {
                    val video = Video()
                    file.usableSpace
                    mList.add(file.absolutePath)
                    return@FileFilter true
                }
            }
            false
        })
        return mList
    }

    companion object {
        fun getInstance(): GifFragment {
            val mRecordFragment = GifFragment()
            return mRecordFragment
        }
    }
}