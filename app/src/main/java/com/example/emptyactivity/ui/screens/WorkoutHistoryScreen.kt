package com.example.emptyactivity.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.emptyactivity.database.WorkoutDatabase
import com.example.emptyactivity.database.WorkoutEntity
import com.example.emptyactivity.utils.kgToLbs
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(currentUsername: String? = null) {

    val context = LocalContext.current
    val db = WorkoutDatabase.getDatabase(context)
    val workoutDao = db.workoutDao()
    val profileDao = db.userProfileDao()

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

    // Watch DB
    val workouts by workoutDao.getAll().collectAsState(initial = emptyList())

    //  SEARCH & FILTER STATE
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter options
    val filterOptions = listOf("All", "This Week", "This Month", "Last 30 Days")

    // FILTERED WORKOUT LIST
    val filteredWorkouts = remember(workouts, searchQuery, selectedFilter) {
        var result = workouts

        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.exercise.contains(searchQuery, ignoreCase = true)
            }
        }

        result = when (selectedFilter) {
            "This Week" -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                result.filter { it.date >= cal.timeInMillis }
            }

            "This Month" -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                result.filter { it.date >= cal.timeInMillis }
            }

            "Last 30 Days" -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -30)
                result.filter { it.date >= cal.timeInMillis }
            }

            else -> result
        }

        result
    }

    // EDIT STATE
    var editingWorkout by remember { mutableStateOf<WorkoutEntity?>(null) }
    var editSets by remember { mutableStateOf("") }
    var editReps by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title
            Text(
                text = "Workout History",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SEARCH BAR UI
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search exercises...") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FILTER UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filter: $selectedFilter")

                Box {
                    OutlinedButton(
                        onClick = { showFilterMenu = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Filter")
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFilter = option
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RESULTS COUNT
            Text(
                text = "${filteredWorkouts.size} workout${if (filteredWorkouts.size != 1) "s" else ""} found",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredWorkouts) { workout ->
                    WorkoutCard(
                        workout = workout,
                        useKg = useKg,
                        onClick = {
                            editingWorkout = workout
                            editSets = workout.sets.toString()
                            editReps = workout.reps.toString()
                            editWeight = workout.weight.toString()
                        },
                        onDelete = {
                            scope.launch {
                                workoutDao.delete(workout)
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    // EDIT DIALOG
    if (editingWorkout != null) {
        AlertDialog(
            onDismissRequest = { editingWorkout = null },
            title = {
                Text(text = "Edit Workout")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = editingWorkout!!.exercise,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatDate(editingWorkout!!.date),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editSets,
                        onValueChange = { editSets = it },
                        label = { Text("Sets") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editReps,
                        onValueChange = { editReps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editWeight,
                        onValueChange = { editWeight = it },
                        label = { Text("Weight (lbs)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val workout = editingWorkout ?: return@TextButton

                        val updated = workout.copy(
                            sets = editSets.toIntOrNull() ?: workout.sets,
                            reps = editReps.toIntOrNull() ?: workout.reps,
                            weight = editWeight.toIntOrNull() ?: workout.weight
                        )

                        scope.launch {
                            workoutDao.update(updated)
                        }
                        editingWorkout = null
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

@Composable
fun WorkoutCard(
    workout: WorkoutEntity,
    useKg: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Weight is stored in kg, convert if needed
    val displayWeight = if (useKg) {
        workout.weight.toDouble()
    } else {
        kgToLbs(workout.weight.toDouble())
    }
    val weightUnit = if (useKg) "kg" else "lbs"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }   // tap card to view/edit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = workout.exercise,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "${workout.sets} sets • ${workout.reps} reps • ${String.format("%.1f", displayWeight)} $weightUnit",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = formatDate(workout.date),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete")
            }
        }
    }
}

/**
 * Date formatter
 */
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
