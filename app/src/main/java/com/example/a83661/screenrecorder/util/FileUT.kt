package com.example.a83661.screenrecorder.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * @author:honglei92
 * @time:2018/7/5
 */
class FileUT {
    companion object {
        fun openAssignFolder(activity: Activity, path: String) {
            val file = File(path)
            if (null == file || !file.exists()) {
                return
            }
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("oneshot", 0)
            intent.putExtra("configchange", 0)
            intent.setDataAndType(Uri.fromFile(file), "video/*")
            try {
                activity.startActivity(intent)
//            startActivity(Intent.createChooser(intent,"选择浏览工具"))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace();
            }

        }

        fun clearAssignFolder(mainActivity: Activity, directory: String) {
            val file = File(directory)
            if (null == file || !file.exists()) {
                return
            }
            var childFile: Array<File> = file.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                return
            }
            for (f: File in childFile) {
                f.delete()
            }
        }
    }
}

