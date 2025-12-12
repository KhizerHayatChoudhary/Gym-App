package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.ui.navigation.LocalNavController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * This is the main/home screen of the app.
 * It shows an overview of workout stats, a streak counter, and quick action buttons.
 * This is the first screen users see when they open the app.
 */

/**
 * Main home screen component.
 * Displays workout statistics, streak information, and quick navigation buttons.
 * 
 * @param currentUsername The username of the logged-in user
 * @param modifier Optional modifier to adjust the layout
 */
@Composable
fun MainScreen(
    currentUsername: String?,
    modifier: Modifier = Modifier
) {
    // Get the navigation controller so we can navigate to other screens
    val navController = LocalNavController.current
    
    // Get the database to fetch workout data
    val context = LocalContext.current
    val database = WorkoutDatabase.getDatabase(context)
    val workoutDao = database.workoutDao()
    val profileDao = database.userProfileDao()
    
    // Get user profile to access workout plan
    var userProfile by remember { mutableStateOf<com.example.emptyactivity.database.UserProfileEntity?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(currentUsername) {
        if (currentUsername != null) {
            scope.launch {
                userProfile = profileDao.findByUsername(currentUsername)
            }
        }
    }
    
    // Get all workouts from the database (automatically updates when data changes)
    val workouts by workoutDao.getAll().collectAsState(initial = emptyList())
    
    // Calculate stats from the actual workout data
    val workoutsThisWeek = calculateWorkoutsThisWeek(workouts)
    val totalWorkouts = workouts.size
    val currentStreak = calculateCurrentStreak(workouts)
    val bestStreak = calculateBestStreak(workouts)

    // Custom colors for the UI (theme-aware)
    val surfaceSoft = MaterialTheme.colorScheme.background  // Background color adapts to theme
    val textMuted = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // Muted text color adapts to theme

    // Main column that holds all the content (scrollable)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceSoft)
            .verticalScroll(rememberScrollState())  // Allow scrolling if content is too long
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // App header - Title and subtitle
        Text(
            text = "Trainr",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Track your workouts and keep your streak alive.",
            style = MaterialTheme.typography.bodyMedium,
            color = textMuted
        )

        Spacer(Modifier.height(16.dp))

        // Today's overview card - Shows workout stats
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp))  // Add shadow for depth
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Today's overview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                
                // Three stat columns side by side
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Workouts this week stat
                    Column {
                        Text(
                            text = "Workouts this week",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "$workoutsThisWeek",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    // Total workouts stat
                    Column {
                        Text(
                            text = "Total workouts",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "$totalWorkouts",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    // Best streak stat
                    Column {
                        Text(
                            text = "Best streak",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "$bestStreak days",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Quick actions section header
        Text(
            text = "Quick actions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(12.dp))

        // Streak card button - Tapping goes to About screen
        Button(
            onClick = { navController.navigate("AboutScreenRoute") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            StreakCard(
                days = currentStreak,  // Use actual calculated streak
                subtitle = "Tap to see more about your progress"
            )
        }

        Spacer(Modifier.height(16.dp))

        // View workout plan button (if user has a profile)
        if (userProfile != null && userProfile!!.goal.isNotBlank()) {
            Button(
                onClick = {
                    // Navigate to workout plan screen
                    val profile = userProfile!!
                    navController.navigate("WorkoutPlanRoute/${profile.goal}/${profile.age}/${profile.height}/${profile.weight}")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "View Workout Plan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "See your personalized workout journal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Workout plan"
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
        }

        // Primary action button - Log a workout
        OutlinedButton(
            onClick = { navController.navigate("WorkoutScreenRoute") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Log a workout",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Add sets, reps and weights for today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textMuted
                    )
                }
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add workout"
                )
            }
        }

        Spacer(Modifier.height(32.dp))


    }
}

/**
 * Streak card component with a beautiful gradient background.
 * Shows how many days in a row the user has worked out.
 * 
 * @param days Number of days in current streak
 * @param subtitle Text to display below the streak number
 * @param modifier Optional modifier to adjust the layout
 */
@Composable
private fun StreakCard(
    days: Int,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    // Create a nice pink/orange gradient for the streak card
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF7EC8),  // Pink
            Color(0xFFFF6B8A),  // Coral
            Color(0xFFFF8066)   // Orange
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 90.dp)
            .background(brush = gradient, shape = RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side - Text content
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Current streak",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$days days",
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.95f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right side - Icon in a semi-transparent circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),  // Semi-transparent white circle
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Streak",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Calculates how many unique days had workouts in the last 7 days.
 * 
 * @param workouts List of all workouts from the database
 * @return Number of days with workouts this week
 */
private fun calculateWorkoutsThisWeek(workouts: List<com.example.emptyactivity.database.WorkoutEntity>): Int {
    if (workouts.isEmpty()) return 0
    
    // Get the current time
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    // Calculate 7 days ago
    val sevenDaysAgo = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val weekAgo = calendar.timeInMillis
    
    // Get unique days that had workouts in the last 7 days
    val uniqueDays = workouts
        .filter { it.date >= weekAgo && it.date < sevenDaysAgo }
        .map { workout ->
            // Convert timestamp to day (ignore time, just get the date)
            val workoutCalendar = Calendar.getInstance()
            workoutCalendar.timeInMillis = workout.date
            workoutCalendar.set(Calendar.HOUR_OF_DAY, 0)
            workoutCalendar.set(Calendar.MINUTE, 0)
            workoutCalendar.set(Calendar.SECOND, 0)
            workoutCalendar.set(Calendar.MILLISECOND, 0)
            workoutCalendar.timeInMillis
        }
        .distinct()
    
    return uniqueDays.size
}

/**
 * Calculates the current streak (consecutive days with workouts ending today).
 * 
 * A streak continues if the user worked out today or yesterday.
 * If the last workout was more than 1 day ago, the streak is broken.
 * 
 * @param workouts List of all workouts from the database
 * @return Number of days in current streak
 */
private fun calculateCurrentStreak(workouts: List<com.example.emptyactivity.database.WorkoutEntity>): Int {
    if (workouts.isEmpty()) return 0
    
    // Get today at midnight (start of day)
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val today = calendar.timeInMillis
    
    // Get unique days that had workouts, sorted newest first
    val uniqueDays = workouts
        .map { workout ->
            // Convert workout timestamp to just the date (remove time)
            val workoutCalendar = Calendar.getInstance()
            workoutCalendar.timeInMillis = workout.date
            workoutCalendar.set(Calendar.HOUR_OF_DAY, 0)
            workoutCalendar.set(Calendar.MINUTE, 0)
            workoutCalendar.set(Calendar.SECOND, 0)
            workoutCalendar.set(Calendar.MILLISECOND, 0)
            workoutCalendar.timeInMillis
        }
        .distinct()
        .sortedDescending()  // Newest first
    
    if (uniqueDays.isEmpty()) return 0
    
    // Check if we have a workout today or yesterday
    // If not, streak is broken (return 0)
    val yesterdayCalendar = Calendar.getInstance()
    yesterdayCalendar.timeInMillis = today
    yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = yesterdayCalendar.timeInMillis
    
    val mostRecentDay = uniqueDays.first()
    
    // If the most recent workout was more than 1 day ago, streak is broken
    if (mostRecentDay != today && mostRecentDay != yesterday) {
        return 0
    }
    
    // Start counting consecutive days from the most recent workout day
    var streak = 0
    var currentDay = mostRecentDay
    
    // Check each day going backwards to see if it's consecutive
    for (day in uniqueDays) {
        if (day == currentDay) {
            // Found a workout on this expected day - streak continues
            streak++
            // Move to the previous day to check next
            val prevCalendar = Calendar.getInstance()
            prevCalendar.timeInMillis = currentDay
            prevCalendar.add(Calendar.DAY_OF_YEAR, -1)
            currentDay = prevCalendar.timeInMillis
        } else if (day < currentDay) {
            // Found a workout that's older than expected - streak broken
            break
        }
        // If day > currentDay, we skip it (it's newer, already counted)
    }
    
    return streak
}

/**
 * Calculates the best (longest) streak of consecutive workout days.
 * 
 * @param workouts List of all workouts from the database
 * @return Longest streak of consecutive days
 */
private fun calculateBestStreak(workouts: List<com.example.emptyactivity.database.WorkoutEntity>): Int {
    if (workouts.isEmpty()) return 0
    
    // Get unique days that had workouts, sorted oldest first
    val uniqueDays = workouts
        .map { workout ->
            val workoutCalendar = Calendar.getInstance()
            workoutCalendar.timeInMillis = workout.date
            workoutCalendar.set(Calendar.HOUR_OF_DAY, 0)
            workoutCalendar.set(Calendar.MINUTE, 0)
            workoutCalendar.set(Calendar.SECOND, 0)
            workoutCalendar.set(Calendar.MILLISECOND, 0)
            workoutCalendar.timeInMillis
        }
        .distinct()
        .sorted()  // Oldest first
    
    if (uniqueDays.isEmpty()) return 0
    
    var bestStreak = 1
    var currentStreak = 1
    
    // Go through each day and check if it's consecutive with the previous day
    for (i in 1 until uniqueDays.size) {
        val currentDay = uniqueDays[i]
        val previousDay = uniqueDays[i - 1]
        
        // Calculate if current day is exactly one day after previous day
        val prevCalendar = Calendar.getInstance()
        prevCalendar.timeInMillis = previousDay
        prevCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val nextExpectedDay = prevCalendar.timeInMillis
        
        if (currentDay == nextExpectedDay) {
            // Consecutive day - increase current streak
            currentStreak++
        } else {
            // Not consecutive - reset current streak
            bestStreak = maxOf(bestStreak, currentStreak)
            currentStreak = 1
        }
    }
    
    // Check the last streak
    bestStreak = maxOf(bestStreak, currentStreak)
    
    return bestStreak
}

