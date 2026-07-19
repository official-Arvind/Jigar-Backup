package com.jigar.feature.setup

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import com.jigar.core.ui.component.AnimatedNavHost
import com.jigar.core.ui.util.LocalNavController
import com.jigar.feature.main.configurations.PageConfigurations
import com.jigar.feature.main.directory.PageDirectory
import com.jigar.feature.setup.page.one.PageOne
import com.jigar.feature.setup.page.two.PageTwo

@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun SetupGraph() {
    val navController = LocalNavController.current!!
    AnimatedNavHost(
        navController = navController,
        startDestination = SetupRoutes.One.route,
    ) {
        composable(SetupRoutes.One.route) {
            PageOne()
        }
        composable(SetupRoutes.Two.route) {
            PageTwo()
        }
        composable(SetupRoutes.Directory.route) {
            PageDirectory()
        }
        composable(SetupRoutes.Configurations.route) {
            PageConfigurations()
        }
    }
}

