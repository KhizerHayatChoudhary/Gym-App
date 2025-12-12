package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.database.WorkoutEntity
import com.example.emptyactivity.database.WorkoutInfoEntity
import com.example.emptyactivity.utils.convertToKg
import com.example.emptyactivity.utils.lbsToKg
import kotlinx.coroutines.launch

/**
 * Screen where users can add a new workout.
 * Users can select an exercise, see form instructions, and log their sets, reps, and weight.
 *
 * @param currentUsername The username of the logged-in user (to access their workout plan)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(currentUsername: String? = null) {
    val context = LocalContext.current
    val db = WorkoutDatabase.getDatabase(context)
    val workoutDao = db.workoutDao()
    val workoutInfoDao = db.workoutInfoDao()
    val profileDao = db.userProfileDao()

    // For running database operations in the background
    val scope = rememberCoroutineScope()

    // Get user's weight unit preference
    var useKg by remember { mutableStateOf(true) }

    // Load user preference
    LaunchedEffect(currentUsername) {
        if (currentUsername != null) {
            scope.launch {
                val profile = profileDao.findByUsername(currentUsername)
                if (profile != null) {
                    useKg = profile.useKg
                }
            }
        }
    }

    // Seed exercises if database is empty (same as InfoScreen)
    LaunchedEffect(Unit) {
        scope.launch {
            val count = workoutInfoDao.getCount()
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
                        tips = "Don't swing or jerk the weight, pull with your elbows, and control the bar back up."
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
                        tips = "Don't swing the weights, control the movement, and squeeze at the top."
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
                        tips = "Don't lock out your knees completely and keep your lower back pressed into the seat."
                    ),
                    WorkoutInfoEntity(
                        name = "Plank",
                        muscleGroup = "Core",
                        description = "Hold a straight-body position on your elbows or hands, keeping your core tight and body in a straight line.",
                        tips = "Avoid letting your hips sag or pike up; focus on breathing and bracing your core."
                    )
                )
                workoutInfoDao.insertAll(defaultWorkouts)
            }
        }
    }

    // Get list of available exercises for selection (from WorkoutInfoEntity)
    val availableExercises by workoutInfoDao.getAll().collectAsState(initial = emptyList())

    // Text field state variables
    var exercise by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // State for showing exercise form
    var selectedExerciseInfo by remember { mutableStateOf<WorkoutInfoEntity?>(null) }
    var showExerciseForm by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Main screen layout (scrollable)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Add Workout",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Log your workout progress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise selection dropdown (shows if we have any exercises)
        if (availableExercises.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Exercise",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Exercise dropdown
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = exercise,
                            onValueChange = { input ->
                                if (input.all { it.isLetter() || it.isWhitespace() }) {
                                    exercise = input
                                    selectedExerciseInfo = null   // Hides the button if user types manually
                                }
                            },
                            label = { Text("Exercise") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            placeholder = { Text("Choose an exercise") },
                            trailingIcon = {
                                DropdownMenuIcon(expanded) { expanded = it }
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Show all exercises from WorkoutInfoEntity
                            availableExercises.forEach { exerciseInfo ->
                                DropdownMenuItem(
                                    text = { Text(exerciseInfo.name) },
                                    onClick = {
                                        exercise = exerciseInfo.name
                                        selectedExerciseInfo = exerciseInfo
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Show form button if exercise is selected
                    if (selectedExerciseInfo != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showExerciseForm = !showExerciseForm },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (showExerciseForm) "Hide Form" else "Show Form")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Show exercise form instructions if selected
        if (showExerciseForm && selectedExerciseInfo != null) {
            ExerciseFormCard(exerciseInfo = selectedExerciseInfo!!)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Input fields (now includes a dropdown of all workouts from DB)
        WorkoutInputFields(
            exercise = exercise,
            sets = sets,
            reps = reps,
            weight = weight,
            useKg = useKg,
            availableExercises = availableExercises,
            onExerciseSelected = { info ->
                exercise = info.name
                selectedExerciseInfo = info
            },
            onExerciseChange = { exercise = it },
            onSetsChange = { sets = it },
            onRepsChange = { reps = it },
            onWeightChange = { weight = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success message
        if (successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = successMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Button to save workout into database
        Button(
            onClick = {
                // Only save if required fields have text
                if (exercise.isNotBlank() && reps.isNotBlank() && weight.isNotBlank()) {
                    val weightDouble = weight.toDoubleOrNull()

                    if (weightDouble != null && weightDouble > 0) {
                        // Convert weight to kg for storage
                        val weightInKg = convertToKg(weightDouble, useKg)

                        val workout = WorkoutEntity(
                            exercise = exercise,
                            sets = sets.toIntOrNull() ?: 0,
                            reps = reps.toIntOrNull() ?: 0,
                            weight = weightInKg.toInt(),  // Store in kg
                            date = System.currentTimeMillis()
                        )

                        scope.launch {
                            workoutDao.insert(workout)

                            // Clear fields after saving
                            exercise = ""
                            sets = ""
                            reps = ""
                            weight = ""
                            selectedExerciseInfo = null
                            showExerciseForm = false
                            successMessage = "Workout saved successfully!"

                            // Clear success message after 3 seconds
                            kotlinx.coroutines.delay(3000)
                            successMessage = null
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Workout", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message to the user
        Text(
            text = "Your workouts are shown on the History page.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Shows all the input fields for entering workout details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutInputFields(
    exercise: String,
    sets: String,
    reps: String,
    weight: String,
    useKg: Boolean,
    availableExercises: List<WorkoutInfoEntity>,
    onExerciseSelected: (WorkoutInfoEntity) -> Unit,
    onExerciseChange: (String) -> Unit,
    onSetsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Workout Details",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (availableExercises.isNotEmpty()) {
                // 🔽 Proper Material 3 exposed dropdown
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = exercise,
                        onValueChange = { /* readOnly when using DB list */ },
                        readOnly = true,
                        label = { Text("Exercise") },
                        placeholder = { Text("Select exercise from database") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableExercises.forEach { exerciseInfo ->
                            DropdownMenuItem(
                                text = { Text(exerciseInfo.name) },
                                onClick = {
                                    onExerciseSelected(exerciseInfo)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                // Fallback if DB is empty for some reason
                OutlinedTextField(
                    value = exercise,
                    onValueChange = onExerciseChange,
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type exercise name") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = sets,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        onSetsChange(input)
                    }
                },
                label = { Text("Sets") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = reps,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        onRepsChange(input)
                    }
                },
                label = { Text("Reps") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { input ->
                    if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        onWeightChange(input)
                    }
                },
                label = { Text("Weight (${if (useKg) "kg" else "lbs"})") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
/**
 * Shows how to properly perform the selected exercise.
 */
@Composable
fun ExerciseFormCard(exerciseInfo: WorkoutInfoEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How to do ${exerciseInfo.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Muscle Groups: ${exerciseInfo.muscleGroup}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Description:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = exerciseInfo.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tips:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = exerciseInfo.tips,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Helper composable for dropdown menu icon.
 */
@Composable
private fun DropdownMenuIcon(expanded: Boolean, onExpandedChange: (Boolean) -> Unit) {
    // Icon is handled by TextField's trailingIcon automatically
    // Parameters are kept for consistency with usage
}