package com.example.a83661.screenrecorder.base

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager

/**
 * @author:honglei92
 * @time:2018/7/4
 */
abstract class BaseActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态栏
        var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        initView()
    }

    abstract fun initView()
}
