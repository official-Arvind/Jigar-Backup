package com.jigar.core.data.repository

import android.content.Context
import com.jigar.core.data.R
import com.jigar.core.database.dao.PackageDao
import com.jigar.core.datastore.di.DbDispatchers.Default
import com.jigar.core.datastore.di.Dispatcher
import com.jigar.core.model.OpType
import com.jigar.core.model.UserInfo
import com.jigar.core.rootservice.service.RemoteRootService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsersRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    private val rootService: RemoteRootService,
    private val appsDao: PackageDao,
) {
    fun getUsers(opType: OpType): Flow<List<UserInfo>> = when (opType) {
        OpType.BACKUP -> flow {
            emit(
                rootService.getUsers().map { UserInfo(it.id, it.name) }
            )
        }.flowOn(defaultDispatcher)

        OpType.RESTORE -> appsDao.queryUserIdsFlow(opType).map { it.map { u -> UserInfo(u, context.getString(R.string.user)) } }
    }

    fun getUsersMap(opType: OpType, cloud: String, backupDir: String): Flow<Map<Int, Long>> = appsDao.countUsersMapFlow(opType = opType, blocked = false, cloud = cloud, backupDir = backupDir)
}
