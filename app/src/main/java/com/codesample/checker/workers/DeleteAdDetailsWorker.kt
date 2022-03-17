package com.codesample.checker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.utils.ImageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteAdDetailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localRepo: AdDetailsRepository,
    private val imageUtil: ImageUtil
): AbstractForegroundWorker(context, workerParams, WORKER_NOTIFICATION_ID) {

    override suspend fun doWork(): Result {
        return try {
            val id = inputData.getLong(KEY_AD_DETAILS_ID, -1L)
            if (id == -1L) return Result.failure()

            val history = localRepo.getHistoryList(id)
            history.forEach {
                imageUtil.deleteImages(it.files)
            }
            localRepo.delete(id)

            Result.success()
        }
        catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_AD_DETAILS_ID = "adDetailsId"
         const val WORKER_NOTIFICATION_ID = 2
    }
}