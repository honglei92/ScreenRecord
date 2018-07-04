package com.example.a83661.screenrecorder.util

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StringUT {
    companion object {
        /**
         * 创建存储路径
         */
        fun getFilePath(): String {
            var directory = Environment.getExternalStorageDirectory().toString() + File.separator + "ScreenRecordings"
            if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
                return null!!
            }
            var folder = File(directory)
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            var filePath: String
            if (success) {
                var videoName = ("capture" + getCurSysDate() + ".mp4")
                filePath = directory + File.separator + videoName
            } else {
                return null!!
            }
            return filePath
        }

        /**
         * 获取时间戳
         */
        fun getCurSysDate(): Any? {
            return SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        }
    }
}