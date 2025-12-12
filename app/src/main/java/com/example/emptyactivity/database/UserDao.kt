package com.example.emptyactivity.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * This file defines all the operations we can do with users in the database.
 * DAO stands for "Data Access Object" - it's like a set of tools for working with user data.
 */

/**
 * Interface that defines database operations for users.
 * Room generates the actual code to perform these operations.
 */
@Dao
interface UserDao {

    /**
     * Creates a new user account in the database.
     * If a user with the same username already exists, it will be ignored.
     * 
     * @param user The user to create
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)  // Ignore if username already exists
    suspend fun insert(user: UserEntity)

    /**
     * Updates an existing user's information (like password).
     *
     * @param user The user to update
     */
    @Update
    suspend fun update(user: UserEntity)

    /**
     * Finds a user by their username.
     * Used to check if a username already exists, or to login.
     * 
     * @param username The username to search for
     * @return The user if found, or null if not found
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username The username to check
     * @return True if username exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean
}

