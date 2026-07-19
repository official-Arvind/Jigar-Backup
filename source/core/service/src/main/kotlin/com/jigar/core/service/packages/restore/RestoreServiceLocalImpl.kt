package com.jigar.core.service.packages.restore

import com.jigar.core.data.repository.PackageRepository
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.database.dao.PackageDao
import com.jigar.core.database.dao.TaskDao
import com.jigar.core.model.DataType
import com.jigar.core.model.OpType
import com.jigar.core.model.TaskType
import com.jigar.core.model.database.PackageEntity
import com.jigar.core.model.database.TaskDetailPackageEntity
import com.jigar.core.model.database.TaskEntity
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.util.CommonBackupUtil
import com.jigar.core.service.util.PackagesRestoreUtil
import com.jigar.core.util.PathUtil
import com.jigar.core.util.localBackupSaveDir
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class RestoreServiceLocalImpl @Inject constructor() : AbstractRestoreService() {
    override val mTAG: String = "RestoreServiceLocalImpl"

    @Inject
    override lateinit var mRootService: RemoteRootService

    @Inject
    override lateinit var mPathUtil: PathUtil

    @Inject
    override lateinit var mCommonBackupUtil: CommonBackupUtil

    @Inject
    override lateinit var mTaskDao: TaskDao

    @Inject
    override lateinit var mTaskRepo: TaskRepository

    override val mTaskEntity by lazy {
        TaskEntity(
            id = 0,
            opType = OpType.RESTORE,
            taskType = TaskType.PACKAGE,
            startTimestamp = mStartTimestamp,
            endTimestamp = mEndTimestamp,
            backupDir = mRootDir,
            isProcessing = true,
        )
    }

    override suspend fun getPackages(): List<PackageEntity> {
        return mPackageRepo.queryActivated(OpType.RESTORE, "", mRootDir)
    }

    override suspend fun restore(type: DataType, userId: Int, p: PackageEntity, t: TaskDetailPackageEntity, srcDir: String) {
        if (type == DataType.PACKAGE_APK) {
            mPackagesRestoreUtil.restoreApk(userId = userId, p = p, t = t, srcDir = srcDir)
        } else {
            mPackagesRestoreUtil.restoreData(userId = userId, p = p, t = t, dataType = type, srcDir = srcDir)
        }
        t.update(dataType = type, progress = 1f)
        t.update(processingIndex = t.processingIndex + 1)
    }

    @Inject
    override lateinit var mPackageDao: PackageDao

    @Inject
    override lateinit var mPackageRepo: PackageRepository

    @Inject
    override lateinit var mPackagesRestoreUtil: PackagesRestoreUtil

    override val mRootDir by lazy { mContext.localBackupSaveDir() }
    override val mAppsDir by lazy { mPathUtil.getLocalBackupAppsDir() }
    override val mConfigsDir by lazy { mPathUtil.getLocalBackupConfigsDir() }
}
