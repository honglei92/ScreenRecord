package com.example.a83661.screenrecorder.base

import android.os.Environment
import java.io.File

class Constants {
    companion object {
        val directory = Environment.getExternalStorageDirectory().toString() + File.separator + "ScreenRecordings"
    }
}

