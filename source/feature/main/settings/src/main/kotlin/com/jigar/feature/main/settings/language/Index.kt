package com.jigar.feature.main.settings.language

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.core.common.util.BuildConfigUtil
import com.jigar.core.datastore.ConstantUtil
import com.jigar.core.ui.component.Clickable
import com.jigar.core.ui.util.LocalNavController
import com.jigar.core.util.LanguageUtil.toLocale
import com.jigar.feature.main.settings.R
import com.jigar.feature.main.settings.SettingsScaffold

@ExperimentalFoundationApi
@ExperimentalLayoutApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun PageLanguageSelector() {
    val context = LocalContext.current
    val navController = LocalNavController.current!!
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val viewModel = hiltViewModel<IndexViewModel>()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    SettingsScaffold(
        scrollBehavior = scrollBehavior,
        title = stringResource(id = R.string.language),
    ) {
        val sortedLocales = remember { BuildConfigUtil.SUPPORTED_LOCALES }
        LazyColumn {
            items(count = sortedLocales.size + 1) {
                if (it == 0) {
                    Clickable(
                        enabled = selectedLanguage != ConstantUtil.LANGUAGE_SYSTEM,
                        title = stringResource(id = R.string.system),
                        onClick = { viewModel.emitIntentOnIO(IndexUiIntent.UpdateLanguage(navController, ConstantUtil.LANGUAGE_SYSTEM)) },
                    )
                } else {
                    val item = sortedLocales[it - 1]
                    val locale by remember(item) { mutableStateOf(item.toLocale(context)) }
                    Clickable(
                        enabled = selectedLanguage != item,
                        title = locale.getDisplayName(locale),
                        onClick = { viewModel.emitIntentOnIO(IndexUiIntent.UpdateLanguage(navController, item)) },
                    )
                }
            }
        }
    }
}
