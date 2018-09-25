package com.example.a83661.screenrecorder.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a83661.screenrecorder.R

/**
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
class LocalFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_record, null)
        initView()
        return view
    }

    private fun initView() {

    }

    companion object {
        fun getInstance(): LocalFragment {
            val mRecordFragment = LocalFragment()
            return mRecordFragment
        }
    }
}