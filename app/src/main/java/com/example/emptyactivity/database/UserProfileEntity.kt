package com.example.emptyactivity.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This file defines what a user profile looks like in our database.
 * Stores user's personal information like weight, height, age, and fitness goals.
 */

/**
 * Represents a user's profile information in the database.
 *
 * @property id Unique identifier for each profile (automatically generated)
 * @property username The username this profile belongs to (must be unique)
 * @property age The user's age
 * @property height Height in centimeters
 * @property weight Weight in kilograms (base unit, converted based on preference)
 * @property goal The user's fitness goal (e.g., "Build Muscle", "Lose Weight", "Maintain")
 * @property useKg Whether to use kg (true) or lbs (false) for weight display
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)  // Database automatically creates unique IDs
    val id: Int = 0,
    val username: String,  // Links to the user account
    val age: Int = 0,      // User's age
    val height: Int = 0,   // Height in centimeters
    val weight: Double = 0.0,  // Weight in kilograms (always stored in kg)
    val goal: String = "",  // Fitness goal
    val useKg: Boolean = true  // Weight unit preference (true = kg, false = lbs)
)