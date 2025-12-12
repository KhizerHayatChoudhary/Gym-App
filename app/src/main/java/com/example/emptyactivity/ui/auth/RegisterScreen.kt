package com.example.emptyactivity.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.UserEntity
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.ui.navigation.LocalNavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Simple password validation with clear student-friendly rules
 */
private fun isValidPassword(password: String): Boolean {
    if (password.length !in 6..16) return false

    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    return hasUpper && hasLower && hasDigit && hasSpecial
}

/**
 * A basic strength meter just to give feedback while typing
 */
private fun passwordStrength(password: String): String {
    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    val longEnough = password.length >= 10

    val score = listOf(hasUpper, hasLower, hasDigit, hasSpecial, longEnough).count { it }

    return when (score) {
        0, 1, 2 -> "Weak"
        3 -> "Medium"
        else -> "Strong"
    }
}

/**
 * This screen allows users to create a new account.
 * Users enter a username and password to register.
 */

/**
 * Register screen component.
 * Allows users to create a new account with username and password.
 *
 * @param onRegisterSuccess Callback function called when registration is successful
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: (String) -> Unit) {
    // Get the navigation controller so we can navigate back to login
    val navController = LocalNavController.current

    // Get the database to save new users
    val context = LocalContext.current
    val database = WorkoutDatabase.getDatabase(context)
    val userDao = database.userDao()

    // Coroutine scope for running database operations (which are async)
    val scope = rememberCoroutineScope()

    // State variables for the input fields
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    // NEW: password visibility toggles
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    // State for error message
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // State for showing loading indicator
    val isLoading = remember { mutableStateOf(false) }

    // Main column layout (centered, scrollable) with theme-aware background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Screen title
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Join Trainr today!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Card container for input fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Username input field
                OutlinedTextField(
                    value = username.value,
                    onValueChange = {
                        username.value = it
                        errorMessage.value = null
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input field WITH show/hide toggle
                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        errorMessage.value = null
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible.value)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Text(if (passwordVisible.value) "Hide" else "Show")
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm password input field WITH show/hide toggle
                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = {
                        confirmPassword.value = it
                        errorMessage.value = null
                    },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible.value)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                            Text(if (confirmPasswordVisible.value) "Hide" else "Show")
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // NEW: Password strength + validation hints (OUTSIDE the card)
        if (password.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            val strength = passwordStrength(password.value)

            // Student-friendly strength label
            Text(
                text = "Password strength: $strength",
                color = when (strength) {
                    "Weak" -> MaterialTheme.colorScheme.error
                    "Medium" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {

                // Length rule (soft limit)
                Text(
                    text =
                        if (password.value.length in 6..16)
                            "✓ Password length looks good"
                        else if (password.value.length < 6)
                            "• Must be at least 6 characters"
                        else
                            "✗ Maximum is 16 characters",
                    color = when {
                        password.value.length in 6..16 -> MaterialTheme.colorScheme.primary
                        password.value.length < 6 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (password.value.any { it.isUpperCase() })
                        "✓ Has uppercase letter"
                    else
                        "• Needs an uppercase letter",
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (password.value.any { it.isUpperCase() }) 1f else 0.5f
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (password.value.any { it.isLowerCase() })
                        "✓ Has lowercase letter"
                    else
                        "• Needs a lowercase letter",
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (password.value.any { it.isLowerCase() }) 1f else 0.5f
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (password.value.any { it.isDigit() })
                        "✓ Has a number"
                    else
                        "• Needs a number",
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (password.value.any { it.isDigit() }) 1f else 0.5f
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (password.value.any { !it.isLetterOrDigit() })
                        "✓ Has a special character"
                    else
                        "• Needs a special character",
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (password.value.any { !it.isLetterOrDigit() }) 1f else 0.5f
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Error message
        if (errorMessage.value != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage.value!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register button
        Button(
            onClick = {
                // Validate fields
                if (username.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
                    errorMessage.value = "Please fill in all fields"
                    return@Button
                }

                if (password.value != confirmPassword.value) {
                    errorMessage.value = "Passwords do not match"
                    return@Button
                }

                // Strong password rules
                if (!isValidPassword(password.value)) {
                    errorMessage.value =
                        "Password must follow the criteria above"
                    return@Button
                }

                // Start loading
                isLoading.value = true
                errorMessage.value = null

                // Register user
                scope.launch {
                    try {
                        val usernameExists = userDao.usernameExists(username.value)

                        if (usernameExists) {
                            errorMessage.value = "That username already exists"
                            isLoading.value = false
                        } else {
                            val newUser = UserEntity(
                                username = username.value,
                                password = password.value
                            )
                            userDao.insert(newUser)

                            isLoading.value = false
                            onRegisterSuccess(username.value)
                        }
                    } catch (e: Exception) {
                        errorMessage.value = "Something went wrong: ${e.message}"
                        isLoading.value = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading.value,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to login button
        TextButton(
            onClick = { navController.navigate("LoginScreenRoute") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? Log In")
        }
    }
}
