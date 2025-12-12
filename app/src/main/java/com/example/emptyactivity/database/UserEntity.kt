package com.example.emptyactivity.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This file defines what a user looks like in our database.
 * Stores user account information like username and password.
 */

/**
 * Represents a user account in the database.
 * 
 * @property id Unique identifier for each user (automatically generated)
 * @property username The user's username (must be unique)
 * @property password The user's password (stored as plain text for simplicity)
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)  // Database automatically creates unique IDs
    val id: Int = 0,
    val username: String,  // User's username (should be unique)
    val password: String   // User's password
)

