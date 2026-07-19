package com.jigar.feature.main.cloud.add

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jigar.core.model.CloudType
import com.jigar.core.ui.component.Clickable
import com.jigar.core.ui.component.Title
import com.jigar.core.ui.route.MainRoutes
import com.jigar.core.ui.token.SizeTokens
import com.jigar.core.ui.util.LocalNavController
import com.jigar.core.ui.util.icon
import com.jigar.core.util.encodedURLWithSpace
import com.jigar.core.util.navigateSingle
import com.jigar.feature.main.cloud.CloudScaffold
import com.jigar.feature.main.cloud.R

@ExperimentalLayoutApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun PageCloudAddAccount() {
    val navController = LocalNavController.current!!
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    CloudScaffold(
        scrollBehavior = scrollBehavior,
        title = stringResource(id = R.string.add_account),
        actions = {}
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SizeTokens.Level24)
        ) {
            Title(title = stringResource(id = R.string.provider)) {
                Clickable(
                    icon = CloudType.FTP.icon,
                    title = CloudType.FTP.title,
                ) {
                    navController.navigateSingle(MainRoutes.FTPSetup.getRoute(encodedURLWithSpace))
                }
                Clickable(
                    icon = CloudType.WEBDAV.icon,
                    title = CloudType.WEBDAV.title,
                ) {
                    navController.navigateSingle(MainRoutes.WebDAVSetup.getRoute(encodedURLWithSpace))
                }
                Clickable(
                    icon = CloudType.SMB.icon,
                    title = CloudType.SMB.title,
                ) {
                    navController.navigateSingle(MainRoutes.SMBSetup.getRoute(encodedURLWithSpace))
                }
                Clickable(
                    icon = CloudType.SFTP.icon,
                    title = CloudType.SFTP.title,
                ) {
                    navController.navigateSingle(MainRoutes.SFTPSetup.getRoute(encodedURLWithSpace))
                }
            }
        }
    }
}
