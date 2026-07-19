package com.jigar.backup.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.jigar.backup.ui.theme.color.hct.Hct
import com.jigar.backup.ui.theme.color.scheme.SchemeContent

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun JigarBackupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val materialColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colorScheme = materialColorScheme.copy(
        background = Color(0xFF080B14),
        surface = Color(0xFF080B14),
        surfaceContainer = materialColorScheme.surfaceBright,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Immutable
class CustomColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val surfaceContainer: Color,
)

private val GreenDarkColorScheme = SchemeContent(
    sourceColorHct = Hct.fromInt(GreenSource.toArgb()),
    isDark = true,
    contrastLevel = 0.0,
).let { scheme ->
    CustomColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        surfaceContainer = Color(scheme.surfaceContainer),
    )
}

private val GreenLightColorScheme = SchemeContent(
    sourceColorHct = Hct.fromInt(GreenSource.toArgb()),
    isDark = false,
    contrastLevel = 0.0,
).let { scheme ->
    CustomColorScheme(
        primary = Color(scheme.primary),
        onPrimary = Color(scheme.onPrimary),
        primaryContainer = Color(scheme.primaryContainer),
        onPrimaryContainer = Color(scheme.onPrimaryContainer),
        surfaceContainer = Color(scheme.surfaceContainer),
    )
}

object JigarBackupTheme {
    val greenColorScheme: CustomColorScheme
        @Composable @ReadOnlyComposable get() = if (isSystemInDarkTheme()) {
            GreenDarkColorScheme
        } else {
            GreenLightColorScheme
        }
}
