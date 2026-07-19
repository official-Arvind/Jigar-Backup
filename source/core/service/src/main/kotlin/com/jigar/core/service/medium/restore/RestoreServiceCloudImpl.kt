package com.jigar.core.service.medium.restore

import com.jigar.core.data.repository.CloudRepository
import com.jigar.core.data.repository.MediaRepository
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.database.dao.MediaDao
import com.jigar.core.database.dao.TaskDao
import com.jigar.core.model.DataType
import com.jigar.core.model.OpType
import com.jigar.core.model.OperationState
import com.jigar.core.model.TaskType
import com.jigar.core.model.database.CloudEntity
import com.jigar.core.model.database.MediaEntity
import com.jigar.core.model.database.TaskDetailMediaEntity
import com.jigar.core.model.database.TaskEntity
import com.jigar.core.network.client.CloudClient
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.util.CommonBackupUtil
import com.jigar.core.service.util.MediumRestoreUtil
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
            taskType = TaskType.MEDIA,
            startTimestamp = mStartTimestamp,
            endTimestamp = mEndTimestamp,
            backupDir = mRootDir,
            isProcessing = true,
        )
    }

    override suspend fun getMedium(): List<MediaEntity> {
        mCloudRepo.getClient().also { (c, e) ->
            mCloudEntity = e
            mClient = c
        }

        mRemotePath = mCloudEntity.remote
        mRemoteFilesDir = mPathUtil.getCloudRemoteFilesDir(mRemotePath)
        mTaskEntity.update(cloud = mCloudEntity.name, backupDir = mRemotePath)

        return mMediaRepo.queryActivated(OpType.RESTORE, mCloudEntity.name, mCloudEntity.remote)
    }

    private fun getRemoteFileDir(archivesRelativeDir: String) = "${mRemoteFilesDir}/${archivesRelativeDir}"

    override suspend fun restore(m: MediaEntity, t: TaskDetailMediaEntity, srcDir: String) {
        val remoteFileDir = getRemoteFileDir(m.archivesRelativeDir)

        if (m.path.isEmpty()) {
            t.update(state = OperationState.ERROR, log = "Path is empty.")
        } else {
            mMediumRestoreUtil.download(client = mClient, m = m, t = t, dataType = DataType.PACKAGE_MEDIA, srcDir = remoteFileDir, dstDir = srcDir) { mM, mT, mPath ->
                mMediumRestoreUtil.restoreMedia(m = mM, t = mT, srcDir = mPath)
            }
        }

        t.update(progress = 1f)
        t.update(processingIndex = t.processingIndex + 1)
    }

    @Inject
    override lateinit var mMediaDao: MediaDao

    @Inject
    override lateinit var mMediaRepo: MediaRepository

    @Inject
    override lateinit var mMediumRestoreUtil: MediumRestoreUtil

    override val mRootDir by lazy { mPathUtil.getCloudTmpDir() }
    override val mFilesDir by lazy { mPathUtil.getCloudTmpFilesDir() }
    override val mConfigsDir by lazy { mPathUtil.getCloudTmpConfigsDir() }

    @Inject
    lateinit var mCloudRepo: CloudRepository

    private lateinit var mCloudEntity: CloudEntity
    private lateinit var mClient: CloudClient
    private lateinit var mRemotePath: String
    private lateinit var mRemoteFilesDir: String
}
