package com.example.a83661.screenrecorder.util

import android.os.Environment
import android.support.v4.app.FragmentActivity
import com.example.a83661.screenrecorder.base.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class StringUT {
    companion object {
        /**
         * 创建存储路径
         */
        fun getFilePath(activity: FragmentActivity?): String {
            var sb: StringBuffer = StringBuffer()
            sb.append(activity!!.getExternalFilesDir(Environment.DIRECTORY_MOVIES).toString())
            sb.append(Constants.directory)
            val directory = sb.toString()
            if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
                return null!!
            }
            val folder = File(directory)
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            val filePath: String
            if (success) {
                val videoName = ("capture" + getCurSysDate() + ".mp4")
                filePath = directory + File.separator + videoName
            } else {
                return null!!
            }
            return filePath
        }

        fun getDirectory(activity: FragmentActivity?): String {
            val path = activity!!.getExternalFilesDir(Environment.DIRECTORY_MOVIES).toString()
            return path + File.separator + "aScreenRecordings"

        }

        /**
         * 获取时间戳
         */
        fun getCurSysDate(): Any? {
            return SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        }
    }
}