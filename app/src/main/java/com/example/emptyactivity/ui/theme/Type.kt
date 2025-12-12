package com.example.emptyactivity.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * This file defines the typography (text styles) used throughout the app.
 * It sets up font sizes, weights, spacing, and other text properties.
 */

/**
 * Typography configuration for the app.
 * Defines how text looks throughout the app.
 */
val Typography = Typography(
    // Default body text style (used for most text)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,  // Use system default font
        fontWeight = FontWeight.Normal,    // Regular weight (not bold)
        fontSize = 16.sp,                  // 16 scaled pixels
        lineHeight = 24.sp,                // Space between lines
        letterSpacing = 0.5.sp             // Space between letters
    )
    /* You can customize other text styles here:
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,        // Larger text for titles
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,    // Slightly bolder
        fontSize = 11.sp,                  // Smaller text for labels
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
