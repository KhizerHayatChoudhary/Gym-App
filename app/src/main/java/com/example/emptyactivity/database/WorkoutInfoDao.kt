package com.example.emptyactivity.database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutInfoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(workouts: List<WorkoutInfoEntity>)

    @Query("SELECT * FROM workout_info ORDER BY name ASC")
    fun getAll(): Flow<List<WorkoutInfoEntity>>

    @Query("SELECT COUNT(*) FROM workout_info")
    suspend fun getCount(): Int

    @Update
    suspend fun update(workout: WorkoutInfoEntity)
}