package com.codesample.checker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.codesample.checker.workers.CheckAdUpdatesWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CheckerApplication: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        registerCheckAdUpdatesWorker()
    }

    private fun registerCheckAdUpdatesWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<CheckAdUpdatesWorker>(CHECK_AD_UPDATES_INTERVAL, TimeUnit.HOURS)
            .setInitialDelay(CHECK_AD_UPDATES_INTERVAL, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CHECK_AD_UPDATES_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

    }

    companion object {
        private const val CHECK_AD_UPDATES_WORK = "checkAdUpdates"
        private const val CHECK_AD_UPDATES_INTERVAL = 12L
    }
}