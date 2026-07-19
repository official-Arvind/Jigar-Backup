package com.jigar.backup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jigar.core.ui.component.AnimatedNavHost
import com.jigar.core.ui.route.MainRoutes
import com.jigar.core.ui.theme.JigarBackupTheme
import com.jigar.core.ui.util.LocalNavController
import com.jigar.core.util.command.BaseUtil
import com.jigar.feature.main.cloud.PageCloud
import com.jigar.feature.main.cloud.add.PageCloudAddAccount
import com.jigar.feature.main.cloud.add.PageFTPSetup
import com.jigar.feature.main.cloud.add.PageSFTPSetup
import com.jigar.feature.main.cloud.add.PageSMBSetup
import com.jigar.feature.main.cloud.add.PageWebDAVSetup
import com.jigar.feature.main.configurations.PageConfigurations
import com.jigar.feature.main.dashboard.PageDashboard
import com.jigar.feature.main.details.DetailsRoute
import com.jigar.feature.main.directory.PageDirectory
import com.jigar.feature.main.history.HistoryRoute
import com.jigar.feature.main.history.TaskDetailsRoute
import com.jigar.feature.main.list.ListRoute
import com.jigar.feature.main.processing.medium.backup.MediumBackupProcessingGraph
import com.jigar.feature.main.processing.medium.restore.MediumRestoreProcessingGraph
import com.jigar.feature.main.processing.packages.backup.PackagesBackupProcessingGraph
import com.jigar.feature.main.processing.packages.restore.PackagesRestoreProcessingGraph
import com.jigar.feature.main.restore.PageRestore
import com.jigar.feature.main.restore.reload.PageReload
import com.jigar.feature.main.settings.PageSettings
import com.jigar.feature.main.settings.about.PageAboutSettings
import com.jigar.feature.main.settings.about.PageTranslatorsSettings
import com.jigar.feature.main.settings.backup.PageBackupSettings
import com.jigar.feature.main.settings.blacklist.PageBlackList
import com.jigar.feature.main.settings.language.PageLanguageSelector
import com.jigar.feature.main.settings.restore.PageRestoreSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @ExperimentalCoroutinesApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalLayoutApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        runBlocking {
            runCatching {
                BaseUtil.initializeEnvironment(context = this@MainActivity)
            }
        }

        setContent {
            JigarBackupTheme {
                val navController = rememberNavController()
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current,
                ) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = MainRoutes.Dashboard.route,
                    ) {
                        composable(MainRoutes.Dashboard.route) {
                            PageDashboard()
                        }
                        composable(MainRoutes.Cloud.route) {
                            PageCloud()
                        }
                        composable(MainRoutes.CloudAddAccount.route) {
                            PageCloudAddAccount()
                        }
                        composable(MainRoutes.FTPSetup.route) {
                            PageFTPSetup()
                        }
                        composable(MainRoutes.WebDAVSetup.route) {
                            PageWebDAVSetup()
                        }
                        composable(MainRoutes.SMBSetup.route) {
                            PageSMBSetup()
                        }
                        composable(MainRoutes.SFTPSetup.route) {
                            PageSFTPSetup()
                        }
                        composable(MainRoutes.List.route) {
                            ListRoute()
                        }
                        composable(MainRoutes.Details.route) {
                            DetailsRoute()
                        }
                        composable(MainRoutes.History.route) {
                            HistoryRoute()
                        }
                        composable(MainRoutes.TaskDetails.route) {
                            TaskDetailsRoute()
                        }
                        composable(MainRoutes.PackagesBackupProcessingGraph.route) {
                            PackagesBackupProcessingGraph()
                        }
                        composable(MainRoutes.PackagesRestoreProcessingGraph.route) {
                            PackagesRestoreProcessingGraph()
                        }
                        composable(MainRoutes.MediumBackupProcessingGraph.route) {
                            MediumBackupProcessingGraph()
                        }
                        composable(MainRoutes.MediumRestoreProcessingGraph.route) {
                            MediumRestoreProcessingGraph()
                        }
                        composable(MainRoutes.Settings.route) {
                            PageSettings()
                        }
                        composable(MainRoutes.Restore.route) {
                            PageRestore()
                        }
                        composable(MainRoutes.Reload.route) {
                            PageReload()
                        }
                        composable(MainRoutes.BackupSettings.route) {
                            PageBackupSettings()
                        }
                        composable(MainRoutes.RestoreSettings.route) {
                            PageRestoreSettings()
                        }
                        composable(MainRoutes.LanguageSettings.route) {
                            PageLanguageSelector()
                        }
                        composable(MainRoutes.BlackList.route) {
                            PageBlackList()
                        }
                        composable(MainRoutes.Configurations.route) {
                            PageConfigurations()
                        }
                        composable(MainRoutes.About.route) {
                            PageAboutSettings()
                        }
                        composable(MainRoutes.Translators.route) {
                            PageTranslatorsSettings()
                        }
                        composable(route = MainRoutes.Directory.route) {
                            PageDirectory()
                        }
                    }
                }
            }
        }
    }
}
