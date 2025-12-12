package com.example.emptyactivity.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database class that holds all workout data and user accounts.
 * It uses the Room library to create a SQLite database on the device.
 */
    @Database(
        entities = [
            WorkoutEntity::class,
            UserEntity::class,
            WorkoutInfoEntity::class,   // Static workout info table
            UserProfileEntity::class    // User profile information
        ],
        version = 5,                   // ️ BUMPED VERSION for new entity
        exportSchema = false
    )
    abstract class WorkoutDatabase : RoomDatabase() {

        // DAOs to access each table
        abstract fun workoutDao(): WorkoutDao
        abstract fun userDao(): UserDao
        abstract fun workoutInfoDao(): WorkoutInfoDao
        abstract fun userProfileDao(): UserProfileDao  //  NEW DAO for profiles

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_databasev2"  // Database file name on the device
                )
                    // If schema changes, clear and recreate DB (ok for a school project)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
