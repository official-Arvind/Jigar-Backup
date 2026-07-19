package com.jigar.backup.data

import com.jigar.backup.App.Companion.application
import com.jigar.backup.database.entity.Network
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.NetworksOptionSelectedBackup
import com.jigar.backup.util.readBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkRepository {
    companion object {
        private const val TAG = "NetworkRepository"
    }

    val isBackupNetworksSelected: Flow<Boolean> = application.readBoolean(NetworksOptionSelectedBackup)

    val networks: Flow<List<Network>> = DatabaseHelper.networkDao.loadFlowNetworks()
    val networksSelected: Flow<List<Network>> = networks.map { networks -> networks.filter { it.selected } }
}
