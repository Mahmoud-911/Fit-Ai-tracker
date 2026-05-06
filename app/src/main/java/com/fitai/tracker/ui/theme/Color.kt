package com.fitai.tracker.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
// iOS / SwiftUI inspired palette — system blue, dark mode
// ─────────────────────────────────────────────────────────────

// iOS dark backgrounds — systemBackground hierarchy
val DarkBackground = Color(0xFF000000)        // systemBackground (dark)
val DarkSurface = Color(0xFF0B1220)            // base surface, slightly blue-tinted
val DarkCard = Color(0xFF111827)               // secondarySystemGroupedBackground
val DarkCardElevated = Color(0xFF1B2436)       // tertiarySystemGroupedBackground

// iOS systemBlue (dark) and supporting blues — the primary identity
val IosBlue = Color(0xFF0A84FF)                // systemBlue (dark)
val IosBlueLight = Color(0xFF409CFF)           // softened tint
val IosBlueDark = Color(0xFF0060D0)            // pressed / deep accent
val IosCyan = Color(0xFF64D2FF)                // systemCyan (dark)
val IosTeal = Color(0xFF40CBE0)                // systemTeal (dark)
val IosIndigo = Color(0xFF5E5CE6)              // systemIndigo (dark)
val IosMint = Color(0xFF66D4CF)                // systemMint (dark)

// Backwards-compatible aliases used across the codebase.
// They now point at the iOS blue family so existing screens
// pick up the new look automatically.
val GojoViolet = IosBlue
val GojoVioletLight = IosBlueLight
val GojoVioletDark = IosBlueDark
val GojoWhite = Color(0xFFEAF2FF)

// "Domain" / cyan accent
val DomainBlue = IosCyan
val DomainBlueDark = Color(0xFF0099CC)

// Secondary character accents — kept for variety, retuned to blue family
val YujiPink = IosTeal                          // was hot pink, now a friendly teal
val YujiPinkLight = Color(0xFF8DE2EE)

val MegumiDark = Color(0xFF1E2A4A)
val NobaraBrown = IosIndigo                     // was warm brown, now indigo

// Status colors — iOS systemRed / Green / Orange / Yellow (dark variants)
val CursedRed = Color(0xFFFF453A)
val CursedRedDark = Color(0xFFC9261B)
val SuccessGreen = Color(0xFF30D158)
val WarningAmber = Color(0xFFFFD60A)

// Macro / nutrition palette — re-tuned around the blue identity
val CalorieOrange = Color(0xFFFF9F0A)           // iOS systemOrange (dark)
val CarbsBlue = IosCyan                         // carbs share the cyan family
val ProteinGreen = SuccessGreen
val FatYellow = Color(0xFFFFD60A)               // iOS systemYellow (dark)

// Text — iOS label hierarchy (dark mode)
val TextPrimary = Color(0xFFFFFFFF)             // primary label
val TextSecondary = Color(0xFFA0AEC8)           // secondary label
val TextTertiary = Color(0xFF6C7793)            // tertiary label

// Misc / ambient
val FieldGreen = Color(0xFF1A6E3C)
val FieldGreenLight = Color(0xFF2A9D5C)

// Background gradient stops for hero areas — soft midnight blue
val GradientStart = Color(0xFF02070F)
val GradientMid = Color(0xFF071226)
val GradientEnd = Color(0xFF000000)

// Subtle hairline / separator (iOS opaqueSeparator)
val IosSeparator = Color(0x33A0AEC8)
