package com.example.a83661.screenrecorder.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.androidkun.xtablayout.XTabLayout
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.base.BaseActivity
import com.example.a83661.screenrecorder.ui.fragment.GifFragment
import com.example.a83661.screenrecorder.ui.fragment.LocalFragment
import com.example.a83661.screenrecorder.ui.fragment.RecordFragment
import kotlinx.android.synthetic.main.activity_index.*

/**
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
class IndexActivity : BaseActivity() {
    val REQUEST_MEDIA_PROJECTION = 2
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var recordFragment: RecordFragment? = null

    override fun initView() {
        setContentView(R.layout.activity_index)
        val titles = arrayOf("录制", "本地视频", "转gif")
        mViewPagerCom.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                when (position) {
                    0 -> {
                        recordFragment = RecordFragment.getInstance()
                        return recordFragment
                    }
                    1
                    -> return LocalFragment.getInstance()
                    2 -> return GifFragment.getInstance()
                }
                return null
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }
        mTabCom.setupWithViewPager(mViewPagerCom)
        mTabCom.addOnTabSelectedListener(
                object : XTabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: XTabLayout.Tab) {
                        mViewPagerCom.currentItem = tab.position
                    }

                    override fun onTabUnselected(tab: XTabLayout.Tab) {

                    }

                    override fun onTabReselected(tab: XTabLayout.Tab) {

                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d("TAG", "Permission Denial: can't record")
                return
            }
            recordFragment!!.onResult(resultCode, data)

        }
    }

}