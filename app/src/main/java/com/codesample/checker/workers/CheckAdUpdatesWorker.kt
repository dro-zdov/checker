package com.codesample.checker.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codesample.checker.MainActivity
import com.codesample.checker.R
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.repo.AvitoRepository
import com.codesample.checker.utils.ImageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException

@HiltWorker
class CheckAdUpdatesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepo: AvitoRepository,
    private val localRepo: AdDetailsRepository,
    private val imageUtil: ImageUtil
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val updates = localRepo.getAllLatestList()
                .map { fromDb ->
                    val fromNetwork = try {
                        remoteRepo.getAdDetails(fromDb.details.id)
                    } catch (httpException: HttpException) {
                        if (httpException.code() == 404) {
                            null // Ad was deleted from backend
                        } else {
                            throw httpException
                        }
                    }
                    DbAndNetworkPair(fromDb, fromNetwork)
                }
                .filter { pair -> pair.isDifferent() }
                .mapNotNull { pair -> pair.fromNetwork }
                .map { update ->
                    val files = imageUtil.downloadImages(update.details.images ?: emptyList())
                    AdDetailsContainer(update.details, files)
                }

            if (updates.isNotEmpty()) {
                localRepo.insert(*updates.toTypedArray())
                showNotification()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MAIN_NOTIFICATION_CHANNEL_ID,
                applicationContext.getString(R.string.main_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.FLAG_NAVIGATE_TO_TRACKED_LIST, true)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }
        else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,flags)

        val notification = NotificationCompat.Builder(applicationContext, MAIN_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.some_ads_has_changed_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(SOME_ADS_CHANGED_NOTIFICATION_ID, notification)
    }

    data class DbAndNetworkPair(
        val fromDb: AdDetailsContainer,
        val fromNetwork: AdDetailsContainer?
    ) {
        fun isDifferent(): Boolean {
            return fromDb.details != fromNetwork?.details
        }
    }

    companion object {
        const val SOME_ADS_CHANGED_NOTIFICATION_ID = 3
        const val MAIN_NOTIFICATION_CHANNEL_ID = "mainNotificationChannelId"
    }

}