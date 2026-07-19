package com.jigar.feature.main.history

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.core.model.DataType
import com.jigar.core.model.database.ProcessingInfoEntity
import com.jigar.core.model.database.TaskDetailMediaEntity
import com.jigar.core.model.database.TaskDetailPackageEntity
import com.jigar.core.model.util.get
import com.jigar.core.ui.component.Clickable
import com.jigar.core.ui.component.InnerBottomSpacer
import com.jigar.core.ui.component.InnerTopSpacer
import com.jigar.core.ui.component.PackageIconImage
import com.jigar.core.ui.component.SecondaryLargeTopBar
import com.jigar.core.ui.component.Title
import com.jigar.core.ui.token.AnimationTokens
import com.jigar.core.ui.token.SizeTokens
import com.jigar.core.ui.util.LocalNavController
import com.jigar.core.ui.util.StateView
import com.jigar.core.util.maybePopBackStack

@Composable
fun TaskDetailsRoute(
    viewModel: TaskDetailsViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current!!
    val uiState: TaskDetailsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    TaskDetailsScreen(uiState)
    LaunchedEffect(uiState) {
        if (uiState is TaskDetailsUiState.Error) {
            navController.maybePopBackStack()
        }
    }
}

@SuppressLint("StringFormatInvalid")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskDetailsScreen(uiState: TaskDetailsUiState) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SecondaryLargeTopBar(
                scrollBehavior = scrollBehavior,
                title = stringResource(R.string.task_details),
            )
        }
    ) { innerPadding ->
        AnimatedContent(uiState, label = AnimationTokens.AnimatedContentLabel) { state ->
            when (state) {
                is TaskDetailsUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            InnerTopSpacer(innerPadding = innerPadding)
                        }

                        item {
                            Title(title = stringResource(R.string.preprocessing)) {
                                state.preprocessingInfoList.forEach {
                                    it.ProcessingItem()
                                }
                            }
                        }

                        items(items = state.appInfoList) {
                            Title(title = it.packageEntity.packageInfo.label.ifEmpty { it.packageEntity.packageName }) {
                                it.AppDataItem(DataType.PACKAGE_APK)
                                it.AppDataItem(DataType.PACKAGE_USER)
                                it.AppDataItem(DataType.PACKAGE_USER_DE)
                                it.AppDataItem(DataType.PACKAGE_DATA)
                                it.AppDataItem(DataType.PACKAGE_OBB)
                                it.AppDataItem(DataType.PACKAGE_MEDIA)
                            }
                        }

                        items(items = state.fileInfoList) {
                            Title(title = it.mediaEntity.name.ifEmpty { it.mediaEntity.path }) {
                                it.FileDataItem(DataType.MEDIA_MEDIA)
                            }
                        }

                        item {
                            Title(title = stringResource(R.string.post_processing)) {
                                state.postProcessingInfoList.forEach {
                                    it.ProcessingItem()
                                }
                            }
                        }

                        item {
                            InnerBottomSpacer(innerPadding = innerPadding)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun TaskDetailPackageEntity.AppDataItem(dataType: DataType) {
    val info by remember(this, dataType) {
        mutableStateOf(get(dataType))
    }
    Clickable(
        title = dataType.type.uppercase(),
        value = info.log.ifEmpty { null },
        leadingIcon = {
            PackageIconImage(packageName = packageEntity.packageName, size = SizeTokens.Level32)
        },
        trailingIcon = {
            info.state.StateView()
        }
    ) {
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun TaskDetailMediaEntity.FileDataItem(dataType: DataType) {
    Clickable(
        title = dataType.type.uppercase(),
        value = mediaInfo.log.ifEmpty { null },
        leadingIcon = {
            PackageIconImage(icon = Icons.Rounded.Folder, packageName = "", inCircleShape = true, size = SizeTokens.Level32)
        },
        trailingIcon = {
            mediaInfo.state.StateView()
        }
    ) {
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ProcessingInfoEntity.ProcessingItem() {
    Clickable(
        title = title,
        value = log.ifEmpty { null },
        leadingIcon = {
            PackageIconImage(icon = ImageVector.vectorResource(com.jigar.core.ui.R.drawable.ic_rounded_hourglass_empty), packageName = "", inCircleShape = true, size = SizeTokens.Level32)
        },
        trailingIcon = {
            state.StateView()
        }
    ) {
    }
}
