package com.codesample.checker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.utils.ImageUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SaveAdDetailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localRepo: AdDetailsRepository,
    private val imageUtil: ImageUtil,
    private val gson: Gson
): AbstractForegroundWorker(context, workerParams, WORKER_NOTIFICATION_ID) {

    override suspend fun doWork(): Result {
        return try {
            val json = inputData.getString(KEY_AD_DETAILS) ?: return Result.failure()
            val adDetails = gson.fromJson<AdDetails>(json, object : TypeToken<AdDetails>() {}.type)
            val files = imageUtil.downloadImages(adDetails.images ?: emptyList())
            val container = AdDetailsContainer(adDetails, files)
            localRepo.insert(container)

            Result.success()
        }
        catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_AD_DETAILS = "adDetails"
        private const val WORKER_NOTIFICATION_ID = 1
    }
}