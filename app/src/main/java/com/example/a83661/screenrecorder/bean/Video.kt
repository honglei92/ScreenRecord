package com.example.a83661.screenrecorder.bean

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

/**
 * 视频类
 */
class Video {


    /**
     * 名称
     */
    var name: String? = null
    /**
     * 路径
     */
    var path: String? = null
    /**
     *创建时间
     */
    var createTime: String? = null
    /**
     *时长
     */
    var duration: String? = null

    fun getThumbImg(): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            bitmap = retriever.frameAtTime
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        return bitmap
    }

    fun getDurationTime(): String? {
        var duration = ""
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(path)
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = (duration.toInt() / 1000).toString() + " s"
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } finally {
            try {
                mmr.release()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        return duration
    }

}