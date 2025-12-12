package com.example.emptyactivity.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * This file defines all the operations we can do with user profiles in the database.
 * DAO stands for "Data Access Object" - it's like a set of tools for working with profile data.
 */

/**
 * Interface that defines database operations for user profiles.
 * Room generates the actual code to perform these operations.
 */
@Dao
interface UserProfileDao {

    /**
     * Creates or updates a user profile in the database.
     * If a profile with the same username already exists, it will be replaced.
     * 
     * @param profile The profile to save
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Replace if username already exists
    suspend fun insert(profile: UserProfileEntity)

    /**
     * Updates an existing user profile.
     * 
     * @param profile The profile to update
     */
    @Update
    suspend fun update(profile: UserProfileEntity)

    /**
     * Finds a user profile by their username.
     * Used to get profile information for the logged-in user.
     * 
     * @param username The username to search for
     * @return The profile if found, or null if not found
     */
    @Query("SELECT * FROM user_profiles WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserProfileEntity?

    /**
     * Checks if a profile exists for a username.
     * 
     * @param username The username to check
     * @return True if profile exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM user_profiles WHERE username = :username)")
    suspend fun profileExists(username: String): Boolean
}

