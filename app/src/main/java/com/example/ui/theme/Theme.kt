package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Brush

private val DarkColorScheme = darkColorScheme(
    primary = LavenderDarkPrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderDarkPrimaryContainer,
    onPrimaryContainer = LavenderDarkOnPrimaryContainer,
    secondary = AmberDarkSecondary,
    onSecondary = Color(0xFF78350F),
    secondaryContainer = AmberDarkSecondaryContainer,
    onSecondaryContainer = AmberDarkOnSecondaryContainer,
    tertiary = LavenderAccent,
    onTertiary = Color.White,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderPrimaryContainer,
    onPrimaryContainer = LavenderOnPrimaryContainer,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = AmberSecondaryContainer,
    onSecondaryContainer = AmberOnSecondaryContainer,
    tertiary = LavenderAccent,
    onTertiary = Color.White,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),
    onBackground = SalesPilotTextPrimary,
    onSurface = SalesPilotTextPrimary
)

@Composable
fun salesPilotTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF111827),
    unfocusedTextColor = Color(0xFF111827),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = LavenderPrimary,
    unfocusedBorderColor = Color(0xFF94A3B8),
    focusedLabelColor = LavenderPrimary,
    unfocusedLabelColor = Color(0xFF1E293B),
    focusedPlaceholderColor = Color(0xFF64748B),
    unfocusedPlaceholderColor = Color(0xFF64748B),
    cursorColor = LavenderPrimary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorLabelColor = MaterialTheme.colorScheme.error,
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)

/**
 * Centralized Gradient Brushes across SalesPilot AI.
 * Derived from the Welcome / Landing Screen design language.
 */
object SalesPilotThemeGradients {
    // Primary brand action gradient for CTAs, FABs, and prominent hero badges
    val primaryHorizontal = Brush.horizontalGradient(
        listOf(LavenderPrimary, LavenderAccent)
    )

    // Deep luminous purple hero gradient for screen headers and executive cards
    val heroVertical = Brush.verticalGradient(
        listOf(Color(0xFF4C1D95), LavenderPrimary, LavenderAccent)
    )

    // Full-screen atmospheric backdrop gradient (soft lavender tint to crisp white to slate)
    val screenBackground = Brush.verticalGradient(
        listOf(LavenderSurfaceLight, Color.White, Color(0xFFF8FAFC))
    )

    // Subtle card surface gradient
    val cardSurface = Brush.verticalGradient(
        listOf(Color(0xFFFAF5FF), Color(0xFFFFFFFF))
    )

    // Stat card luminous background
    val statCardLuminous = Brush.linearGradient(
        listOf(Color(0xFFEDE9FE).copy(alpha = 0.5f), Color.White)
    )

    // Festive Warm Gold gradient for Indian retail offers and pipeline values
    val goldHorizontal = Brush.horizontalGradient(
        listOf(AmberSecondary, Color(0xFFF59E0B))
    )

    // Active live / WhatsApp / success gradient
    val emeraldHorizontal = Brush.horizontalGradient(
        listOf(Color(0xFF047857), Color(0xFF10B981))
    )
}

@Composable
fun SalesPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = SalesPilotTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)

