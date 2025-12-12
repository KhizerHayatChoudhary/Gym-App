package com.example.emptyactivity.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.UserProfileEntity
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.ui.navigation.LocalNavController
import com.example.emptyactivity.utils.convertToKg
import kotlinx.coroutines.launch

/**
 * This screen appears right after registration to collect user's initial profile information.
 * Users enter their age, height, weight, and fitness goal.
 */

/**
 * Profile setup screen component.
 * Collects initial profile information from newly registered users.
 *
 * @param username The username of the newly registered user
 * @param onSetupComplete Callback function called when profile setup is complete
 *                        Parameters: goal, age, height, weight
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    username: String,
    onSetupComplete: (String, Int, Int, Double) -> Unit
) {
    // Get the database to save profile data
    val context = LocalContext.current
    val database = WorkoutDatabase.getDatabase(context)
    val profileDao = database.userProfileDao()

    // Get navigation controller to navigate to workout plan screen
    val navController = LocalNavController.current

    // Coroutine scope for database operations
    val scope = rememberCoroutineScope()

    // State variables for the input fields
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var useKg by remember { mutableStateOf(true) }

    // State for error message
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State for showing loading indicator
    var isLoading by remember { mutableStateOf(false) }

    // Main column layout (centered)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Screen title
        Text(
            text = "Complete Your Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Help us personalize your experience",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Age input field
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

        Spacer(modifier = Modifier.height(16.dp))

        // Height input field
        OutlinedTextField(
            value = height,
            onValueChange = {
                height = it
                errorMessage = null
            },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("e.g., 175") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight unit toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weight Unit:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (useKg) "kg" else "lbs",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Switch(
                    checked = useKg,
                    onCheckedChange = { useKg = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight input field
        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
                errorMessage = null
            },
            label = { Text("Weight (${if (useKg) "kg" else "lbs"})") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(if (useKg) "e.g., 70.5" else "e.g., 155.0") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Goal dropdown selection
        var goalExpanded by remember { mutableStateOf(false) }
        val goalOptions = listOf("Build Muscle", "Lose Weight", "Maintain", "General Fitness")

        // Use ExposedDropdownMenuBox for proper dropdown functionality
        ExposedDropdownMenuBox(
            expanded = goalExpanded,
            onExpandedChange = { goalExpanded = !goalExpanded }
        ) {
            OutlinedTextField(
                value = goal,
                onValueChange = { },
                label = { Text("Fitness Goal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                placeholder = { Text("Select your fitness goal") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) }
            )

            ExposedDropdownMenu(
                expanded = goalExpanded,
                onDismissRequest = { goalExpanded = false }
            ) {
                goalOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            goal = option
                            goalExpanded = false
                            errorMessage = null
                        }
                    )
                }
            }
        }

        // Error message (only shows if there's an error)
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue button
        Button(
            onClick = {
                // Validate input fields
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

                // Start loading
                isLoading = true
                errorMessage = null

                // Save profile (database operation must run in coroutine)
                scope.launch {
                    try {
                        val newProfile = UserProfileEntity(
                            username = username,
                            age = ageInt,
                            height = heightInt,
                            weight = weightInKg,
                            goal = goal,
                            useKg = useKg
                        )
                        profileDao.insert(newProfile)

                        // Profile setup successful!
                        isLoading = false

                        // Call completion with profile data - MainLayout will handle navigation
                        onSetupComplete(goal, ageInt, heightInt, weightInKg)
                    } catch (e: Exception) {
                        // Something went wrong
                        errorMessage = "Error: ${e.message}"
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading  // Disable button while loading
        ) {
            if (isLoading) {
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Continue")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip button (optional - allows user to skip for now)
        TextButton(
            onClick = {
                // Allow user to skip and set up profile later
                // Pass empty/default values
                onSetupComplete("", 0, 0, 0.0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Skip for now")
        }
    }
}