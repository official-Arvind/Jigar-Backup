package com.jigar.feature.main.processing

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.AbstractProcessingServiceProxy
import com.jigar.core.ui.model.ProcessingDataCardItem
import com.jigar.core.ui.util.toProcessingCardItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

data object UpdateApps : ProcessingUiIntent()
data class SetCloudEntity(val name: String) : ProcessingUiIntent()
data class FinishSetup(val navController: NavController) : ProcessingUiIntent()
data object GetUsers : ProcessingUiIntent()

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
abstract class AbstractPackagesProcessingViewModel(
    @ApplicationContext private val mContext: Context,
    mRootService: RemoteRootService,
    private val mTaskRepo: TaskRepository,
    mLocalService: AbstractProcessingServiceProxy,
    mCloudService: AbstractProcessingServiceProxy,
) : AbstractProcessingViewModel(mContext, mRootService, mTaskRepo, mLocalService, mCloudService) {
    override val _dataItems: Flow<List<ProcessingDataCardItem>> = _taskId.flatMapLatest { id ->
        mTaskRepo.queryPackageFlow(id)
            .map { packages ->
                val items = mutableListOf<ProcessingDataCardItem>()
                packages.map {
                    items.add(
                        ProcessingDataCardItem(
                            title = it.packageEntity.packageInfo.label,
                            state = it.state,
                            key = it.packageEntity.packageName,
                            processingIndex = it.processingIndex,
                            progress = (it.apkInfo.progress + it.userInfo.progress + it.userDeInfo.progress + it.dataInfo.progress + it.obbInfo.progress + it.mediaInfo.progress) / 6,
                            items = listOf(
                                it.apkInfo.toProcessingCardItem,
                                it.userInfo.toProcessingCardItem,
                                it.userDeInfo.toProcessingCardItem,
                                it.dataInfo.toProcessingCardItem,
                                it.obbInfo.toProcessingCardItem,
                                it.mediaInfo.toProcessingCardItem,
                            )
                        )
                    )
                }
                items
            }
            .flowOnIO()
    }
}