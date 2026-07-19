package com.jigar.backup.util

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.jigar.backup.App
import com.jigar.backup.workers.AppsUpdateWorker
import com.jigar.backup.workers.OthersUpdateWorker

object WorkManagerHelper {
    private const val APPS_UPDATE_WORK_NAME = "apps_update_work"
    private const val OTHERS_UPDATE_WORK_NAME = "others_update_work"

    fun enqueueAppsUpdateWork() {
        WorkManager.getInstance(App.application)
            .enqueueUniqueWork(APPS_UPDATE_WORK_NAME, ExistingWorkPolicy.KEEP, AppsUpdateWorker.buildRequest())
    }

    fun enqueueOthersUpdateWork() {
        WorkManager.getInstance(App.application)
            .enqueueUniqueWork(OTHERS_UPDATE_WORK_NAME, ExistingWorkPolicy.KEEP, OthersUpdateWorker.buildRequest())
    }
}
