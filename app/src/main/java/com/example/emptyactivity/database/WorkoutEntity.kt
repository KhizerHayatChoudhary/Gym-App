package com.example.emptyactivity.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This file defines what a workout looks like in our database.
 * It's like a blueprint for storing workout information.
 * 
 * Room database uses this to create a table called "workouts" that stores all workout data.
 */

/**
 * Represents a single workout entry in the database.
 * 
 * @property id Unique identifier for each workout (automatically generated)
 * @property exercise Name of the exercise (e.g., "Bench Press", "Squats")
 * @property reps Number of repetitions performed
 * @property weight Weight lifted in pounds
 * @property date Timestamp (in milliseconds) when the workout was logged
 */
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)  // Database automatically creates unique IDs
    val id: Int = 0,
    val exercise: String,  // Name of the exercise
    val sets: Int,         // Number of sets
    val reps: Int,         // Number of repetitions
    val weight: Int,       // Weight in pounds
    val date: Long         // Timestamp when workout was logged
)
