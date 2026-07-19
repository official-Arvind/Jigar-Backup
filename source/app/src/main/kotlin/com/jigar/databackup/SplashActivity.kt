package com.jigar.backup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.jigar.core.datastore.getCurrentAppVersionName
import com.jigar.core.datastore.readAppVersionName
import com.jigar.core.work.WorkManagerInitializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.jigar.feature.setup.MainActivity as SetupActivity

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    // Workaround for HarmonyOS
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()

        if (getCurrentAppVersionName() > runBlocking { readAppVersionName().first() }) {
            // There is an update
            startActivity(Intent(this, SetupActivity::class.java))
        } else {
            WorkManagerInitializer.fullInitialize(this)
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
