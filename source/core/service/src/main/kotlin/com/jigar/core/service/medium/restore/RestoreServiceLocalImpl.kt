package com.jigar.core.service.medium.restore

import com.jigar.core.data.repository.MediaRepository
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.database.dao.MediaDao
import com.jigar.core.database.dao.TaskDao
import com.jigar.core.model.OpType
import com.jigar.core.model.OperationState
import com.jigar.core.model.TaskType
import com.jigar.core.model.database.MediaEntity
import com.jigar.core.model.database.TaskDetailMediaEntity
import com.jigar.core.model.database.TaskEntity
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.util.CommonBackupUtil
import com.jigar.core.service.util.MediumRestoreUtil
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
            taskType = TaskType.MEDIA,
            startTimestamp = mStartTimestamp,
            endTimestamp = mEndTimestamp,
            backupDir = mRootDir,
            isProcessing = true,
        )
    }

    override suspend fun getMedium(): List<MediaEntity> {
        return mMediaRepo.queryActivated(OpType.RESTORE, "", mRootDir)
    }

    override suspend fun restore(m: MediaEntity, t: TaskDetailMediaEntity, srcDir: String) {
        if (m.path.isEmpty()) {
            t.update(state = OperationState.ERROR, log = "Path is empty.")
        } else {
            mMediumRestoreUtil.restoreMedia(m = m, t = t, srcDir = srcDir)
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

    override val mRootDir by lazy { mContext.localBackupSaveDir() }
    override val mFilesDir by lazy { mPathUtil.getLocalBackupFilesDir() }
    override val mConfigsDir by lazy { mPathUtil.getLocalBackupConfigsDir() }
}
