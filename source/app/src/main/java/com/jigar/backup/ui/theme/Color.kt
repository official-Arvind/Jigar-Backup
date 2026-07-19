package com.jigar.backup.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jigar.backup.ui.theme.color.palettes.TonalPalette

val Purple80 = Color(0xFFa78bfa) // Violet light
val PurpleGrey80 = Color(0xFF06b6d4) // Cyan
val Pink80 = Color(0xFFf59e0b) // Orange

val Purple40 = Color(0xFF7c3aed) // Violet
val PurpleGrey40 = Color(0xFF06b6d4) // Cyan
val Pink40 = Color(0xFFf59e0b) // Orange

val GreenSource = Color(0xFF7c3aed)

fun Color.tone(tone: Int): Color {
    return Color(TonalPalette.fromInt(this.toArgb()).tone(tone))
}
