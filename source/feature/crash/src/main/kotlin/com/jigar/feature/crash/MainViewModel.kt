package com.jigar.feature.crash

import androidx.compose.material3.ExperimentalMaterial3Api
import com.jigar.core.ui.viewmodel.BaseViewModel
import com.jigar.core.ui.viewmodel.IndexUiEffect
import com.jigar.core.ui.viewmodel.UiIntent
import com.jigar.core.ui.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class IndexUiState(
    val text: String = "",
) : UiState

@ExperimentalMaterial3Api
@HiltViewModel
class IndexViewModel @Inject constructor() : BaseViewModel<IndexUiState, UiIntent, IndexUiEffect>(IndexUiState()) {
    override suspend fun onEvent(state: IndexUiState, intent: UiIntent) {}
}
