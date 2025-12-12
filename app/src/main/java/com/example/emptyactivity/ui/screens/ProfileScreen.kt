package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.UserProfileEntity
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.ui.navigation.LocalNavController
import com.example.emptyactivity.ui.navigation.LocalThemeState
import com.example.emptyactivity.utils.formatWeight
import com.example.emptyactivity.utils.getDisplayWeight
import com.example.emptyactivity.utils.convertToKg
import kotlinx.coroutines.launch

/**
 * This screen shows the user's profile information and allows them to edit it.
 * Users can update their weight, height, age, goal, change password,
 * toggle weight units, and logout.
 */

/**
 * Profile screen component.
 * Displays user profile information and allows editing.
 *
 * @param currentUsername The username of the currently logged-in user
 * @param onLogout Callback function called when user logs out
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUsername: String?,
    onLogout: () -> Unit
) {
    // Get the database to access profile data
    val context = LocalContext.current
    val database = WorkoutDatabase.getDatabase(context)
    val profileDao = database.userProfileDao()
    val userDao = database.userDao()

    // Get theme state to allow theme switching
    val (isDarkTheme, setTheme) = LocalThemeState.current

    // Coroutine scope for database operations
    val scope = rememberCoroutineScope()

    // State for profile data
    var profile by remember { mutableStateOf<UserProfileEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // State for editing mode
    var isEditing by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }

    // State for input fields
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var useKg by remember { mutableStateOf(true) }

    // State for password change
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // State for show/hide toggles (same as register)
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    // State for error message
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Load profile when screen opens
    LaunchedEffect(currentUsername) {
        if (currentUsername != null) {
            scope.launch {
                try {
                    val loadedProfile = profileDao.findByUsername(currentUsername)
                    profile = loadedProfile
                    if (loadedProfile != null) {
                        age = loadedProfile.age.toString()
                        height = loadedProfile.height.toString()
                        useKg = loadedProfile.useKg
                        // Convert weight to display unit
                        val displayWeight = getDisplayWeight(loadedProfile.weight, useKg)
                        weight = String.format("%.1f", displayWeight)
                        goal = loadedProfile.goal
                    }
                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Error loading profile: ${e.message}"
                    isLoading = false
                }
            }
        }
    }

    // Update weight display when unit changes
    LaunchedEffect(useKg) {
        if (profile != null && !isEditing) {
            val displayWeight = getDisplayWeight(profile!!.weight, useKg)
            weight = String.format("%.1f", displayWeight)
        }
    }

    // Main column layout (scrollable)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen title
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username display
        if (currentUsername != null) {
            Text(
                text = "@$currentUsername",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            // Show loading indicator
            CircularProgressIndicator()
        } else {
            // Profile information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = if (isEditing) "Edit Profile" else "Profile Information",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        // Edit mode - show input fields
                        OutlinedTextField(
                            value = age,
                            onValueChange = {
                                age = it
                                errorMessage = null
                            },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = height,
                            onValueChange = {
                                height = it
                                errorMessage = null
                            },
                            label = { Text("Height (cm)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = weight,
                            onValueChange = {
                                weight = it
                                errorMessage = null
                            },
                            label = { Text("Weight (${if (useKg) "kg" else "lbs"})") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = goal,
                            onValueChange = {
                                goal = it
                                errorMessage = null
                            },
                            label = { Text("Fitness Goal") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("e.g., Build Muscle, Lose Weight, Maintain") }
                        )
                    } else {
                        // View mode - show profile data
                        ProfileInfoRow("Age", if (profile?.age ?: 0 > 0) "${profile?.age} years" else "Not set")
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileInfoRow("Height", if (profile?.height ?: 0 > 0) "${profile?.height} cm" else "Not set")
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileInfoRow(
                            "Weight",
                            if ((profile?.weight ?: 0.0) > 0) formatWeight(profile!!.weight, useKg) else "Not set"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileInfoRow("Goal", profile?.goal?.takeIf { it.isNotBlank() } ?: "Not set")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Success message
                    if (successMessage != null) {
                        Text(
                            text = successMessage!!,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isEditing) {
                            // Save and Cancel buttons
                            OutlinedButton(
                                onClick = {
                                    isEditing = false
                                    // Reset to original values
                                    if (profile != null) {
                                        age = profile!!.age.toString()
                                        height = profile!!.height.toString()
                                        val displayWeight = getDisplayWeight(profile!!.weight, useKg)
                                        weight = String.format("%.1f", displayWeight)
                                        goal = profile!!.goal
                                    }
                                    errorMessage = null
                                    successMessage = null
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    // Validate input
                                    if (age.isBlank() || height.isBlank() || weight.isBlank() || goal.isBlank()) {
                                        errorMessage = "Please fill in all fields"
                                        return@Button
                                    }

                                    val ageInt = age.toIntOrNull()
                                    val heightInt = height.toIntOrNull()
                                    val weightDouble = weight.toDoubleOrNull()

                                    if (ageInt == null || ageInt <= 0) {
                                        errorMessage = "Please enter a valid age"
                                        return@Button
                                    }

                                    if (heightInt == null || heightInt <= 0) {
                                        errorMessage = "Please enter a valid height"
                                        return@Button
                                    }

                                    if (weightDouble == null || weightDouble <= 0) {
                                        errorMessage = "Please enter a valid weight"
                                        return@Button
                                    }

                                    // Convert weight to kg for storage
                                    val weightInKg = convertToKg(weightDouble, useKg)

                                    // Save profile
                                    scope.launch {
                                        try {
                                            val updatedProfile = UserProfileEntity(
                                                id = profile?.id ?: 0,
                                                username = currentUsername ?: "",
                                                age = ageInt,
                                                height = heightInt,
                                                weight = weightInKg,
                                                goal = goal,
                                                useKg = useKg
                                            )

                                            if (profile == null) {
                                                profileDao.insert(updatedProfile)
                                            } else {
                                                profileDao.update(updatedProfile)
                                            }

                                            profile = updatedProfile
                                            isEditing = false
                                            successMessage = "Profile updated successfully!"
                                            errorMessage = null

                                            // Clear success message after 3 seconds
                                            kotlinx.coroutines.delay(3000)
                                            successMessage = null
                                        } catch (e: Exception) {
                                            errorMessage = "Error saving profile: ${e.message}"
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save")
                            }
                        } else {
                            // Edit button
                            Button(
                                onClick = { isEditing = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Profile")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weight unit preference card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weight Unit",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (useKg) "Kilograms (kg)" else "Pounds (lbs)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = useKg,
                        onCheckedChange = { newValue ->
                            useKg = newValue
                            // Update profile in database
                            if (profile != null) {
                                scope.launch {
                                    try {
                                        val updatedProfile = profile!!.copy(useKg = newValue)
                                        profileDao.update(updatedProfile)
                                        profile = updatedProfile

                                        // Update weight display
                                        if (!isEditing) {
                                            val displayWeight = getDisplayWeight(profile!!.weight, newValue)
                                            weight = String.format("%.1f", displayWeight)
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error updating unit preference: ${e.message}"
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Theme toggle card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Appearance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isDarkTheme) "Dark mode" else "Light mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { setTheme(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password change card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isChangingPassword) {
                        // Show password change fields
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                errorMessage = null
                            },
                            label = { Text("Current Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { showCurrent = !showCurrent }) {
                                    Text(if (showCurrent) "Hide" else "Show")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                errorMessage = null
                            },
                            label = { Text("New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { showNew = !showNew }) {
                                    Text(if (showNew) "Hide" else "Show")
                                }
                            }
                        )

                        // REAL-TIME VALIDATION (same as register screen)
                        if (newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            val hasUpper = newPassword.any { it.isUpperCase() }
                            val hasLower = newPassword.any { it.isLowerCase() }
                            val hasDigit = newPassword.any { it.isDigit() }
                            val hasSpecial = newPassword.any { !it.isLetterOrDigit() }
                            val lengthOK = newPassword.length in 6..16

                            rule("6–16 characters", lengthOK)
                            rule("Uppercase letter", hasUpper)
                            rule("Lowercase letter", hasLower)
                            rule("Number", hasDigit)
                            rule("Special character", hasSpecial)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = {
                                confirmNewPassword = it
                                errorMessage = null
                            },
                            label = { Text("Confirm New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { showConfirm = !showConfirm }) {
                                    Text(if (showConfirm) "Hide" else "Show")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password change buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isChangingPassword = false
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmNewPassword = ""
                                    errorMessage = null
                                    successMessage = null
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    // Validate password change
                                    if (currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
                                        errorMessage = "Please fill in all password fields"
                                        return@Button
                                    }

                                    if (newPassword != confirmNewPassword) {
                                        errorMessage = "New passwords do not match"
                                        return@Button
                                    }

                                    // SAME VALIDATION RULES AS REGISTER
                                    val hasUpper = newPassword.any { it.isUpperCase() }
                                    val hasLower = newPassword.any { it.isLowerCase() }
                                    val hasDigit = newPassword.any { it.isDigit() }
                                    val hasSpecial = newPassword.any { !it.isLetterOrDigit() }

                                    if (newPassword.length !in 6..16) {
                                        errorMessage = "Password must be 6–16 characters long"
                                        return@Button
                                    }

                                    if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
                                        errorMessage =
                                            "Password must include uppercase, lowercase, number, and special character"
                                        return@Button
                                    }

                                    // Change password
                                    scope.launch {
                                        try {
                                            // Get current user from database
                                            val user = userDao.findByUsername(currentUsername ?: "")

                                            if (user == null) {
                                                errorMessage = "User not found"
                                                return@launch
                                            }

                                            // Check if current password is correct
                                            if (user.password != currentPassword) {
                                                errorMessage = "Current password is incorrect"
                                                return@launch
                                            }

                                            // Create updated user entity with new password
                                            val updatedUser = com.example.emptyactivity.database.UserEntity(
                                                id = user.id,
                                                username = user.username,
                                                password = newPassword
                                            )

                                            // Update in database
                                            userDao.update(updatedUser)

                                            // Success - clear fields and show message
                                            isChangingPassword = false
                                            currentPassword = ""
                                            newPassword = ""
                                            confirmNewPassword = ""
                                            successMessage = "Password changed successfully!"
                                            errorMessage = null

                                            // Clear success message after 3 seconds
                                            kotlinx.coroutines.delay(3000)
                                            successMessage = null
                                        } catch (e: Exception) {
                                            errorMessage = "Error changing password: ${e.message}"
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Update")
                            }
                        }
                    } else {
                        // Show change password button
                        Button(
                            onClick = { isChangingPassword = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Password")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            OutlinedButton(
                onClick = {
                    // Call logout callback which will handle navigation
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }
        }
    }
}

/**
 * Helper composable to display a profile information row.
 *
 * @param label The label for the information
 * @param value The value to display
 */
@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun rule(text: String, ok: Boolean) {
    Text(
        text = if (ok) "✓ $text" else "• $text",
        color = if (ok)
            MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        style = MaterialTheme.typography.bodySmall
    )
}
