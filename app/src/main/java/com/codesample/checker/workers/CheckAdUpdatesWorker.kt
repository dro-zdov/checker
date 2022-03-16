package com.codesample.checker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.repo.AvitoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException

@HiltWorker
class CheckAdUpdatesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepo: AvitoRepository,
    private val localRepo: AdDetailsRepository
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
        //TODO: show
    }

    data class DbAndNetworkPair(
        val fromDb: AdDetailsContainer,
        val fromNetwork: AdDetailsContainer?
    ) {
        fun isDifferent(): Boolean {
            return fromDb.details != fromNetwork?.details
        }
    }

}