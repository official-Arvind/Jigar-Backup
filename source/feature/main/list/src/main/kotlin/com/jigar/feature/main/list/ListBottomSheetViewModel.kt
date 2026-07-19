package com.jigar.feature.main.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.core.data.repository.AppsRepo
import com.jigar.core.data.repository.CloudRepository
import com.jigar.core.data.repository.Filters
import com.jigar.core.data.repository.LabelsRepo
import com.jigar.core.data.repository.ListData
import com.jigar.core.data.repository.ListDataRepo
import com.jigar.core.hiddenapi.castTo
import com.jigar.core.model.App
import com.jigar.core.model.File
import com.jigar.core.model.OpType
import com.jigar.core.model.SortType
import com.jigar.core.model.Target
import com.jigar.core.model.database.CloudEntity
import com.jigar.core.model.database.LabelEntity
import com.jigar.core.model.database.PackageDataStates
import com.jigar.core.model.util.of
import com.jigar.core.ui.route.MainRoutes
import com.jigar.core.util.decodeURL
import com.jigar.core.util.launchOnDefault
import com.jigar.feature.main.list.ListBottomSheetUiState.Loading
import com.jigar.feature.main.list.ListBottomSheetUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listDataRepo: ListDataRepo,
    private val appsRepo: AppsRepo,
    cloudRepo: CloudRepository,
    labelsRepo: LabelsRepo
) : ViewModel() {
    private val target: Target = Target.valueOf(savedStateHandle.get<String>(MainRoutes.ARG_TARGET)!!.decodeURL().trim())
    private val opType: OpType = OpType.of(savedStateHandle.get<String>(MainRoutes.ARG_OP_TYPE)?.decodeURL()?.trim())

    val uiState: StateFlow<ListBottomSheetUiState> = when (target) {
        Target.Apps -> combine(
            listDataRepo.getListData(),
            listDataRepo.getAppList(),
            labelsRepo.getLabelsFlow(),
            cloudRepo.clouds,
        ) { lData, aList, labels, clouds ->
            val listData = lData.castTo<ListData.Apps>()
            Success.Apps(
                opType = opType,
                showFilterSheet = listData.showFilterSheet,
                sortIndex = listData.sortIndex,
                sortType = listData.sortType,
                labelEntities = labels,
                labels = listData.labels,
                showDataItemsSheet = listData.showDataItemsSheet,
                filters = listData.filters,
                appList = aList,
                clouds = clouds
            )
        }

        Target.Files -> combine(
            listDataRepo.getListData(),
            listDataRepo.getFileList(),
            labelsRepo.getLabelsFlow()
        ) { lData, fList, labels ->
            val listData = lData.castTo<ListData.Files>()
            Success.Files(
                opType = opType,
                showFilterSheet = listData.showFilterSheet,
                sortIndex = listData.sortIndex,
                sortType = listData.sortType,
                labelEntities = labels,
                labels = listData.labels,
                fileList = fList,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun setShowFilterSheet(value: Boolean) {
        viewModelScope.launchOnDefault {
            listDataRepo.setShowFilterSheet(value)
        }
    }

    fun setShowDataItemsSheet(value: Boolean) {
        viewModelScope.launchOnDefault {
            listDataRepo.setShowDataItemsSheet(value)
        }
    }

    fun setFilters(filters: Filters) {
        viewModelScope.launchOnDefault {
            if (uiState.value is Success.Apps) {
                val isShow = filters.showSystemApps
                listDataRepo.setFilters { filters }
                val state = uiState.value.castTo<Success.Apps>()
                if (isShow.not()) {
                    appsRepo.unselectAll(state.appList.filter { it.isSystemApp }.map { it.id })
                }
            }
        }
    }

    fun setSortByType() {
        viewModelScope.launchOnDefault {
            listDataRepo.setSortType { if (it == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING }
        }
    }

    fun setSortByIndex(index: Int) {
        viewModelScope.launchOnDefault {
            listDataRepo.setSortIndex { index }
        }
    }

    fun addOrRemoveLabel(label: String) {
        viewModelScope.launchOnDefault {
            if (uiState.value is Success) {
                val state = uiState.value.castTo<Success>()
                if (label in state.labels) {
                    listDataRepo.removeLabel(label)
                } else {
                    listDataRepo.addLabel(label)
                }
            }
        }
    }

    fun setDataItems(selections: PackageDataStates) {
        viewModelScope.launchOnDefault {
            if (uiState.value is Success.Apps) {
                val state = uiState.value.castTo<Success.Apps>()
                appsRepo.setDataItems(state.appList.filter { it.selected }.map { it.id }, selections)
            }
        }
    }
}

sealed interface ListBottomSheetUiState {
    data object Loading : ListBottomSheetUiState
    sealed class Success(
        open val opType: OpType,
        open val showFilterSheet: Boolean,
        open val sortIndex: Int,
        open val sortType: SortType,
        open val labelEntities: List<LabelEntity>,
        open val labels: Set<String>,
    ) : ListBottomSheetUiState {
        data class Apps(
            override val opType: OpType,
            override val showFilterSheet: Boolean,
            override val sortIndex: Int,
            override val sortType: SortType,
            override val labelEntities: List<LabelEntity>,
            override val labels: Set<String>,
            val showDataItemsSheet: Boolean,
            val filters: Filters,
            val appList: List<App>,
            val clouds: List<CloudEntity>,
        ) : Success(opType, showFilterSheet, sortIndex, sortType, labelEntities, labels)

        data class Files(
            override val opType: OpType,
            override val showFilterSheet: Boolean,
            override val sortIndex: Int,
            override val sortType: SortType,
            override val labelEntities: List<LabelEntity>,
            override val labels: Set<String>,
            val fileList: List<File>,
        ) : Success(opType, showFilterSheet, sortIndex, sortType, labelEntities, labels)
    }
}
