package com.example.emptyactivity.api

/**
 * This file defines the data structures for workout plans.
 * These are used to display workout information to the user.
 */

/**
 * Represents a complete workout plan with multiple exercises.
 * 
 * @property exercises List of exercises in the plan
 * @property duration How long each workout session should take
 * @property frequency How often to do the workouts
 */
data class WorkoutPlan(
    val exercises: List<WorkoutPlanExercise>,
    val duration: String,
    val frequency: String
)

/**
 * Represents a single exercise in a workout plan.
 * 
 * @property name Name of the exercise
 * @property sets Number of sets to do
 * @property reps Number of repetitions per set
 * @property restSeconds How long to rest between sets (in seconds)
 * @property instructions How to perform the exercise correctly
 */
data class WorkoutPlanExercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String
)
