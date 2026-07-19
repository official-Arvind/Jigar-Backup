package com.jigar.feature.main.processing.medium.backup

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import com.jigar.core.data.repository.CloudRepository
import com.jigar.core.data.repository.MediaRepository
import com.jigar.core.data.repository.TaskRepository
import com.jigar.core.datastore.saveCloudActivatedAccountName
import com.jigar.core.model.OpType
import com.jigar.core.model.StorageMode
import com.jigar.core.model.database.MediaEntity
import com.jigar.core.model.util.formatSize
import com.jigar.core.network.client.getCloud
import com.jigar.core.rootservice.service.RemoteRootService
import com.jigar.core.service.medium.backup.ProcessingServiceProxyCloudImpl
import com.jigar.core.service.medium.backup.ProcessingServiceProxyLocalImpl
import com.jigar.core.ui.material3.SnackbarDuration
import com.jigar.core.ui.material3.SnackbarType
import com.jigar.core.ui.model.DialogRadioItem
import com.jigar.core.ui.route.MainRoutes
import com.jigar.core.ui.viewmodel.IndexUiEffect
import com.jigar.core.util.navigateSingle
import com.jigar.feature.main.processing.AbstractMediumProcessingViewModel
import com.jigar.feature.main.processing.FinishSetup
import com.jigar.feature.main.processing.IndexUiState
import com.jigar.feature.main.processing.ProcessingUiIntent
import com.jigar.feature.main.processing.R
import com.jigar.feature.main.processing.SetCloudEntity
import com.jigar.feature.main.processing.UpdateFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@HiltViewModel
class BackupViewModelImpl @Inject constructor(
    @ApplicationContext private val mContext: Context,
    mRootService: RemoteRootService,
    mTaskRepo: TaskRepository,
    private val mMediaRepo: MediaRepository,
    private val mCloudRepo: CloudRepository,
    mLocalService: ProcessingServiceProxyLocalImpl,
    mCloudService: ProcessingServiceProxyCloudImpl,
) : AbstractMediumProcessingViewModel(mContext, mRootService, mTaskRepo, mLocalService, mCloudService) {
    override suspend fun onOtherEvent(state: IndexUiState, intent: ProcessingUiIntent) {
        when (intent) {
            is UpdateFiles -> {
                val medium = mMediaRepo.queryActivated(OpType.BACKUP)
                var bytes = 0.0
                medium.forEach {
                    bytes += it.displayStatsBytes
                }
                _medium.value = medium
                _mediumSize.value = bytes.formatSize()
            }

            is SetCloudEntity -> {
                mContext.saveCloudActivatedAccountName(intent.name)
                emitState(state.copy(cloudEntity = mCloudRepo.queryByName(intent.name)))
            }

            is FinishSetup -> {
                if (state.storageType == StorageMode.Cloud) {
                    _isTesting.value = true
                    emitEffect(IndexUiEffect.DismissSnackbar)
                    emitEffectOnIO(
                        IndexUiEffect.ShowSnackbar(
                            type = SnackbarType.Loading,
                            message = mCloudRepo.getString(R.string.processing),
                            duration = SnackbarDuration.Indefinite,
                        )
                    )
                    runCatching {
                        val client = state.cloudEntity!!.getCloud()
                        client.testConnection()
                        emitEffect(IndexUiEffect.DismissSnackbar)
                        withMainContext {
                            intent.navController.popBackStack()
                            intent.navController.navigateSingle(MainRoutes.MediumBackupProcessing.route)
                        }
                    }.onFailure {
                        emitEffect(IndexUiEffect.DismissSnackbar)
                        if (it.localizedMessage != null)
                            emitEffectOnIO(IndexUiEffect.ShowSnackbar(type = SnackbarType.Error, message = it.localizedMessage!!, duration = SnackbarDuration.Long))
                    }
                    _isTesting.value = false
                } else {
                    withMainContext {
                        intent.navController.popBackStack()
                        intent.navController.navigateSingle(MainRoutes.MediumBackupProcessing.route)
                    }
                }
            }

            else -> {

            }
        }
    }

    private val _accounts: Flow<List<DialogRadioItem<Any>>> = mCloudRepo.clouds.map { entities ->
        entities.map {
            DialogRadioItem(
                enum = Any(),
                title = it.name,
                desc = it.user,
            )
        }
    }.flowOnIO()
    private val _isTesting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _medium: MutableStateFlow<List<MediaEntity>> = MutableStateFlow(listOf())
    private val _mediumSize: MutableStateFlow<String> = MutableStateFlow("")

    val accounts: StateFlow<List<DialogRadioItem<Any>>> = _accounts.stateInScope(listOf())
    val isTesting: StateFlow<Boolean> = _isTesting.stateInScope(false)
    val medium: StateFlow<List<MediaEntity>> = _medium.stateInScope(listOf())
    val mediumSize: StateFlow<String> = _mediumSize.stateInScope("")
}