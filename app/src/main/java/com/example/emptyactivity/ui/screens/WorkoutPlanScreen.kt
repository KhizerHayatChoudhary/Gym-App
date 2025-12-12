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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.api.WorkoutPlan
import com.example.emptyactivity.api.WorkoutPlanExercise
import com.example.emptyactivity.api.WorkoutPlanGenerator

/**
 * Screen that shows the user's personalized workout plan.
 * Displays all exercises with sets, reps, rest times, and instructions.
 * 
 * @param goal The user's fitness goal
 * @param age User's age
 * @param height User's height in cm
 * @param weight User's weight in kg
 * @param onDismiss What to do when user closes this screen
 */
@Composable
fun WorkoutPlanScreen(
    goal: String,
    age: Int,
    height: Int,
    weight: Double,
    onDismiss: () -> Unit
) {
    // State for the workout plan
    var workoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Create the plan generator
    val planGenerator = remember { WorkoutPlanGenerator() }
    
    // Generate the workout plan when the screen loads
    LaunchedEffect(Unit) {
        try {
            workoutPlan = planGenerator.generatePlan(goal, age, height, weight)
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error generating plan: ${e.message}"
            isLoading = false
        }
    }
    
    // Main column layout (scrollable)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your Workout Plan",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Center)
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("Close")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Personalized for: $goal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Generating your personalized workout plan...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                }
            }
        } else if (errorMessage != null) {
            // Show error message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else if (workoutPlan != null) {
            // Display workout plan
            DisplayWorkoutPlan(workoutPlan = workoutPlan!!)
        }
    }
}

/**
 * Displays the workout plan details.
 */
@Composable
private fun DisplayWorkoutPlan(workoutPlan: WorkoutPlan) {
    // Plan overview card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Plan Overview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            PlanInfoRow("Duration", workoutPlan.duration)
            Spacer(modifier = Modifier.height(8.dp))
            PlanInfoRow("Frequency", workoutPlan.frequency)
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Exercises list
    Text(
        text = "Exercises",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    workoutPlan.exercises.forEachIndexed { index, exercise ->
        ExerciseCard(exercise = exercise, exerciseNumber = index + 1)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Displays a single exercise card.
 */
@Composable
private fun ExerciseCard(exercise: WorkoutPlanExercise, exerciseNumber: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Exercise header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$exerciseNumber. ${exercise.name}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                // Sets and reps badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "${exercise.sets}×${exercise.reps}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rest time
            Text(
                text = "Rest: ${exercise.restSeconds} seconds",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Instructions
            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = exercise.instructions,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Helper composable for plan info rows.
 */
@Composable
private fun PlanInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

