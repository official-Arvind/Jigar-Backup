package com.jigar.backup.data

import com.jigar.backup.App.Companion.application
import com.jigar.backup.database.entity.App
import com.jigar.backup.util.AppsOptionSelectedBackup
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.FilterBackupUser
import com.jigar.backup.util.FiltersSystemAppsBackup
import com.jigar.backup.util.FiltersUserAppsBackup
import com.jigar.backup.util.filterApp
import com.jigar.backup.util.readBoolean
import com.jigar.backup.util.readInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AppRepository {
    companion object {
        private const val TAG = "AppsRepository"
    }

    val isBackupAppsSelected: Flow<Boolean> = application.readBoolean(AppsOptionSelectedBackup)

    val appsFiltered: Flow<List<App>> = combine(
        DatabaseHelper.appDao.loadFlowApps(),
        application.readInt(FilterBackupUser),
        application.readBoolean(FiltersUserAppsBackup),
        application.readBoolean(FiltersSystemAppsBackup),
    ) { apps, userId, filterUserApps, filterSystemApps ->
        apps.filterApp(userId, filterUserApps, filterSystemApps)
    }

    val appsFilteredAndSelected: Flow<List<App>> = appsFiltered.map { apps -> apps.filter { it.isSelected } }
}
