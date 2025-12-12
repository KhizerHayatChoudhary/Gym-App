package com.example.emptyactivity.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * This file sets up the app's theme (colors, typography, etc.).
 * It handles light/dark mode and dynamic colors (Android 12+).
 */

// Dark theme color scheme (colors for dark mode)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,       // Main color in dark mode
    secondary = PurpleGrey80, // Secondary color in dark mode
    tertiary = Pink80         // Tertiary/accent color in dark mode
)

// Light theme color scheme (colors for light mode)
private val LightColorScheme = lightColorScheme(
    primary = Purple40,       // Main color in light mode
    secondary = PurpleGrey40, // Secondary color in light mode
    tertiary = Pink40         // Tertiary/accent color in light mode

    /* Other default colors you can customize:
    background = Color(0xFFFFFBFE),  // Background color
    surface = Color(0xFFFFFBFE),     // Surface color (cards, etc.)
    onPrimary = Color.White,         // Text color on primary colored background
    onSecondary = Color.White,       // Text color on secondary colored background
    onTertiary = Color.White,        // Text color on tertiary colored background
    onBackground = Color(0xFF1C1B1F), // Text color on background
    onSurface = Color(0xFF1C1B1F),   // Text color on surface
    */
)

/**
 * Main theme composable that wraps the entire app.
 * 
 * It determines whether to use light or dark theme, and whether to use dynamic colors
 * (Android 12+ feature that matches system colors).
 * 
 * @param darkTheme Whether to use dark theme (usually based on system setting)
 * @param dynamicColor Whether to use dynamic colors (Android 12+ only)
 * @param content The content to apply the theme to (the entire app UI)
 */
@Composable
fun EmptyActivityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Automatically detect system theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Choose which color scheme to use
    val colorScheme = when {
        // If dynamic colors are enabled and device supports it (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Use system colors (matches user's wallpaper theme)
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Use custom dark theme
        darkTheme -> DarkColorScheme
        // Use custom light theme
        else -> LightColorScheme
    }
    
    // Get the current view to update system UI (status bar, etc.)
    val view = LocalView.current
    
    // Update the status bar color to match the theme
    if (!view.isInEditMode) {  // Don't run this in Android Studio preview
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()  // Match status bar to theme
            // Make status bar icons light or dark based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    // Apply the theme to all child components
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // Use our custom typography (see Type.kt)
        content = content
    )
}
