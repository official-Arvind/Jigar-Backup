package com.jigar.core.service.packages.restore

import com.jigar.core.data.repository.CloudRepository
import com.jigar.core.data.repository.PackageRepository
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.database.dao.PackageDao
import com.jigar.core.database.dao.TaskDao
import com.jigar.core.model.DataType
import com.jigar.core.model.OpType
import com.jigar.core.model.TaskType
import com.jigar.core.model.database.CloudEntity
import com.jigar.core.model.database.PackageEntity
import com.jigar.core.model.database.TaskDetailPackageEntity
import com.jigar.core.model.database.TaskEntity
import com.jigar.core.network.client.CloudClient
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.util.CommonBackupUtil
import com.jigar.core.service.util.PackagesRestoreUtil
import com.jigar.core.util.PathUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class RestoreServiceCloudImpl @Inject constructor() : AbstractRestoreService() {
    override val mTAG: String = "RestoreServiceCloudImpl"

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
        mCloudRepo.getClient().also { (c, e) ->
            mCloudEntity = e
            mClient = c
        }

        mRemotePath = mCloudEntity.remote
        mRemoteAppsDir = mPathUtil.getCloudRemoteAppsDir(mRemotePath)
        mRemoteConfigsDir = mPathUtil.getCloudRemoteConfigsDir(mRemotePath)
        mTaskEntity.update(cloud = mCloudEntity.name, backupDir = mRemotePath)

        return mPackageRepo.queryActivated(OpType.RESTORE, mCloudEntity.name, mCloudEntity.remote)
    }

    private fun getRemoteAppDir(archivesRelativeDir: String) = "${mRemoteAppsDir}/${archivesRelativeDir}"

    override suspend fun restore(type: DataType, userId: Int, p: PackageEntity, t: TaskDetailPackageEntity, srcDir: String) {
        val remoteAppDir = getRemoteAppDir(p.archivesRelativeDir)
        mPackagesRestoreUtil.download(client = mClient, p = p, t = t, dataType = type, srcDir = remoteAppDir, dstDir = srcDir) { mP, mT, _, mPath ->
            if (type == DataType.PACKAGE_APK) {
                mPackagesRestoreUtil.restoreApk(userId = userId, p = mP, t = mT, srcDir = mPath)
            } else {
                mPackagesRestoreUtil.restoreData(userId = userId, p = mP, t = mT, dataType = type, srcDir = mPath)
            }
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

    override val mRootDir by lazy { mPathUtil.getCloudTmpDir() }
    override val mAppsDir by lazy { mPathUtil.getCloudTmpAppsDir() }
    override val mConfigsDir by lazy { mPathUtil.getCloudTmpConfigsDir() }

    @Inject
    lateinit var mCloudRepo: CloudRepository

    private lateinit var mCloudEntity: CloudEntity
    private lateinit var mClient: CloudClient
    private lateinit var mRemotePath: String
    private lateinit var mRemoteAppsDir: String
    private lateinit var mRemoteConfigsDir: String
}
