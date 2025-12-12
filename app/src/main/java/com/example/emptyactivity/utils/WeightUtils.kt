package com.example.emptyactivity.utils

/**
 * Utility functions for converting between weight units (kg and lbs).
 * All weights are stored in kg in the database, but can be displayed in lbs.
 */

/**
 * Converts kilograms to pounds.
 *
 * @param kg Weight in kilograms
 * @return Weight in pounds
 */
fun kgToLbs(kg: Double): Double {
    return kg * 2.20462
}

/**
 * Converts pounds to kilograms.
 *
 * @param lbs Weight in pounds
 * @return Weight in kilograms
 */
fun lbsToKg(lbs: Double): Double {
    return lbs / 2.20462
}

/**
 * Formats weight for display based on user preference.
 *
 * @param weightInKg Weight stored in kg (database format)
 * @param useKg Whether to display in kg (true) or lbs (false)
 * @return Formatted weight string with unit
 */
fun formatWeight(weightInKg: Double, useKg: Boolean): String {
    return if (useKg) {
        String.format("%.1f kg", weightInKg)
    } else {
        String.format("%.1f lbs", kgToLbs(weightInKg))
    }
}

/**
 * Gets the weight value for display (converted if needed).
 *
 * @param weightInKg Weight stored in kg (database format)
 * @param useKg Whether to display in kg (true) or lbs (false)
 * @return Weight value in the preferred unit
 */
fun getDisplayWeight(weightInKg: Double, useKg: Boolean): Double {
    return if (useKg) weightInKg else kgToLbs(weightInKg)
}

/**
 * Converts user input weight to kg for storage.
 *
 * @param inputWeight Weight entered by user
 * @param useKg Whether the input is in kg (true) or lbs (false)
 * @return Weight in kg for database storage
 */
fun convertToKg(inputWeight: Double, useKg: Boolean): Double {
    return if (useKg) inputWeight else lbsToKg(inputWeight)
}