package com.jigar.core.data.repository

import android.content.Context
import com.jigar.core.common.util.toSpaceString
import com.jigar.core.data.R
import com.jigar.core.database.dao.DirectoryDao
import com.jigar.core.database.dao.PackageDao
import com.jigar.core.datastore.ConstantUtil
import com.jigar.core.datastore.ConstantUtil.DEFAULT_PATH_PARENT
import com.jigar.core.datastore.readBackupSavePath
import com.jigar.core.datastore.saveBackupSavePath
import com.jigar.core.model.StorageType
import com.jigar.core.model.database.DirectoryEntity
import com.jigar.core.model.database.DirectoryUpsertEntity
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.rootservice.util.withIOContext
import com.jigar.core.util.PathUtil
import com.jigar.core.util.command.PreparationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DirectoryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val directoryDao: DirectoryDao,
    private val packageDao: PackageDao,
    private val rootService: RemoteRootService,
) {
    fun queryActiveDirectoriesFlow(storageType: StorageType) = directoryDao.queryActiveDirectoriesFlow(storageType).distinctUntilChanged()

    private suspend fun resetDir() = selectDir(
        path = ConstantUtil.DEFAULT_PATH,
        id = directoryDao.queryDefaultDirectoryId(StorageType.INTERNAL),
    )

    suspend fun deleteDir(entity: DirectoryEntity) = run {
        if (entity.selected) resetDir()
        directoryDao.delete(entity)
    }

    suspend fun addDir(pathList: List<String>) {
        val customDirList = mutableListOf<DirectoryUpsertEntity>()
        pathList.forEach { pathString ->
            if (pathString.isNotEmpty()) {
                val parent = PathUtil.getParentPath(pathString)
                val child = PathUtil.getFileName(pathString)

                // Custom storage
                val dir = DirectoryUpsertEntity(
                    id = directoryDao.queryId(parent = parent, child = child),
                    title = "",
                    parent = parent,
                    child = child,
                    storageType = StorageType.CUSTOM,
                )
                customDirList.add(dir)
            }
        }

        directoryDao.upsert(customDirList)
    }

    suspend fun selectDir(entity: DirectoryEntity) = run {
        packageDao.delete(context.readBackupSavePath().first())
        selectDir(entity.path, entity.id)
    }

    private suspend fun selectDir(path: String, id: Long?) = run {
        if (id != null) {
            context.saveBackupSavePath(path)
            directoryDao.select(id = id)
        }
    }

    suspend fun update() {
        withIOContext {
            // Inactivate all directories
            directoryDao.updateActive(active = false)

            // Internal storage
            val internalList = rootService.listFilePaths(ConstantUtil.STORAGE_EMULATED_PATH, listFiles = false)
                .filter { it.substring(it.lastIndexOf("/") + 1).toIntOrNull() != null }.toMutableList() // Just select 0 10 999 etc.
            if (internalList.contains(DEFAULT_PATH_PARENT).not()) {
                internalList.add(DEFAULT_PATH_PARENT)
            }
            val internalDirs = mutableListOf<DirectoryUpsertEntity>()
            for (storageItem in internalList) {
                // e.g. /data/media/0
                runCatching {
                    val child = ConstantUtil.DEFAULT_PATH_CHILD
                    internalDirs.add(
                        DirectoryUpsertEntity(
                            id = directoryDao.queryId(parent = storageItem, child = child),
                            title = "",
                            parent = storageItem,
                            child = child,
                            storageType = StorageType.INTERNAL,
                        )
                    )
                }
            }
            directoryDao.upsert(internalDirs)

            // External storage
            val externalList = PreparationUtil.listExternalStorage().out
            val externalDirs = mutableListOf<DirectoryUpsertEntity>()
            for (storageItem in externalList) {
                // e.g. /mnt/media_rw/E7F9-FA61
                runCatching {
                    val child = ConstantUtil.DEFAULT_PATH_CHILD
                    externalDirs.add(
                        DirectoryUpsertEntity(
                            id = directoryDao.queryId(parent = storageItem, child = child),
                            title = "",
                            parent = storageItem,
                            child = child,
                            storageType = StorageType.EXTERNAL,
                            active = true,
                        )
                    )
                }
            }
            directoryDao.upsert(externalDirs)

            // Activate backup/restore directories except external directories
            directoryDao.updateActive(excludeType = StorageType.EXTERNAL, active = true)

            // Read statFs of each storage
            directoryDao.queryActiveDirectories().forEach { entity ->
                val parent = entity.parent
                entity.error = ""
                val statFs = rootService.readStatFs(parent)
                entity.childUsedBytes = rootService.calculateSize(entity.path)
                entity.availableBytes = statFs.availableBytes
                entity.totalBytes = statFs.totalBytes
                if (entity.storageType == StorageType.EXTERNAL) {
                    val tags = mutableListOf<String>()
                    val type = PreparationUtil.getExternalStorageType(parent).out.firstOrNull() ?: ""
                    tags.add(type)
                    // Check the format
                    val supported = type.lowercase() in ConstantUtil.SupportedExternalStorageFormat
                    if (supported.not()) {
                        tags.add(context.getString(R.string.limited_4gb))
                        entity.error = "${context.getString(R.string.outdated_fs_warning)}\n\n" +
                                "${context.getString(R.string.recommend)}: ${ConstantUtil.SupportedExternalStorageFormat.toSpaceString()}"
                        entity.enabled = true
                    } else {
                        entity.error = ""
                        entity.enabled = true
                    }
                    entity.tags = tags
                    entity.type = type
                }

                directoryDao.upsert(entity)
            }

            val selectedDirectory = directoryDao.querySelectedByDirectoryType()
            if (selectedDirectory == null || (selectedDirectory.storageType == StorageType.EXTERNAL && selectedDirectory.enabled.not()) || selectedDirectory.active.not()) {
                resetDir()
            }
        }
    }

    suspend fun updateSelected() {
        withIOContext {
            directoryDao.querySelectedByDirectoryType()?.apply {
                val statFs = rootService.readStatFs(parent)
                childUsedBytes = rootService.calculateSize(path)
                availableBytes = statFs.availableBytes
                totalBytes = statFs.totalBytes
                directoryDao.upsert(this)
            }
        }
    }

    fun querySelectedByDirectoryTypeFlow() = directoryDao.querySelectedByDirectoryTypeFlow()
}
