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
import android.support.annotation.RequiresApi
import android.util.DisplayMetrics
import android.util.Log
import com.example.a83661.screenrecorder.base.BaseActivity
import com.example.a83661.screenrecorder.util.FileUT
import com.example.a83661.screenrecorder.util.StringUT
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : BaseActivity() {
    val TAG = "honglei92"
    val REQUEST_MEDIA_PROJECTION = 2
    private val START = 0
    private val RUNNING = 1
    private val PAUSE = 2
    private val STOP = 3
    var mState: Int = STOP
    var mediaProjectionManager: MediaProjectionManager? = null
    var mMediaProjection: MediaProjection? = null
    var mMediaRecorder: MediaRecorder? = null
    var mVirtualDisplay: VirtualDisplay? = null
    var callback: Callback? = null
    var mResultCode: Int = 0
    var mResultData: Intent? = null
    var mWith: Int = 0
    var mHeight: Int = 0
    var mScreenDensity: Int = 0
    lateinit var disposable: Disposable

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    /**
     * 初始化权限和屏幕尺寸
     */
    private fun init() {
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
        val mDisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        mWith = mDisplayMetrics.widthPixels
        mHeight = mDisplayMetrics.heightPixels
        mScreenDensity = mDisplayMetrics.densityDpi
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        initView()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        startRecordBtn.setOnClickListener {
            if (mState == STOP) {
                onHandle(START)
            }
        }
        stopRecordBtn.setOnClickListener {
            if (mState == RUNNING) {
                onHandle(STOP)
            }
        }
        openLocalBtn.setOnClickListener {
            FileUT.openAssignFolder(this, StringUT.getDirectory())
        }
        clearLocalBtn.setOnClickListener {
            FileUT.clearAssignFolder(this, StringUT.getDirectory())
        }
    }

    /**
     * 初始化录屏参数
     */
    private fun initRecorder() {
        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder!!.setVideoEncodingBitRate(1024 * 1024 * 2)
        mMediaRecorder!!.setVideoFrameRate(60)
        mMediaRecorder!!.setVideoSize(mWith, mHeight)
        mMediaRecorder!!.setOutputFile(StringUT.getFilePath())
        prepareRecorder()
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
        initCallBack()
    }

    /**
     * 监听回调
     */
    private fun initCallBack() {
        callback = object : Callback() {
            override fun onStop() {
                if (mState == RUNNING) {
                    mMediaRecorder!!.stop()
                    mMediaRecorder!!.reset()
                }
                mMediaProjection = null
                stopScreenSharing()
            }
        }
    }

    /**
     * 点击事件
     */
    private fun onHandle(code: Int) {
        if (code == START) {
            mState = RUNNING
            initRecorder()
            shareScreen()
            runOnUiThread {
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                object : Observer<Long> {
                                    override fun onSubscribe(d: Disposable?) {
                                        disposable = d!!
                                    }

                                    override fun onComplete() {
                                    }

                                    override fun onNext(value: Long?) {
                                        Log.d(TAG, value.toString())
                                        secondsTv.text = value.toString() + "秒"
                                    }

                                    override fun onError(e: Throwable?) {
                                    }
                                })
            }
        } else if (code == STOP) {
            mState = STOP
            stopScreenSharing()
        }
    }

    /**
     * 开始录制
     */
    private fun shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
            return
        }
        mVirtualDisplay = createVirtualDisplay()
        mMediaRecorder!!.start()
        mState = START
    }

    private fun getObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {
            override fun onComplete() {

            }

            override fun onNext(value: Long?) {
                secondsTv.setText(value.toString() + "秒")
            }

            override fun onError(e: Throwable?) {

            }

        }
    }

    /**
     * 停止录制
     */
    private fun stopScreenSharing() {
        mMediaRecorder!!.stop()
        mMediaRecorder!!.reset()
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
        mState = STOP
        destroyMediaProjection()
        closeTimer()
    }

    /**
     * 结束定时器
     */
    private fun closeTimer() {
        disposable.dispose()
    }

    /**
     * 销毁projection
     */
    private fun destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection!!.unregisterCallback(callback)
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }

    /**
     * 创建display
     */
    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection!!.createVirtualDisplay("MainActivity",
                mWith, mHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder!!.surface, null, null)
    }

    /**
     * 绑定projection
     */

    private fun setUpMediaProjection() {
        mMediaProjection = mediaProjectionManager!!.getMediaProjection(mResultCode, mResultData)
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
            mMediaProjection!!.registerCallback(callback, null)
            mVirtualDisplay = createVirtualDisplay()
            mMediaRecorder!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }
}
