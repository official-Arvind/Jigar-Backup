package com.jigar.feature.main.processing

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.jigar.core.ui.component.Divider
import com.jigar.core.ui.component.InnerBottomSpacer
import com.jigar.core.ui.component.InnerTopSpacer
import com.jigar.core.ui.component.LinearProgressIndicator
import com.jigar.core.ui.component.SecondaryTopBar
import com.jigar.core.ui.component.paddingBottom
import com.jigar.core.ui.material3.SnackbarHost
import com.jigar.core.ui.material3.SnackbarHostState
import com.jigar.core.ui.theme.ThemedColorSchemeKeyTokens
import com.jigar.core.ui.theme.value
import com.jigar.core.ui.token.AnimationTokens
import com.jigar.core.ui.token.SizeTokens

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun ProcessingSetupScaffold(
    scrollBehavior: TopAppBarScrollBehavior?,
    title: String,
    snackbarHostState: SnackbarHostState,
    onBackClick: (() -> Unit)? = null,
    progress: Float = -1f,
    actions: @Composable ColumnScope.() -> Unit = {},
    content: @Composable (BoxScope.(bottomPadding: Dp) -> Unit)
) {
    var bottomBarSize by remember { mutableStateOf(IntSize.Zero) }

    Scaffold(
        modifier = if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
        topBar = {
            Column {
                SecondaryTopBar(
                    scrollBehavior = scrollBehavior,
                    title = title,
                    onBackClick = onBackClick,
                )
                if (progress != -1f) {
                    var targetProgress by remember { mutableFloatStateOf(0f) }
                    val animatedProgress = animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = tween(),
                        label = AnimationTokens.AnimatedProgressLabel
                    )
                    targetProgress = if (progress.isNaN()) 0f else progress
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = animatedProgress.value)
                }
            }
        },
        snackbarHost = {
            with(LocalDensity.current) {
                SnackbarHost(
                    modifier = Modifier
                        .paddingBottom(bottomBarSize.height.toDp() + SizeTokens.Level24 + SizeTokens.Level4),
                    hostState = snackbarHostState,
                )
            }
        },
    ) { innerPadding ->
        Column {
            InnerTopSpacer(innerPadding = innerPadding)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                with(LocalDensity.current) {
                    content(bottomBarSize.height.toDp())
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .wrapContentSize()
                        .background(ThemedColorSchemeKeyTokens.SurfaceContainerLowest.value)
                        .onSizeChanged { bottomBarSize = it }
                ) {
                    Divider()
                    actions()
                    InnerBottomSpacer(innerPadding = innerPadding)
                }
            }
        }
    }
}
