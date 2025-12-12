package com.example.emptyactivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.emptyactivity.ui.navigation.LocalNavController
import com.example.emptyactivity.ui.navigation.LocalThemeState
import com.example.emptyactivity.ui.navigation.MainLayout
import com.example.emptyactivity.ui.theme.EmptyActivityTheme

/**
 * MainActivity is the entry point of the Android app.
 * This is where the app starts when the user opens it.
 * 
 * It sets up:
 * - The app theme (colors, fonts, etc.)
 * - Navigation controller (so screens can navigate to each other)
 * - The main layout that holds all the screens
 */

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    
    /**
     * Called when the activity is first created.
     * This is where we set up the entire app UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set the content (UI) of this activity to our Compose UI
        setContent {
            // Remember theme preference (defaults to light mode)
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            
            // Apply the app theme (colors, typography, etc.)
            EmptyActivityTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                // Create a navigation controller that manages screen navigation
                val navController = rememberNavController()

                // Make the navigation controller and theme state available to all screens
                // This is like passing it down through all the components
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalThemeState provides Pair(isDarkTheme) { isDarkTheme = it }
                ) {
                    // Show the main layout which contains all screens and navigation
                    MainLayout()
                }
            }
        }
    }
}
