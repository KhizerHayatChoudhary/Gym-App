package com.example.emptyactivity.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

/**
 * This file sets up navigation and theme state for the entire app.
 * It creates a way for any screen to access the navigation controller
 * and theme state without having to pass it around manually.
 */

// This makes the navigation controller available to all screens in the app
// Think of it like a global variable that all screens can use to navigate
val LocalNavController = compositionLocalOf<NavHostController> {
    // If we try to use navigation but it's not set up, show this error
    error("No NavController found! Make sure MainActivity is set up correctly.")
}

// Type alias for theme state setter
typealias ThemeSetter = (Boolean) -> Unit

// This makes the theme state available to all screens in the app
// Pair of (isDarkTheme, setTheme) so screens can read and change the theme
val LocalThemeState = compositionLocalOf<Pair<Boolean, ThemeSetter>> {
    // If we try to use theme state but it's not set up, show this error
    error("No ThemeState found! Make sure MainActivity is set up correctly.")
}

