package com.jigar.backup.service.util

import arrow.optics.copy
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.jigar.backup.App.Companion.application
import com.jigar.backup.R
import com.jigar.backup.data.BackupProcessRepository
import com.jigar.backup.data.ProcessItem
import com.jigar.backup.data.currentIndex
import com.jigar.backup.data.msg
import com.jigar.backup.data.progress
import com.jigar.backup.database.entity.CallLog
import com.jigar.backup.rootservice.RemoteRootService
import com.jigar.backup.util.LogHelper
import com.jigar.backup.util.PathHelper

class BackupCallLogsHelper(private val mBackupProcessRepo: BackupProcessRepository) {
    companion object {
        private const val TAG = "BackupCallLogsHelper"
    }

    suspend fun start() {
        val callLogs = mBackupProcessRepo.getCallLogs()
        callLogs.forEachIndexed { index, callLog ->
            mBackupProcessRepo.updateCallLogsItem {
                copy {
                    ProcessItem.currentIndex set index
                    ProcessItem.msg set callLog.id.toString()
                    ProcessItem.progress set index.toFloat() / callLogs.size
                }
            }
        }
        val json = runCatching {
            val moshi: Moshi = Moshi.Builder().build()
            moshi.adapter<List<CallLog>>().toJson(callLogs)
        }.onFailure {
            LogHelper.e(TAG, "start", "Failed to serialize to json.", it)
        }.getOrNull()
        if (json != null) {
            val backupConfig = mBackupProcessRepo.getBackupConfig()
            val callLogsPath = PathHelper.getBackupCallLogsDir(backupConfig.path)
            if (RemoteRootService.exists(callLogsPath).not() && RemoteRootService.mkdirs(callLogsPath).not()) {
                LogHelper.e(TAG, "start", "Failed to mkdirs: $callLogsPath.")
            }
            val configPath = PathHelper.getBackupCallLogsConfigFilePath(backupConfig.path)
            RemoteRootService.deleteRecursively(configPath)
            RemoteRootService.writeText(configPath, json)
        } else {
            LogHelper.e(TAG, "start", "Failed to save call logs, json is null")
        }

        mBackupProcessRepo.updateCallLogsItem {
            copy {
                ProcessItem.currentIndex set callLogs.size
                ProcessItem.msg set application.getString(R.string.finished)
                ProcessItem.progress set 1f
            }
        }
    }
}
