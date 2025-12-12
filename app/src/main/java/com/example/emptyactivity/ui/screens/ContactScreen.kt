package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.ui.navigation.LocalNavController

/**
 * This screen shows contact information for user support.
 * Users can find help or contact the app developers from here.
 */

/**
 * Contact screen component.
 * Displays support email and navigation buttons.
 */
@Composable
fun ContactScreen() {
    // Get navigation controller to navigate to other screens
    val navController = LocalNavController.current

    // Main column layout (centered)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Screen title with envelope emoji
        Text(
            text = "Contact Us 📬",
            textAlign = TextAlign.Center
        )

        // Contact information
        Text(
            text = "Have questions or feedback? You can reach us at:\n\nsupport@trainr.com",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Button to go back to Home screen
        Button(onClick = { navController.navigate("MainScreenRoute") }) {
            Text("Go to Home")
        }

        // Button to go to About screen
        Button(
            onClick = { navController.navigate("AboutScreenRoute") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("About")
        }

        // Back button - only enabled if there's a previous screen
        Button(
            onClick = { navController.popBackStack() },
            enabled = navController.previousBackStackEntry != null,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Back")
        }
    }
}

