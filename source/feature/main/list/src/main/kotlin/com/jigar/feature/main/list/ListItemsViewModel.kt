package com.jigar.feature.main.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.core.data.repository.AppsRepo
import com.jigar.core.data.repository.FilesRepo
import com.jigar.core.data.repository.ListDataRepo
import com.jigar.core.model.App
import com.jigar.core.model.DataState
import com.jigar.core.model.File
import com.jigar.core.model.OpType
import com.jigar.core.model.Target
import com.jigar.core.model.database.PackageEntity
import com.jigar.core.model.util.of
import com.jigar.core.ui.route.MainRoutes
import com.jigar.core.util.decodeURL
import com.jigar.core.util.launchOnDefault
import com.jigar.feature.main.list.ListItemsUiState.Loading
import com.jigar.feature.main.list.ListItemsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    listDataRepo: ListDataRepo,
    private val appsRepo: AppsRepo,
    private val filesRepo: FilesRepo,
) : ViewModel() {
    private val target: Target = Target.valueOf(savedStateHandle.get<String>(MainRoutes.ARG_TARGET)!!.decodeURL().trim())
    private val opType: OpType = OpType.of(savedStateHandle.get<String>(MainRoutes.ARG_OP_TYPE)?.decodeURL()?.trim())

    val uiState: StateFlow<ListItemsUiState> = when (target) {
        Target.Apps -> listDataRepo.getAppList().map {
            Success.Apps(
                opType = opType,
                appList = it,
            )
        }


        Target.Files -> listDataRepo.getFileList().map {
            Success.Files(
                opType = opType,
                fileList = it,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun onSelectedChanged(id: Long, selected: Boolean) {
        viewModelScope.launchOnDefault {
            when (target) {
                Target.Apps -> appsRepo.selectApp(id, selected)
                Target.Files -> filesRepo.selectFile(id, selected)
            }
        }
    }

    fun onChangeFlag(id: Long, flag: Int) {
        viewModelScope.launchOnDefault {
            when (flag) {
                PackageEntity.FLAG_APK -> {
                    appsRepo.selectDataItems(
                        id = id,
                        apk = DataState.NotSelected,
                        user = DataState.Selected,
                        userDe = DataState.Selected,
                        data = DataState.Selected,
                        obb = DataState.Selected,
                        media = DataState.Selected,
                    )
                }

                PackageEntity.FLAG_ALL -> {
                    appsRepo.selectDataItems(
                        id = id,
                        apk = DataState.Selected,
                        user = DataState.NotSelected,
                        userDe = DataState.NotSelected,
                        data = DataState.NotSelected,
                        obb = DataState.NotSelected,
                        media = DataState.NotSelected,
                    )
                }

                else -> {
                    appsRepo.selectDataItems(
                        id = id,
                        apk = DataState.Selected,
                        user = DataState.Selected,
                        userDe = DataState.Selected,
                        data = DataState.Selected,
                        obb = DataState.Selected,
                        media = DataState.Selected,
                    )
                }
            }
        }
    }
}

sealed interface ListItemsUiState {
    data object Loading : ListItemsUiState
    sealed class Success(
        open val opType: OpType,
    ) : ListItemsUiState {
        data class Apps(
            override val opType: OpType,
            val appList: List<App>,
        ) : Success(opType)

        data class Files(
            override val opType: OpType,
            val fileList: List<File>,
        ) : Success(opType)
    }
}
