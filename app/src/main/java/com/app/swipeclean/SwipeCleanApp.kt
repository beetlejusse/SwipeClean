package com.app.swipeclean

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// @HiltAndroidApp triggers Hilt's code generation for the entire app.
// MUST be applied to Application class — without this, Hilt won't work.
@HiltAndroidApp
class SwipeCleanApp: Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
