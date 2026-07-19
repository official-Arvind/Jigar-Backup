package com.jigar.feature.main.processing

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
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

data object UpdateFiles : ProcessingUiIntent()

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
abstract class AbstractMediumProcessingViewModel(
    @ApplicationContext private val mContext: Context,
    mRootService: RemoteRootService,
    private val mTaskRepo: TaskRepository,
    mLocalService: AbstractProcessingServiceProxy,
    mCloudService: AbstractProcessingServiceProxy,
) : AbstractProcessingViewModel(mContext, mRootService, mTaskRepo, mLocalService, mCloudService) {
    override val _dataItems: Flow<List<ProcessingDataCardItem>> = _taskId.flatMapLatest { id ->
        mTaskRepo.queryMediaFlow(id)
            .map { medium ->
                val items = mutableListOf<ProcessingDataCardItem>()
                medium.map {
                    items.add(
                        ProcessingDataCardItem(
                            title = it.mediaEntity.name,
                            state = it.state,
                            key = it.mediaEntity.name,
                            processingIndex = it.processingIndex,
                            progress = it.mediaInfo.progress,
                            items = listOf(
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