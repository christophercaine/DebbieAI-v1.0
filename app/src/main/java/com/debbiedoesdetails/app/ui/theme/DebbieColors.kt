package com.debbiedoesdetails.app.ui.theme

import androidx.compose.ui.graphics.Color

// ===== DEBBIE AI BRAND COLORS =====

// Primary Brand Colors
val DebbieBlue = Color(0xFF1E5AA8)
val DebbieRed = Color(0xFFD32F2F)
val DebbieWhite = Color(0xFFFFFFFF)
val DebbieGold = Color(0xFFFFB300)

// Light variants
val DebbieBlueLght = Color(0xFF4A7FC4)
val DebbieRedLight = Color(0xFFE57373)
val DebbieGoldLight = Color(0xFFFFCC4D)

// Dark variants
val DebbieBlueDark = Color(0xFF0D3A6E)
val DebbieRedDark = Color(0xFF9A0007)
val DebbieGoldDark = Color(0xFFC68400)

// Background colors
val DebbieBackground = Color(0xFFF5F7FA)
val DebbieSurface = Color(0xFFFFFFFF)
val DebbieCardBackground = Color(0xFFFAFAFA)

// ===== CONTACT TYPE COLORS =====

val TypeCustomer = Color(0xFF4CAF50)      // Green
val TypeLead = Color(0xFFFF9800)          // Orange
val TypeVendor = Color(0xFF9C27B0)        // Purple
val TypeSubcontractor = Color(0xFF2196F3) // Blue
val TypeEmployee = Color(0xFF00BCD4)      // Cyan
val TypePersonal = Color(0xFF607D8B)      // Gray

// ===== TAG COLORS =====

val TagVIP = Color(0xFFFFB300)            // Gold
val TagFollowUp = Color(0xFFFF9800)       // Orange
val TagUrgent = Color(0xFFD32F2F)         // Red
val TagPending = Color(0xFF9C27B0)        // Purple
val TagPaid = Color(0xFF4CAF50)           // Green
val TagReferral = Color(0xFF2196F3)       // Blue
val TagCallback = Color(0xFFFF5722)       // Deep Orange
val TagEstimateSent = Color(0xFF00BCD4)   // Cyan
val TagNewLead = Color(0xFF8BC34A)        // Light Green
val TagHotLead = Color(0xFFE91E63)        // Pink
val TagColdLead = Color(0xFF607D8B)       // Blue Gray
val TagDoNotContact = Color(0xFF795548)   // Brown

// ===== UTILITY MAPS =====

val ContactTypeColors = mapOf(
    "Customer" to TypeCustomer,
    "Lead" to TypeLead,
    "Vendor" to TypeVendor,
    "Subcontractor" to TypeSubcontractor,
    "Employee" to TypeEmployee,
    "Personal" to TypePersonal
)

val TagColors = mapOf(
    "VIP" to TagVIP,
    "Follow Up" to TagFollowUp,
    "Urgent" to TagUrgent,
    "Pending" to TagPending,
    "Paid" to TagPaid,
    "Referral" to TagReferral,
    "Callback" to TagCallback,
    "Estimate Sent" to TagEstimateSent,
    "New Lead" to TagNewLead,
    "Hot Lead" to TagHotLead,
    "Cold Lead" to TagColdLead,
    "Do Not Contact" to TagDoNotContact
)

// ===== HELPER FUNCTIONS =====

fun getTagColor(tag: String): Color = TagColors[tag] ?: DebbieBlue

fun getTypeColor(type: String): Color = ContactTypeColors[type] ?: TypePersonal

// ===== PREDEFINED LISTS =====

val QuickTags = listOf(
    "VIP", "Follow Up", "Urgent", "Pending", "Paid", "Referral",
    "Callback", "Estimate Sent", "New Lead", "Hot Lead", "Cold Lead", "Do Not Contact"
)

val ContactTypes = listOf(
    "Personal", "Customer", "Lead", "Vendor", "Subcontractor", "Employee"
)
