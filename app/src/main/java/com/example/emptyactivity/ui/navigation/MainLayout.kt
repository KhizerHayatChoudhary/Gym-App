package com.example.emptyactivity.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emptyactivity.ui.auth.LoginScreen
import com.example.emptyactivity.ui.auth.RegisterScreen
import com.example.emptyactivity.ui.auth.ProfileSetupScreen
import com.example.emptyactivity.ui.screens.InfoScreen
import com.example.emptyactivity.ui.screens.AddWorkoutScreen
import com.example.emptyactivity.ui.screens.ProfileScreen
import com.example.emptyactivity.ui.screens.MainScreen
import com.example.emptyactivity.ui.screens.WorkoutHistoryScreen
import com.example.emptyactivity.ui.screens.WorkoutPlanScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.emptyactivity.database.WorkoutDatabase
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * This file handles all the navigation in the app.
 * It sets up the routes (like URLs for different screens) and creates the bottom navigation bar.
 */

/**
 * Main layout component that wraps the entire app.
 * It sets up navigation between screens and displays the bottom navigation bar.
 * Also handles login state - shows login/register screens if not logged in.
 */
@Composable
fun MainLayout() {
    // Get the navigation controller so we can navigate between screens
    val navController = LocalNavController.current

    // Track if user is logged in (simple state management)
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentUsername by remember { mutableStateOf<String?>(null) }
    var needsProfileSetup by remember { mutableStateOf(false) }

    // Track workout plan parameters to navigate after profile setup
    var workoutPlanParams by remember { mutableStateOf<Pair<String, Triple<Int, Int, Double>>?>(null) }

    // Get database to check if profile exists
    val context = LocalContext.current
    val database = remember { WorkoutDatabase.getDatabase(context) }
    val profileDao = database.userProfileDao()
    val scope = rememberCoroutineScope()

    // If user is not logged in, show login/register screens
    if (!isLoggedIn) {
        NavHost(
            navController = navController,
            startDestination = "LoginScreenRoute",  // Start at login screen
            modifier = Modifier.fillMaxSize()
        ) {
            // Login screen
            composable("LoginScreenRoute") {
                LoginScreen(
                    onLoginSuccess = { username ->
                        // User successfully logged in
                        currentUsername = username
                        // Check if profile exists
                        scope.launch {
                            val profileExists = profileDao.profileExists(username)
                            if (!profileExists) {
                                // Need profile setup - navigate there first
                                needsProfileSetup = true
                                navController.navigate("ProfileSetupRoute") {
                                    popUpTo("LoginScreenRoute") { inclusive = true }
                                }
                            } else {
                                // Profile exists - mark as logged in
                                // This will switch to the logged-in NavHost
                                needsProfileSetup = false
                                isLoggedIn = true
                            }
                        }
                    }
                )
            }

            // Register screen
            composable("RegisterScreenRoute") {
                RegisterScreen(
                    onRegisterSuccess = { username ->
                        // User successfully registered
                        // Set username but DON'T set isLoggedIn yet - keep in "not logged in" flow
                        currentUsername = username
                        needsProfileSetup = true

                        // Navigate to profile setup screen immediately
                        navController.navigate("ProfileSetupRoute") {
                            // Clear the back stack so user can't go back to login
                            popUpTo("LoginScreenRoute") { inclusive = true }
                        }
                    }
                )
            }

            // Profile setup screen (shown after registration or if profile missing)
            composable("ProfileSetupRoute") {
                if (currentUsername != null) {
                    ProfileSetupScreen(
                        username = currentUsername!!,
                        onSetupComplete = { goal, age, height, weight ->
                            // After profile setup is complete, store params and mark as logged in
                            // This will switch to the logged-in NavHost
                            workoutPlanParams = Pair(goal, Triple(age, height, weight))
                            needsProfileSetup = false
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    } else {
        // User is logged in - show the main app with bottom navigation

        // Navigate to workout plan if we just completed profile setup
        LaunchedEffect(workoutPlanParams) {
            workoutPlanParams?.let { params ->
                val (goal, triple) = params
                val (age, height, weight) = triple

                // Small delay to ensure NavHost is ready
                kotlinx.coroutines.delay(100)

                // Only show workout plan if user actually filled in profile (not skipped)
                if (goal.isNotBlank() && age > 0 && height > 0 && weight > 0.0) {
                    navController.navigate("WorkoutPlanRoute/$goal/$age/$height/$weight") {
                        // Start fresh from workout plan
                        popUpTo(0) { inclusive = false }
                    }
                } else {
                    // User skipped, go directly to main screen
                    navController.navigate("MainScreenRoute") {
                        popUpTo(0) { inclusive = false }
                    }
                }

                // Clear the params so we don't navigate again
                workoutPlanParams = null
            }
        }

        // Figure out which screen is currently showing (for highlighting in bottom bar)
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: "MainScreenRoute"

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // This is where the actual screen content gets displayed
            Box(
                modifier = Modifier
                    .weight(1f)  // Takes up all available space except bottom bar
                    .fillMaxWidth()
            ) {
                // NavHost is like a router - it shows different screens based on the route
                NavHost(
                    navController = navController,
                    startDestination = "MainScreenRoute",  // First screen shown when app opens
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Define all the routes (paths) to different screens
                    composable("MainScreenRoute") { MainScreen(currentUsername = currentUsername) }
                    composable("AboutScreenRoute") { InfoScreen() }
                    composable("ProfileScreenRoute") {
                        ProfileScreen(
                            currentUsername = currentUsername,
                            onLogout = {
                                // Logout user - reset all state
                                isLoggedIn = false
                                currentUsername = null
                                needsProfileSetup = false
                                workoutPlanParams = null
                                // Navigation will automatically switch to login NavHost
                            }
                        )
                    }
                    composable("WorkoutScreenRoute") { AddWorkoutScreen(currentUsername = currentUsername) }
                    composable("HistoryScreenRoute") { WorkoutHistoryScreen(currentUsername = currentUsername) }

                    // Workout plan screen (shown after profile setup)
                    composable(
                        route = "WorkoutPlanRoute/{goal}/{age}/{height}/{weight}",
                        arguments = listOf(
                            navArgument("goal") { type = NavType.StringType },
                            navArgument("age") { type = NavType.IntType },
                            navArgument("height") { type = NavType.IntType },
                            navArgument("weight") { type = NavType.FloatType }
                        )
                    ) { backStackEntry ->
                        val goal = backStackEntry.arguments?.getString("goal") ?: ""
                        val age = backStackEntry.arguments?.getInt("age") ?: 0
                        val height = backStackEntry.arguments?.getInt("height") ?: 0
                        val weight = backStackEntry.arguments?.getFloat("weight")?.toDouble() ?: 0.0

                        WorkoutPlanScreen(
                            goal = goal,
                            age = age,
                            height = height,
                            weight = weight,
                            onDismiss = {
                                // Navigate to main screen after viewing plan
                                navController.navigate("MainScreenRoute") {
                                    popUpTo("WorkoutPlanRoute/{goal}/{age}/{height}/{weight}") { inclusive = true }
                                }
                            }
                        )
                    }

                }
            }

            // Bottom navigation bar that stays at the bottom of the screen
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        // If clicking home, go back to home screen
                        "MainScreenRoute" -> {
                            navController.popBackStack("MainScreenRoute", inclusive = false)
                        }
                        // For other routes, just navigate normally
                        else -> {
                            navController.navigate(route)
                        }
                    }
                }
            )
        }
    }
}

/**
 * Bottom navigation bar component.
 * Shows 5 icons at the bottom of the screen for quick navigation.
 *
 * @param currentRoute The route (screen path) that's currently active
 * @param onNavigate Function to call when user clicks a navigation item
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // Create a surface with shadow for the bottom bar (adapts to theme)
    Surface(shadowElevation = 6.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,  // Space icons evenly
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info icon - goes to About screen
            NavIcon(
                icon = Icons.Filled.Info,
                label = "Info",
                selected = currentRoute == "AboutScreenRoute",
                onClick = { onNavigate("AboutScreenRoute") }
            )

            // Add icon - goes to Add Workout screen
            NavIcon(
                icon = Icons.Outlined.Add,
                label = "Workout",
                selected = currentRoute == "WorkoutScreenRoute",
                onClick = { onNavigate("WorkoutScreenRoute") }
            )

            // Home icon (centered, special style)
            HomeCenterIcon(
                selected = currentRoute == "MainScreenRoute",
                onClick = { onNavigate("MainScreenRoute") }
            )

            // Stats icon - currently goes to workout screen
            // TODO: Could be changed to go to a stats screen later
            NavIcon(
                icon = Icons.Filled.List,
                label = "History",
                selected = currentRoute == "HistoryScreenRoute",
                onClick = { onNavigate("HistoryScreenRoute") }
            )

            // Settings/Profile icon - goes to Profile screen
            NavIcon(
                icon = Icons.Filled.Settings,
                label = "Profile",
                selected = currentRoute == "ProfileScreenRoute",
                onClick = { onNavigate("ProfileScreenRoute") }
            )
        }
    }
}

/**
 * Regular navigation icon component.
 * Shows an icon that changes color when selected.
 *
 * @param icon The icon image to display
 * @param label Description for accessibility
 * @param selected Whether this icon is currently selected (active screen)
 * @param onClick What to do when user clicks this icon
 */
@Composable
private fun NavIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Color changes based on selection (adapts to theme)
    val tint = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .size(width = 56.dp, height = 48.dp)
            .clickable { onClick() },  // Make it clickable
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint)
    }
}

/**
 * Special home icon that's centered and has a background.
 * This one looks different from the other navigation icons.
 *
 * @param selected Whether home is currently selected
 * @param onClick What to do when user clicks home
 */
@Composable
private fun HomeCenterIcon(selected: Boolean, onClick: () -> Unit) {
    // Background color changes based on selection (adapts to theme)
    val bg = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
    val tint = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(bg, RoundedCornerShape(12.dp))  // Rounded background
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Home, contentDescription = "Home", tint = tint)
    }
}
