package com.smsforw.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF1976D2),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    secondary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFC8E6C9),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1B5E20),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFF9800),
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color(0xFFFAFDF7),
    surface = androidx.compose.ui.graphics.Color(0xFFFAFDF7),
    error = androidx.compose.ui.graphics.Color(0xFFB3261E),
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF1565C0),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
    secondary = androidx.compose.ui.graphics.Color(0xFFA5D6A7),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF1B5E20),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF2E7D32),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFC8E6C9),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFB74D),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFE65100),
    background = androidx.compose.ui.graphics.Color(0xFF1A1C19),
    surface = androidx.compose.ui.graphics.Color(0xFF1A1C19),
    error = androidx.compose.ui.graphics.Color(0xFFF2B8B5),
)

@Composable
fun SmsForwTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
