package com.jigar.feature.setup.page.two

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.jigar.core.datastore.saveAppVersionName
import com.jigar.core.ui.viewmodel.BaseViewModel
import com.jigar.core.ui.viewmodel.IndexUiEffect
import com.jigar.core.ui.viewmodel.UiIntent
import com.jigar.core.ui.viewmodel.UiState
import com.jigar.core.util.ActivityUtil
import com.jigar.core.work.WorkManagerInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data object IndexUiState : UiState

sealed class IndexUiIntent : UiIntent {
    data class ToMain(val context: Activity) : IndexUiIntent()
}

@ExperimentalMaterial3Api
@HiltViewModel
class IndexViewModel @Inject constructor() : BaseViewModel<IndexUiState, IndexUiIntent, IndexUiEffect>(IndexUiState) {
    override suspend fun onEvent(state: IndexUiState, intent: IndexUiIntent) {
        when (intent) {
            is IndexUiIntent.ToMain -> {
                val context = intent.context
                WorkManagerInitializer.fullInitialize(context)
                context.saveAppVersionName()
                context.startActivity(Intent(context, ActivityUtil.classMainActivity))
                context.finish()
            }
        }
    }
}
