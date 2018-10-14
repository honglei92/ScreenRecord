package com.example.a83661.screenrecorder.ui

import android.app.Activity
import android.os.Bundle
import android.widget.MediaController
import com.example.a83661.screenrecorder.R
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : Activity() {
    var mc: MediaController? = null
    var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        path = intent.getStringExtra("path")
        initView()
    }

    private fun initView() {
        mc = MediaController(this)
        videoView.setVideoPath(path)
        videoView.setMediaController(mc)
        mc!!.setMediaPlayer(videoView)
        videoView.requestFocus()
        videoView.start()

    }

}