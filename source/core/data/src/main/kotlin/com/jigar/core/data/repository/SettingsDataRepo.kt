package com.jigar.core.data.repository

import com.jigar.core.datastore.DbPreferencesDataSource
import com.jigar.core.datastore.KeyAppsUpdateTime
import com.jigar.core.datastore.KeyCompressionType
import com.jigar.core.model.CompressionType
import com.jigar.core.model.SettingsData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsDataRepo @Inject constructor(
    private val dbPreferencesDataSource: DbPreferencesDataSource,
) {
    val settingsData: Flow<SettingsData> = dbPreferencesDataSource.settingsData

    suspend fun setCompressionType(value: CompressionType) {
        dbPreferencesDataSource.edit(KeyCompressionType, value.name)
    }

    suspend fun setAppsUpdateTime(value: Long) {
        dbPreferencesDataSource.edit(KeyAppsUpdateTime, value)
    }
}
