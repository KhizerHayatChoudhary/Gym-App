package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.database.WorkoutInfoEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen() {   // keep this name – your nav graph is using it
    val context = LocalContext.current
    val db = remember { WorkoutDatabase.getDatabase(context) }
    val infoDao = db.workoutInfoDao()
    val scope = rememberCoroutineScope()

    // Seed DB on first run
    LaunchedEffect(Unit) {
        val count = infoDao.getCount()
        if (count == 0) {
            val defaultWorkouts = listOf(
                WorkoutInfoEntity(
                    name = "Bench Press",
                    muscleGroup = "Chest, Triceps, Shoulders",
                    description = "Lie on a flat bench and press a barbell or dumbbells from chest level upward until your arms are extended, then lower back down.",
                    tips = "Keep your feet flat, shoulder blades retracted, and avoid bouncing the bar off your chest."
                ),
                WorkoutInfoEntity(
                    name = "Squat",
                    muscleGroup = "Quads, Glutes, Hamstrings",
                    description = "With a bar on your upper back or holding dumbbells, bend at the hips and knees to lower your body, then drive back up.",
                    tips = "Keep your chest up, knees tracking over toes, and sit back as if into a chair."
                ),
                WorkoutInfoEntity(
                    name = "Deadlift",
                    muscleGroup = "Hamstrings, Glutes, Lower Back",
                    description = "Lift a barbell or dumbbells from the floor by hinging at the hips and standing tall with the weight close to your body.",
                    tips = "Brace your core, keep the bar close, and avoid rounding your lower back."
                ),
                WorkoutInfoEntity(
                    name = "Overhead Press",
                    muscleGroup = "Shoulders, Triceps, Upper Chest",
                    description = "Press a barbell or dumbbells overhead until your arms are locked out, then lower with control.",
                    tips = "Brace your core, avoid leaning back, and press slightly in front of your face in a straight line."
                ),
                WorkoutInfoEntity(
                    name = "Lat Pulldown",
                    muscleGroup = "Lats, Upper Back, Biceps",
                    description = "Pull the bar from above your head down toward your upper chest while keeping your chest up and squeezing your shoulder blades.",
                    tips = "Don’t swing or jerk the weight, pull with your elbows, and control the bar back up."
                ),
                WorkoutInfoEntity(
                    name = "Dumbbell Row",
                    muscleGroup = "Lats, Rhomboids, Rear Delts, Biceps",
                    description = "With one knee and hand on a bench for support, row a dumbbell towards your hip while keeping your back flat.",
                    tips = "Avoid twisting your torso, lead with your elbow, and squeeze your back at the top."
                ),
                WorkoutInfoEntity(
                    name = "Bicep Curl",
                    muscleGroup = "Biceps",
                    description = "Curl a barbell or dumbbells from your thighs up toward your shoulders while keeping your elbows close to your body.",
                    tips = "Don’t swing the weights, control the movement, and squeeze at the top."
                ),
                WorkoutInfoEntity(
                    name = "Tricep Pushdown",
                    muscleGroup = "Triceps",
                    description = "Push a cable attachment from chest height down until your arms are extended, then return with control.",
                    tips = "Keep your elbows pinned to your sides and avoid using momentum from your shoulders."
                ),
                WorkoutInfoEntity(
                    name = "Leg Press",
                    muscleGroup = "Quads, Glutes, Hamstrings",
                    description = "Push the platform away with your feet while seated, then lower it back down under control.",
                    tips = "Don’t lock out your knees completely and keep your lower back pressed into the seat."
                ),
                WorkoutInfoEntity(
                    name = "Plank",
                    muscleGroup = "Core",
                    description = "Hold a straight-body position on your elbows or hands, keeping your core tight and body in a straight line.",
                    tips = "Avoid letting your hips sag or pike up; focus on breathing and bracing your core."
                )
            )
            infoDao.insertAll(defaultWorkouts)
        }
    }

    val workouts by infoDao.getAll().collectAsState(initial = emptyList())

    // Search + add states
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newMuscleGroup by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newTips by remember { mutableStateOf("") }

    // EDIT states
    var editingWorkout by remember { mutableStateOf<WorkoutInfoEntity?>(null) }
    var editName by remember { mutableStateOf("") }
    var editMuscleGroup by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editTips by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Text(
            text = "Workout Info",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )

        // SEARCH + ADD ROW (side by side)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                placeholder = { Text("Search workouts...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(56.dp) // match text field height
            ) {
                Text("Add")
            }
        }

        if (workouts.isEmpty()) {
            Text(
                text = "Loading workout info from the database...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            val filteredWorkouts = if (searchQuery.isBlank()) {
                workouts
            } else {
                workouts.filter { workout ->
                    workout.name.contains(searchQuery, ignoreCase = true) ||
                            workout.muscleGroup.contains(searchQuery, ignoreCase = true)
                }
            }

            if (filteredWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workouts found.\nTry a different keyword or muscle group.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredWorkouts) { workout ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = workout.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Muscle group(s): ${workout.muscleGroup}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "How to do it:",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = workout.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Tips & tricks:",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = workout.tips,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // EDIT BUTTON (aligned to the end)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            editingWorkout = workout
                                            editName = workout.name
                                            editMuscleGroup = workout.muscleGroup
                                            editDescription = workout.description
                                            editTips = workout.tips
                                        }
                                    ) {
                                        Text("Edit")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD WORKOUT DIALOG
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add new workout") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Workout name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMuscleGroup,
                        onValueChange = { newMuscleGroup = it },
                        label = { Text("Muscle group(s)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Description / how to do it") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTips,
                        onValueChange = { newTips = it },
                        label = { Text("Tips & cues") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val newWorkout = WorkoutInfoEntity(
                                name = newName.trim(),
                                muscleGroup = newMuscleGroup.trim(),
                                description = newDescription.trim(),
                                tips = newTips.trim()
                            )

                            scope.launch {
                                infoDao.insertAll(listOf(newWorkout))
                            }

                            // reset + close
                            newName = ""
                            newMuscleGroup = ""
                            newDescription = ""
                            newTips = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // EDIT WORKOUT DIALOG
    if (editingWorkout != null) {
        AlertDialog(
            onDismissRequest = { editingWorkout = null },
            title = { Text("Edit workout") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Workout name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editMuscleGroup,
                        onValueChange = { editMuscleGroup = it },
                        label = { Text("Muscle group(s)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description / how to do it") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTips,
                        onValueChange = { editTips = it },
                        label = { Text("Tips & cues") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val workout = editingWorkout
                        if (workout != null && editName.isNotBlank()) {
                            val updated = workout.copy(
                                name = editName.trim(),
                                muscleGroup = editMuscleGroup.trim(),
                                description = editDescription.trim(),
                                tips = editTips.trim()
                            )

                            scope.launch {
                                // Make sure your DAO has @Update fun update(workout: WorkoutInfoEntity)
                                infoDao.update(updated)
                            }

                            editingWorkout = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingWorkout = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
