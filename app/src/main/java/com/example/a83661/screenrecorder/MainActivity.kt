package com.example.a83661.screenrecorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjection.Callback
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {
    val TAG = "honglei92"
    private val START = 0
    private val STOP = 1
    val REQUEST_MEDIA_PROJECTION = 2
    var mediaProjectionManager: MediaProjectionManager? = null
    var mMediaProjection: MediaProjection? = null
    var mMediaRecorder: MediaRecorder? = null
    var mResultCode: Int = 0
    var mResultData: Intent? = null
    var mWith: Int = 0
    var mHeight: Int = 0
    var mVirtualDisplay: VirtualDisplay? = null
    var mState: Int = 1
    var mScreenDensity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initRecorder()
        prepareRecorder()
        var rxPermissions = RxPermissions(this)
        rxPermissions.request(android.Manifest.permission.RECORD_AUDIO
                , android.Manifest.permission.CAMERA
                , android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {

                    } else {
                        Log.d(TAG, "Permission Denial: can't record")
                    }
                }

        startRecordBtn.setOnClickListener {
            if (startRecordBtn.text.toString() == (getString(R.string.start_record))) {
                startRecordBtn.text = getString(R.string.stop_record)
                onHandle(START)
                mState = 0
            } else {
                startRecordBtn.text = getString(R.string.start_record)
                onHandle(STOP)
                mState = 1
            }
        }
    }

    /**
     * 初始化屏幕尺寸
     */
    private fun init() {
        val mDisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        mWith = mDisplayMetrics.widthPixels
        mHeight = mDisplayMetrics.heightPixels
        mScreenDensity = mDisplayMetrics.densityDpi
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    /**
     * 初始化录屏参数
     */
    private fun initRecorder() {
        if (mMediaRecorder == null) {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder!!.setVideoEncodingBitRate(512 * 1000)
            mMediaRecorder!!.setVideoFrameRate(30)
            mMediaRecorder!!.setVideoSize(mWith, mHeight)
            mMediaRecorder!!.setOutputFile(getFilePath())
        }
    }

    /**
     * 准备录屏
     */
    private fun prepareRecorder() {
        try {
            mMediaRecorder!!.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }

    private fun onHandle(code: Int) {
        if (code == 0) {
            shareScreen()
        } else {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.reset()
            stopScreenSharing()
        }
    }

    private fun shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
            return
        }
        mVirtualDisplay = createVirtualDisplay()
        mMediaRecorder!!.start()
    }

    private fun stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection!!.createVirtualDisplay("MainActivity",
                mWith, mHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder!!.surface, null, null)
    }


    private fun setUpMediaProjection() {
        mMediaProjection = mediaProjectionManager!!.getMediaProjection(mResultCode, mResultData)
    }

    /**
     * 创建存储路径
     */
    private fun getFilePath(): String {
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
    private fun getCurSysDate(): Any? {
        return SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "Permission Denial: can't record")
                return
            }
            mResultCode = resultCode
            mResultData = data
            setUpMediaProjection()
            mMediaProjection!!.registerCallback(object : Callback() {
                override fun onStop() {
                    if (mState == START) {
                        startRecordBtn.setText(R.string.stop_record)
                        mMediaRecorder!!.stop()
                        mMediaRecorder!!.reset()
                    }
                    mMediaProjection = null
                    stopScreenSharing()
                }
            }, null)
            mVirtualDisplay = createVirtualDisplay()
            mMediaRecorder!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }
}
