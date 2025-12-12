package com.example.emptyactivity.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * This file defines all the operations we can do with workouts in the database.
 * DAO stands for "Data Access Object" - it's like a set of tools for working with database data.
 * 
 * Think of it like this:
 * - WorkoutEntity = the blueprint of what a workout is
 * - WorkoutDao = the tools to save, get, and delete workouts
 * - WorkoutDatabase = the actual database that stores everything
 */

/**
 * Interface that defines database operations for workouts.
 * Room generates the actual code to perform these operations.
 */
@Dao
interface WorkoutDao {

    /**
     * Saves a new workout to the database.
     * 
     * @param workout The workout to save
     */
    @Insert
    suspend fun insert(workout: WorkoutEntity)  // suspend = runs in background, doesn't block UI

    /**
     * Gets all workouts from the database, ordered by date (newest first).
     * Returns a Flow, which means the UI automatically updates when workouts change.
     * 
     * @return A Flow (stream) of workout lists that updates in real-time
     */
    @Query("SELECT * FROM workouts ORDER BY date DESC")  // SQL query to get all workouts
    fun getAll(): Flow<List<WorkoutEntity>>

    /**
     * Deletes a workout from the database.
     * 
     * @param workout The workout to delete
     */
    @Delete
    suspend fun delete(workout: WorkoutEntity)  // suspend = runs in background, doesn't block UI
    @Update
    suspend fun update(workout: WorkoutEntity)
}
