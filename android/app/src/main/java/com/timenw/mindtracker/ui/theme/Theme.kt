package com.timenw.mindtracker.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val MindTeal = Color(0xFF4DB6AC)
val MindDark = Color(0xFF1B3A4B)
val MindPurple = Color(0xFF7E88B7)
val MindBlue = Color(0xFF5C9EAD)
val MindLight = Color(0xFFB2DFDB)
val MindGold = Color(0xFFD4A574)
val MindCream = Color(0xFFE0F2F1)
val MindSafe = Color(0xFF4CAF50)
val MindWarning = Color(0xFFFF9800)
val MindDanger = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = MindTeal, onPrimary = Color.White, primaryContainer = MindBlue, onPrimaryContainer = MindCream,
    secondary = MindPurple, onSecondary = MindCream, secondaryContainer = MindDark, onSecondaryContainer = MindLight,
    background = Color(0xFF0D1B21), onBackground = MindCream, surface = Color(0xFF162530), onSurface = MindCream,
    surfaceVariant = Color(0xFF1B3A4B), onSurfaceVariant = MindLight, error = MindDanger, outline = MindPurple
)

@Composable
fun MindTrackerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { val window = (view.context as Activity).window; window.statusBarColor = DarkColorScheme.background.toArgb(); WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false }
    }
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography(), content = content)
}
