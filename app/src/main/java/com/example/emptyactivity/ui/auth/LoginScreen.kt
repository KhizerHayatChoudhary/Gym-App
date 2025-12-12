package com.example.emptyactivity.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.ui.navigation.LocalNavController
import kotlinx.coroutines.launch

/**
 * This screen allows users to log in to their account.
 * Users enter their username and password to access the app.
 */

/**
 * Login screen component.
 * Allows users to enter username and password to log in.
 *
 * @param onLoginSuccess Callback function called when login is successful
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    // Get the navigation controller so we can navigate to register screen
    val navController = LocalNavController.current

    // Get the database to check user credentials
    val context = LocalContext.current
    val database = WorkoutDatabase.getDatabase(context)
    val userDao = database.userDao()

    // Coroutine scope for running database operations (which are async)
    val scope = rememberCoroutineScope()

    // State variables for the input fields
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // State for error message
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // State for showing loading indicator
    val isLoading = remember { mutableStateOf(false) }

    // Main column layout (centered, scrollable) with theme-aware background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App title with better styling
        Text(
            text = "Trainr",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Card container for input fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Username input field
                OutlinedTextField(
                    value = username.value,
                    onValueChange = {
                        username.value = it
                        errorMessage.value = null  // Clear error when user types
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input field
                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        errorMessage.value = null  // Clear error when user types
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),  // Hides password as dots
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
        }

        // Error message (only shows if there's an error)
        if (errorMessage.value != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage.value!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = {
                // Check if fields are filled
                if (username.value.isBlank() || password.value.isBlank()) {
                    errorMessage.value = "Please fill in all fields"
                    return@Button
                }

                // Start loading
                isLoading.value = true
                errorMessage.value = null

                // Try to log in (database operation must run in coroutine)
                scope.launch {
                    try {
                        // Find user by username
                        val user = userDao.findByUsername(username.value)

                        if (user == null) {
                            // User doesn't exist
                            errorMessage.value = "Username not found"
                            isLoading.value = false
                        } else if (user.password != password.value) {
                            // Wrong password
                            errorMessage.value = "Incorrect password"
                            isLoading.value = false
                        } else {
                            // Login successful!
                            isLoading.value = false
                            onLoginSuccess(user.username)  // Tell parent that login succeeded
                        }
                    } catch (e: Exception) {
                        // Something went wrong
                        errorMessage.value = "Error: ${e.message}"
                        isLoading.value = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading.value,  // Disable button while loading
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            if (isLoading.value) {
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Log In")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        TextButton(
            onClick = { navController.navigate("RegisterScreenRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Register")
        }
    }
}