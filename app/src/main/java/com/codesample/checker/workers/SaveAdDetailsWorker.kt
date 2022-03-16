package com.codesample.checker.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.codesample.checker.R
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.entities.details.Image
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.repo.AvitoRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.security.MessageDigest
import java.util.*

@HiltWorker
class SaveAdDetailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepo: AvitoRepository,
    private val localRepo: AdDetailsRepository,
    private val gson: Gson
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val json = inputData.getString(KEY_AD_DETAILS) ?: return Result.failure()
            val adDetails = gson.fromJson<AdDetails>(json, object : TypeToken<AdDetails>() {}.type)
            val files = adDetails.images.map {
                downloadImage(it)
            }
            val container = AdDetailsContainer(adDetails, files)
            localRepo.insert(container)

            Result.success()
        }
        catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun downloadImage(image: Image): File {
        val url = image.img1280x960
        val filesDir = File("${applicationContext.filesDir}/images").also { it.mkdir() }
        val md5Name = MessageDigest.getInstance("MD5").run {
            update(url.toByteArray())
            digest().joinToString(separator = "") { byteToString(it) }
        }
        val file = File(filesDir, "${md5Name}.jpg")
        if (!file.exists()) {
            remoteRepo.downloadFile(url, file)
        }
        return file
    }

    // Use this workaround, because android not supporting DatatypeConverter.printHexBinary()
    private fun byteToString(byte: Byte): String {
        return Integer.toHexString(byte.toInt()).run {
            when (length) {
                1 -> "0$this"
                2 -> this
                else -> substring(length -2, length)
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WORKER_NOTIFICATION_CHANNEL_ID,
                applicationContext.getString(R.string.save_ad_details_notification_channel_name),
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, WORKER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.save_ad_details_notification_text))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        return ForegroundInfo(WORKER_NOTIFICATION_ID, notification)
    }

    companion object {
        const val KEY_AD_DETAILS = "adDetails"
        private const val WORKER_NOTIFICATION_CHANNEL_ID = "saveAdDetailsWorker"
        private const val WORKER_NOTIFICATION_ID = 1
    }
}