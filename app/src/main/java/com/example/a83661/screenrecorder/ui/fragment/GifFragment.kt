package com.example.a83661.screenrecorder.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a83661.screenrecorder.R

/**
 * 转gif
 *
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
class GifFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_local_gif, null)
        initView()
        return view
    }

    private fun initView() {

    }

    companion object {
        fun getInstance(): GifFragment {
            val mRecordFragment = GifFragment()
            return mRecordFragment
        }
    }
}