package com.jigar.backup.service.util

import arrow.optics.copy
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.jigar.backup.App.Companion.application
import com.jigar.backup.R
import com.jigar.backup.adapter.WifiConfigurationAdapter
import com.jigar.backup.data.BackupProcessRepository
import com.jigar.backup.data.ProcessItem
import com.jigar.backup.data.currentIndex
import com.jigar.backup.data.msg
import com.jigar.backup.data.progress
import com.jigar.backup.database.entity.Network
import com.jigar.backup.rootservice.RemoteRootService
import com.jigar.backup.util.LogHelper
import com.jigar.backup.util.PathHelper

class BackupNetworksHelper(private val mBackupProcessRepo: BackupProcessRepository) {
    companion object {
        private const val TAG = "BackupNetworksHelper"
    }

    suspend fun start() {
        val networks = mBackupProcessRepo.getNetworks()
        networks.forEachIndexed { index, network ->
            mBackupProcessRepo.updateNetworksItem {
                copy {
                    ProcessItem.currentIndex set index
                    ProcessItem.msg set network.ssid
                    ProcessItem.progress set index.toFloat() / networks.size
                }
            }
        }
        val json = runCatching {
            val moshi: Moshi = Moshi.Builder().add(WifiConfigurationAdapter()).build()
            moshi.adapter<List<Network>>().toJson(networks)
        }.onFailure {
            LogHelper.e(TAG, "start", "Failed to serialize to json.", it)
        }.getOrNull()
        if (json != null) {
            val backupConfig = mBackupProcessRepo.getBackupConfig()
            val networksPath = PathHelper.getBackupNetworksDir(backupConfig.path)
            if (RemoteRootService.exists(networksPath).not() && RemoteRootService.mkdirs(networksPath).not()) {
                LogHelper.e(TAG, "start", "Failed to mkdirs: $networksPath.")
            }
            val configPath = PathHelper.getBackupNetworksConfigFilePath(backupConfig.path)
            RemoteRootService.deleteRecursively(configPath)
            RemoteRootService.writeText(configPath, json)
        } else {
            LogHelper.e(TAG, "start", "Failed to save networks, json is null")
        }

        mBackupProcessRepo.updateNetworksItem {
            copy {
                ProcessItem.currentIndex set networks.size
                ProcessItem.msg set application.getString(R.string.finished)
                ProcessItem.progress set 1f
            }
        }
    }
}
