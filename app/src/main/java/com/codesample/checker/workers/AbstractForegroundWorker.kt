package com.codesample.checker.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.codesample.checker.R

abstract class AbstractForegroundWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val notificationId: Int
): CoroutineWorker(context, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WORKER_NOTIFICATION_CHANNEL_ID,
                applicationContext.getString(R.string.save_ad_details_notification_channel_name),
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, WORKER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.save_ad_details_notification_text))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        return ForegroundInfo(notificationId, notification)
    }

    companion object {
        private const val WORKER_NOTIFICATION_CHANNEL_ID = "workerNotificationChannelId"
    }
}