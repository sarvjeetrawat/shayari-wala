package com.kunpitech.shayariwala.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.kunpitech.shayariwala.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// Playfair Display — for shayari / headings (serif, poetic)
val PlayfairDisplay = FontFamily(
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.Normal,  style = FontStyle.Italic),
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.Bold),
)

// DM Sans — for UI labels, buttons, metadata
val DmSans = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.SemiBold),
)

// Noto Nastaliq Urdu — for Urdu script rendering
val NotoNastaliq = FontFamily(
    Font(googleFont = GoogleFont("Noto Nastaliq Urdu"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Noto Nastaliq Urdu"), fontProvider = provider, weight = FontWeight.Bold),
)

val ShayariTypography = Typography(
    // Display — app title "Shayari ✦"
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize   = 36.sp,
        lineHeight = 44.sp,
        color      = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        color      = TextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
        color      = TextPrimary
    ),

    // Headlines — screen titles, section headers
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 28.sp,
        color      = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize   = 18.sp,
        lineHeight = 26.sp,
        color      = TextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = TextPrimary
    ),

    // Title — card titles, poet names
    titleLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 17.sp,
        lineHeight = 24.sp,
        color      = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 22.sp,
        color      = TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 20.sp,
        color      = TextSecondary
    ),

    // Body — shayari lines, descriptions
    bodyLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontStyle  = FontStyle.Italic,
        fontSize   = 16.sp,
        lineHeight = 28.sp,
        color      = TextSecondary
    ),
    bodyMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontStyle  = FontStyle.Italic,
        fontSize   = 14.sp,
        lineHeight = 24.sp,
        color      = TextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
        color      = TextMuted
    ),

    // Label — chips, badges, counts, nav labels
    labelLarge = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
        color      = TextMuted
    ),
    labelMedium = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color      = TextDisabled
    ),
    labelSmall = TextStyle(
        fontFamily = DmSans,
        fontWeight = FontWeight.Normal,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp,
        color      = TextDisabled
    ),
)

// ── Extra styles not in M3 Typography ─────────────────────

// Full shayari on detail screen — larger, more breath
val ShayariDetailStyle = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Normal,
    fontStyle  = FontStyle.Italic,
    fontSize   = 18.sp,
    lineHeight = 34.sp,
    color      = TextPrimary
)

// Urdu script style
val UrduTextStyle = TextStyle(
    fontFamily  = NotoNastaliq,
    fontWeight  = FontWeight.Normal,
    fontSize    = 15.sp,
    lineHeight  = 30.sp,
    color       = TextMuted,
    textAlign   = androidx.compose.ui.text.style.TextAlign.End
)

// Category tag — "ISHQ · TRENDING"
val CategoryTagStyle = TextStyle(
    fontFamily    = DmSans,
    fontWeight    = FontWeight.Medium,
    fontSize      = 10.sp,
    lineHeight    = 14.sp,
    letterSpacing = 0.8.sp,
    color         = Gold400
)

// Poet attribution — "— Mirza Ghalib"
val PoetNameStyle = TextStyle(
    fontFamily = DmSans,
    fontWeight = FontWeight.Normal,
    fontSize   = 12.sp,
    lineHeight = 18.sp,
    color      = TextDisabled
)