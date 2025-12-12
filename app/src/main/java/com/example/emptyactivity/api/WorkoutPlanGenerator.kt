package com.example.emptyactivity.api

import kotlinx.coroutines.delay

/**
 * This generates personalized workout plans based on the user's fitness goal.
 * It creates different plans for building muscle, losing weight, or maintaining fitness.
 */

/**
 * Generates a workout plan based on the user's goal.
 * 
 * @param goal What the user wants to achieve (e.g., "Build Muscle", "Lose Weight")
 * @param age User's age (not currently used, but kept for future use)
 * @param height User's height in cm (not currently used, but kept for future use)
 * @param weight User's weight in kg (not currently used, but kept for future use)
 * @return A workout plan with exercises, sets, reps, and instructions
 */
class WorkoutPlanGenerator {
    
    suspend fun generatePlan(
        goal: String,
        age: Int,
        height: Int,
        weight: Double
    ): WorkoutPlan {
        // Small delay to make it feel like it's thinking
        delay(1000)
        
        // Match the goal to the right workout plan
        return when (goal.lowercase()) {
            "build muscle", "muscle", "gain muscle", "bulk" -> {
                createMuscleBuildingPlan()
            }
            "lose weight", "weight loss", "cut", "fat loss" -> {
                createWeightLossPlan()
            }
            "maintain", "maintenance", "stay fit" -> {
                createMaintenancePlan()
            }
            else -> {
                createGeneralPlan()
            }
        }
    }
    
    /**
     * Creates a plan focused on building muscle and strength.
     */
    private fun createMuscleBuildingPlan(): WorkoutPlan {
        return WorkoutPlan(
            exercises = listOf(
                WorkoutPlanExercise(
                    name = "Bench Press",
                    sets = 4,
                    reps = 8,
                    restSeconds = 90,
                    instructions = "Focus on controlled movement, 2 seconds down, 1 second up"
                ),
                WorkoutPlanExercise(
                    name = "Squat",
                    sets = 4,
                    reps = 10,
                    restSeconds = 90,
                    instructions = "Keep your back straight, go below parallel"
                ),
                WorkoutPlanExercise(
                    name = "Deadlift",
                    sets = 3,
                    reps = 6,
                    restSeconds = 120,
                    instructions = "Keep the bar close to your body, brace your core"
                ),
                WorkoutPlanExercise(
                    name = "Overhead Press",
                    sets = 3,
                    reps = 8,
                    restSeconds = 60,
                    instructions = "Press straight up, don't lean back"
                ),
                WorkoutPlanExercise(
                    name = "Lat Pulldown",
                    sets = 3,
                    reps = 10,
                    restSeconds = 60,
                    instructions = "Pull to your upper chest, squeeze at the top"
                )
            ),
            duration = "60-75 minutes",
            frequency = "4-5 times per week"
        )
    }
    
    /**
     * Creates a plan focused on burning fat and losing weight.
     */
    private fun createWeightLossPlan(): WorkoutPlan {
        return WorkoutPlan(
            exercises = listOf(
                WorkoutPlanExercise(
                    name = "Squat",
                    sets = 3,
                    reps = 15,
                    restSeconds = 45,
                    instructions = "Higher reps for fat burning, keep form tight"
                ),
                WorkoutPlanExercise(
                    name = "Dumbbell Row",
                    sets = 3,
                    reps = 12,
                    restSeconds = 45,
                    instructions = "Controlled movement, focus on muscle contraction"
                ),
                WorkoutPlanExercise(
                    name = "Leg Press",
                    sets = 3,
                    reps = 15,
                    restSeconds = 45,
                    instructions = "Full range of motion, controlled tempo"
                ),
                WorkoutPlanExercise(
                    name = "Plank",
                    sets = 3,
                    reps = 1,
                    restSeconds = 30,
                    instructions = "Hold for 60 seconds, keep body straight"
                ),
                WorkoutPlanExercise(
                    name = "Cardio (Optional)",
                    sets = 1,
                    reps = 1,
                    restSeconds = 0,
                    instructions = "20-30 minutes of moderate intensity cardio"
                )
            ),
            duration = "45-60 minutes",
            frequency = "5-6 times per week"
        )
    }
    
    /**
     * Creates a plan for maintaining current fitness level.
     */
    private fun createMaintenancePlan(): WorkoutPlan {
        return WorkoutPlan(
            exercises = listOf(
                WorkoutPlanExercise(
                    name = "Bench Press",
                    sets = 3,
                    reps = 10,
                    restSeconds = 60,
                    instructions = "Moderate weight, focus on form"
                ),
                WorkoutPlanExercise(
                    name = "Squat",
                    sets = 3,
                    reps = 12,
                    restSeconds = 60,
                    instructions = "Maintain strength, full range of motion"
                ),
                WorkoutPlanExercise(
                    name = "Dumbbell Row",
                    sets = 3,
                    reps = 12,
                    restSeconds = 45,
                    instructions = "Balanced upper body development"
                ),
                WorkoutPlanExercise(
                    name = "Overhead Press",
                    sets = 3,
                    reps = 10,
                    restSeconds = 45,
                    instructions = "Shoulder health and strength"
                )
            ),
            duration = "45-50 minutes",
            frequency = "3-4 times per week"
        )
    }
    
    /**
     * Creates a general workout plan for any goal.
     */
    private fun createGeneralPlan(): WorkoutPlan {
        return WorkoutPlan(
            exercises = listOf(
                WorkoutPlanExercise(
                    name = "Full Body Circuit",
                    sets = 3,
                    reps = 12,
                    restSeconds = 30,
                    instructions = "Mix of compound movements for overall fitness"
                )
            ),
            duration = "30-45 minutes",
            frequency = "3-4 times per week"
        )
    }
}
