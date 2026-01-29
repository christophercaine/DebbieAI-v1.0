package com.debbiedoesit.debbieai.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ============================================================================
// DEBBIE BRAND COLORS (from logo)
// ============================================================================

// Primary: Debbie Blue (logo text, main UI elements)
val DebbieBlue = Color(0xFF3B5998)
val DebbieBlueLight = Color(0xFF5C7BC0)
val DebbieBlueDark = Color(0xFF1A3A6E)

// Accent: Debbie Red (shirt, banner, CTAs)
val DebbieRed = Color(0xFFC62828)
val DebbieRedLight = Color(0xFFE15353)
val DebbieRedDark = Color(0xFF8E0000)

// Secondary: Debbie Teal (glasses, accents)
val DebbieTeal = Color(0xFF00796B)
val DebbieTealLight = Color(0xFF48A999)
val DebbieTealDark = Color(0xFF004C40)

// Supporting colors
val DebbieOrange = Color(0xFFFF8F00)
val DebbieOrangeLight = Color(0xFFFFBF47)
val DebbieGreen = Color(0xFF43A047)
val DebbieGreenLight = Color(0xFF76D275)
val DebbiePurple = Color(0xFF7B1FA2)
val DebbiePurpleLight = Color(0xFFAE52D4)
val DebbieBrown = Color(0xFF6D4C41)
val DebbieGray = Color(0xFF757575)

// ============================================================================
// MODULE COLORS (each app has a signature color)
// ============================================================================

object DebbieModuleColors {
    val contacts = DebbieTeal       // 👥 CRM green
    val photos = DebbieBlue         // 📸 Photo blue
    val details = DebbieOrange      // 📋 Task orange
    val drafting = DebbiePurple     // 📐 Estimate purple
    val data = DebbieGreen          // 🔍 AI green
    val jobs = DebbieRed            // 💼 Jobs red
}

// ============================================================================
// PHOTO CATEGORY COLORS
// ============================================================================

object PhotoCategoryColors {
    val before = Color(0xFFFF9800)      // Orange
    val during = Color(0xFF2196F3)      // Blue
    val after = Color(0xFF4CAF50)       // Green
    val damage = Color(0xFFF44336)      // Red
    val materials = Color(0xFF795548)   // Brown
    val measurement = Color(0xFF9C27B0) // Purple
    val receipt = Color(0xFF607D8B)     // Blue Gray
    val reference = Color(0xFF00BCD4)   // Cyan
    val general = Color(0xFF9E9E9E)     // Gray
    
    fun forCategory(category: String): Color = when (category.uppercase()) {
        "BEFORE" -> before
        "DURING" -> during
        "AFTER" -> after
        "DAMAGE" -> damage
        "MATERIALS" -> materials
        "MEASUREMENT" -> measurement
        "RECEIPT" -> receipt
        "REFERENCE" -> reference
        else -> general
    }
}

// ============================================================================
// JOB STATUS COLORS
// ============================================================================

object JobStatusColors {
    val lead = Color(0xFF9E9E9E)        // Gray
    val quoted = Color(0xFF2196F3)      // Blue
    val scheduled = Color(0xFF00BCD4)   // Cyan
    val inProgress = Color(0xFFFF9800)  // Orange
    val onHold = Color(0xFFFFEB3B)      // Yellow
    val completed = Color(0xFF4CAF50)   // Green
    val invoiced = Color(0xFF9C27B0)    // Purple
    val paid = Color(0xFF43A047)        // Dark Green
    val cancelled = Color(0xFFF44336)   // Red
    
    fun forStatus(status: String): Color = when (status.uppercase()) {
        "LEAD" -> lead
        "QUOTED" -> quoted
        "SCHEDULED" -> scheduled
        "IN_PROGRESS" -> inProgress
        "ON_HOLD" -> onHold
        "COMPLETED" -> completed
        "INVOICED" -> invoiced
        "PAID" -> paid
        "CANCELLED" -> cancelled
        else -> lead
    }
}

// ============================================================================
// TASK PRIORITY COLORS
// ============================================================================

object TaskPriorityColors {
    val low = Color(0xFF9E9E9E)
    val normal = Color(0xFF2196F3)
    val high = Color(0xFFFF9800)
    val urgent = Color(0xFFF44336)
    
    fun forPriority(priority: String): Color = when (priority.uppercase()) {
        "LOW" -> low
        "NORMAL" -> normal
        "HIGH" -> high
        "URGENT" -> urgent
        else -> normal
    }
}

// ============================================================================
// LIGHT COLOR SCHEME
// ============================================================================

private val DebbieAILightColors = lightColorScheme(
    // Primary (Blue)
    primary = DebbieBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = DebbieBlueDark,
    
    // Secondary (Teal)
    secondary = DebbieTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = DebbieTealDark,
    
    // Tertiary (Red for accents)
    tertiary = DebbieRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFCDD2),
    onTertiaryContainer = DebbieRedDark,
    
    // Error
    error = DebbieRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Background & Surface
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    // Outline
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

// ============================================================================
// DARK COLOR SCHEME
// ============================================================================

private val DebbieAIDarkColors = darkColorScheme(
    // Primary (Blue)
    primary = DebbieBlueLight,
    onPrimary = DebbieBlueDark,
    primaryContainer = DebbieBlue,
    onPrimaryContainer = Color(0xFFD4E3FF),
    
    // Secondary (Teal)
    secondary = DebbieTealLight,
    onSecondary = DebbieTealDark,
    secondaryContainer = DebbieTeal,
    onSecondaryContainer = Color(0xFFB2DFDB),
    
    // Tertiary (Red)
    tertiary = DebbieRedLight,
    onTertiary = DebbieRedDark,
    tertiaryContainer = DebbieRed,
    onTertiaryContainer = Color(0xFFFFCDD2),
    
    // Error
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    // Background & Surface
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    // Outline
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

// ============================================================================
// TYPOGRAPHY
// ============================================================================

val DebbieTypography = Typography(
    // Display
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    
    // Headline
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    
    // Title
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================================================
// THEME COMPOSABLE
// ============================================================================

@Composable
fun DebbieAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DebbieAIDarkColors else DebbieAILightColors
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DebbieTypography,
        content = content
    )
}

// ============================================================================
// HELPER EXTENSIONS
// ============================================================================

// Get color for any category string
@Composable
fun String.categoryColor(): Color = PhotoCategoryColors.forCategory(this)

// Get color for any job status string
@Composable
fun String.jobStatusColor(): Color = JobStatusColors.forStatus(this)

// Get color for any priority string
@Composable
fun String.priorityColor(): Color = TaskPriorityColors.forPriority(this)
