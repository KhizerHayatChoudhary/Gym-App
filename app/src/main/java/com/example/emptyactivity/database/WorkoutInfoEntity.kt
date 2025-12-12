package com.example.emptyactivity.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Static info about different workouts:
 * - name of the exercise
 * - muscle group it targets
 * - description of the movement
 * - tips & tricks
 */
@Entity(tableName = "workout_info")
data class WorkoutInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val muscleGroup: String,
    val description: String,
    val tips: String
)