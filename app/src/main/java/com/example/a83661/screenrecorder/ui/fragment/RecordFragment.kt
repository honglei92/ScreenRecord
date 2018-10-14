package com.example.a83661.screenrecorder.ui.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.a83661.screenrecorder.R
import com.example.a83661.screenrecorder.util.FileUT
import com.example.a83661.screenrecorder.util.StringUT
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_record.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 *
 *录制
 * @author: https://github.com/honglei92
 * @time: 2018/9/9
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RecordFragment : Fragment() {
    //定义常变量
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
    var callback: MediaProjection.Callback? = null
    var mResultCode: Int = 0
    var mResultData: Intent? = null
    var mWith: Int = 0
    var mHeight: Int = 0
    var mScreenDensity: Int = 0
    lateinit var disposable: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_record, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        init(view)
    }

    /**
     * 初始化权限和屏幕尺寸
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init(view: View) {
        //请求权限
        val rxPermissions = RxPermissions(activity as Activity)
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
        activity!!.windowManager.defaultDisplay.getMetrics(mDisplayMetrics)
        mWith = mDisplayMetrics.widthPixels
        mHeight = mDisplayMetrics.heightPixels
        mScreenDensity = mDisplayMetrics.densityDpi
        mediaProjectionManager = activity!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        initMyView(view)
    }

    /**
     * 初始化视图
     */
    private fun initMyView(view: View) {
        val mStartRecordBtn = view.findViewById<Button>(R.id.startRecordBtn)
        mStartRecordBtn.setOnClickListener {
            if (mState == STOP) {
                onHandle(START)
            }
        }
        val mPauseRecordBtn = view.findViewById<Button>(R.id.pauseRecordBtn)
        mPauseRecordBtn.setOnClickListener {
            if (mState == RUNNING) {
                onHandle(STOP)
            }
        }
        val mStopRecordBtn = view.findViewById<Button>(R.id.stopRecordBtn)
        mStopRecordBtn.setOnClickListener {
            if (mState == RUNNING) {
                onHandle(STOP)
            }
        }
        val mOpenLocalBtn = view.findViewById<Button>(R.id.openLocalBtn)
        mOpenLocalBtn.setOnClickListener {
            FileUT.openAssignFolder(activity, StringUT.getDirectory())
        }
        val mClearLocalBtn = view.findViewById<Button>(R.id.clearLocalBtn)
        mClearLocalBtn.setOnClickListener {
            FileUT.clearAssignFolder(activity, StringUT.getDirectory())
            Toast.makeText(activity, "清理完成", Toast.LENGTH_SHORT).show()
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
            activity!!.finish()
        } catch (e: IOException) {
            e.printStackTrace()
            activity!!.finish()
        }
        initCallBack()
    }

    /**
     * 监听回调
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initCallBack() {
        callback = object : MediaProjection.Callback() {
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onHandle(code: Int) {
        if (code == START) {
            mState = RUNNING
            initRecorder()
            shareScreen()
            activity!!.runOnUiThread {
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                object : Observer<Long> {
                                    override fun onComplete() {

                                    }

                                    override fun onSubscribe(d: Disposable) {
                                        disposable = d!!

                                    }

                                    override fun onNext(t: Long) {
                                        Log.d(TAG, t.toString())
                                        secondsTv.text = t.toString() + "秒"
                                    }

                                    override fun onError(e: Throwable) {

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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun shareScreen() {
        if (mMediaProjection == null) {
            activity!!.startActivityForResult(mediaProjectionManager!!.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
            return
        }
        mVirtualDisplay = createVirtualDisplay()
        mMediaRecorder!!.start()
        mState = START
    }

    /**
     * 停止录制
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection!!.createVirtualDisplay("MainActivity",
                mWith, mHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder!!.surface, null, null)
    }

    /**
     * 绑定projection
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUpMediaProjection() {
        mMediaProjection = mediaProjectionManager!!.getMediaProjection(mResultCode, mResultData)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDestroy() {
        super.onDestroy()
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }

    companion object {
        fun getInstance(): RecordFragment {
            val mRecordFragment = RecordFragment()
            return mRecordFragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onResult(resultCode: Int, data: Intent?) {
        mResultCode = resultCode
        mResultData = data
        setUpMediaProjection()
        mMediaProjection!!.registerCallback(callback, null)
        mVirtualDisplay = createVirtualDisplay()
        mMediaRecorder!!.start()
    }
}