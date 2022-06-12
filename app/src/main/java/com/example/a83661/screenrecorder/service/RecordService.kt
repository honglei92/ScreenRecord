package com.example.a83661.screenrecorder.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.content.Context

import android.graphics.BitmapFactory

import android.os.Build
import android.support.v4.app.NotificationCompat

import android.util.Log
import com.example.a83661.screenrecorder.R


class RecordService : Service() {

    private val TAG: String?="RecordService"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notification();//通知显示可以写到onCreate中。不管是写到onCreate里面还是onStartCommand中，都要写到getMediaProjection方法调用之前


        return super.onStartCommand(intent, flags, startId)
    }

    fun notification() {
        Log.i(TAG, "notification: " + Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID="112"
            //Call Start foreground with notification
            val notificationIntent = Intent(this, RecordService::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            resources,
                            R.mipmap.ic_launcher
                        )
                    )
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Starting Service")
                    .setContentText("Starting monitoring service")
//                    .setTicker(NOTIFICATION_TICKER)
                    .setContentIntent(pendingIntent)
            val notification: Notification = notificationBuilder.build()
            val NOTIFICATION_CHANNEL_NAME = "3213123"
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val NOTIFICATION_CHANNEL_DESC="dasddad"
            channel.description = NOTIFICATION_CHANNEL_DESC
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            //notificationManager.notify(NOTIFICATION_ID, notification);
            val NOTIFICATION_ID = 123
            startForeground(
                NOTIFICATION_ID,
                notification
            ) //必须使用此方法显示通知，不能使用notificationManager.notify，否则还是会报上面的错误
        }
    }

}