package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.local.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = ElegantDarkPrimary,
    onPrimary = ElegantDarkOnPrimary,
    primaryContainer = ElegantDarkPrimaryContainer,
    onPrimaryContainer = ElegantDarkOnPrimaryContainer,
    secondary = ElegantDarkSecondary,
    onSecondary = ElegantDarkOnSecondary,
    secondaryContainer = ElegantDarkSecondaryContainer,
    onSecondaryContainer = ElegantDarkOnSecondaryContainer,
    tertiary = ElegantDarkTertiary,
    onTertiary = ElegantDarkOnTertiary,
    tertiaryContainer = ElegantDarkTertiaryContainer,
    onTertiaryContainer = ElegantDarkOnTertiaryContainer,
    background = ElegantDarkBackground,
    onBackground = ElegantDarkOnBackground,
    surface = ElegantDarkSurface,
    onSurface = ElegantDarkOnSurface,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantDarkOnSurfaceVariant,
    outline = ElegantDarkOutline,
    outlineVariant = ElegantDarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = ElegantLightPrimary,
    onPrimary = ElegantLightOnPrimary,
    primaryContainer = ElegantLightPrimaryContainer,
    onPrimaryContainer = ElegantLightOnPrimaryContainer,
    secondary = ElegantLightSecondary,
    onSecondary = ElegantLightOnSecondary,
    secondaryContainer = ElegantLightSecondaryContainer,
    onSecondaryContainer = ElegantLightOnSecondaryContainer,
    tertiary = ElegantLightTertiary,
    onTertiary = ElegantLightOnTertiary,
    tertiaryContainer = ElegantLightTertiaryContainer,
    onTertiaryContainer = ElegantLightOnTertiaryContainer,
    background = ElegantLightBackground,
    onBackground = ElegantLightOnBackground,
    surface = ElegantLightSurface,
    onSurface = ElegantLightOnSurface,
    surfaceVariant = ElegantLightSurfaceVariant,
    onSurfaceVariant = ElegantLightOnSurfaceVariant,
    outline = ElegantLightOutline
)

@Composable
fun SmartCalcTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
