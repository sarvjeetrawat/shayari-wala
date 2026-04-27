package com.kunpitech.shayariwala.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Custom extra colours not covered by M3 slots ─────────
@Immutable
data class ShayariExtendedColors(
    val cardBackground   : Color,
    val cardBorder       : Color,
    val iconButtonBg     : Color,
    val bottomNavBg      : Color,
    val divider          : Color,
    val chipActive       : Color,
    val chipActiveFg     : Color,
    val chipInactive     : Color,
    val chipInactiveFg   : Color,
    val accentGold       : Color,
    val accentGoldSubtle : Color,
    val ishq             : Color,
    val ishqDim          : Color,
    val dard             : Color,
    val dardDim          : Color,
    val zindagi          : Color,
    val zindagiDim       : Color,
    val khushi           : Color,
    val khushiDim        : Color,
    val judai            : Color,
    val judaiDim         : Color,
    val wafa             : Color,
    val wafaDim          : Color,
)

val LocalShayariColors = staticCompositionLocalOf {
    ShayariExtendedColors(
        cardBackground   = Color.Unspecified,
        cardBorder       = Color.Unspecified,
        iconButtonBg     = Color.Unspecified,
        bottomNavBg      = Color.Unspecified,
        divider          = Color.Unspecified,
        chipActive       = Color.Unspecified,
        chipActiveFg     = Color.Unspecified,
        chipInactive     = Color.Unspecified,
        chipInactiveFg   = Color.Unspecified,
        accentGold       = Color.Unspecified,
        accentGoldSubtle = Color.Unspecified,
        ishq             = Color.Unspecified,
        ishqDim          = Color.Unspecified,
        dard             = Color.Unspecified,
        dardDim          = Color.Unspecified,
        zindagi          = Color.Unspecified,
        zindagiDim       = Color.Unspecified,
        khushi           = Color.Unspecified,
        khushiDim        = Color.Unspecified,
        judai            = Color.Unspecified,
        judaiDim         = Color.Unspecified,
        wafa             = Color.Unspecified,
        wafaDim          = Color.Unspecified,
    )
}

// ── M3 dark colour scheme ─────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = Gold400,
    onPrimary        = Bg900,
    primaryContainer = Gold900,
    onPrimaryContainer = Gold300,

    secondary        = DardBlue,
    onSecondary      = Bg900,
    secondaryContainer = Color(0xFF1A1E3A),
    onSecondaryContainer = DardBlue,

    tertiary         = ZindagiGreen,
    onTertiary       = Bg900,
    tertiaryContainer = Color(0xFF0F2A1E),
    onTertiaryContainer = ZindagiGreen,

    background       = Bg900,
    onBackground     = TextPrimary,

    surface          = Bg700,
    onSurface        = TextPrimary,
    surfaceVariant   = Bg600,
    onSurfaceVariant = TextMuted,

    outline          = Border100,
    outlineVariant   = Bg500,

    error            = ErrorRed,
    onError          = Bg900,

    scrim            = Color(0xCC0A0A0F),
    inverseSurface   = TextPrimary,
    inverseOnSurface = Bg900,
    inversePrimary   = Gold900,
)

// ── M3 light colour scheme (minimal — app is dark-first) ──
private val LightColorScheme = lightColorScheme(
    primary          = Gold600,
    onPrimary        = LightBg,
    primaryContainer = Color(0xFFF5E6C8),
    onPrimaryContainer = Gold900,

    secondary        = DardBlue,
    onSecondary      = LightBg,
    secondaryContainer = Color(0xFFDDE3FF),
    onSecondaryContainer = Color(0xFF1A1E3A),

    background       = LightBg,
    onBackground     = LightOnSurface,

    surface          = LightSurface,
    onSurface        = LightOnSurface,
    surfaceVariant   = Color(0xFFEDE4D0),
    onSurfaceVariant = Color(0xFF5C4A2A),

    outline          = Color(0xFFD0B88A),
    error            = ErrorRed,
    onError          = LightBg,
)

// ── Extended dark colours instance ───────────────────────
private val DarkExtended = ShayariExtendedColors(
    cardBackground   = Bg700,           // 0xFF111118 — dark card
    cardBorder       = Bg500,           // 0xFF1E1E2A — subtle border
    iconButtonBg     = Bg600,           // 0xFF141418
    bottomNavBg      = Bg800,           // 0xFF0E0E16
    divider          = Bg500,           // 0xFF1E1E2A
    chipActive       = Gold400,         // 0xFFC9A96E
    chipActiveFg     = Bg900,           // 0xFF0A0A0F
    chipInactive     = Bg600,           // 0xFF141418
    chipInactiveFg   = TextMuted,       // 0xFF888880
    accentGold       = Gold400,
    accentGoldSubtle = Color(0x22C9A96E),
    ishq             = IshqPink,
    ishqDim          = IshqPinkDim,     // 0x1FE05C7A ~12% opacity
    dard             = DardBlue,
    dardDim          = DardBlueDim,     // 0x1F6B7FE0 ~12% opacity
    zindagi          = ZindagiGreen,
    zindagiDim       = ZindagiGreenDim,
    khushi           = KhushiAmber,
    khushiDim        = KhushiAmberDim,
    judai            = JudaiPurple,
    judaiDim         = JudaiPurpleDim,
    wafa             = WafaTeal,
    wafaDim          = WafaTealDim,
)

// ── Extended light colours instance ──────────────────────
private val LightExtended = ShayariExtendedColors(
    cardBackground   = LightSurface,
    cardBorder       = Color(0xFFE8D8B8),
    iconButtonBg     = Color(0xFFEEE4D0),
    bottomNavBg      = LightBg,
    divider          = Color(0xFFE0CFA8),
    chipActive       = Gold600,
    chipActiveFg     = LightBg,
    chipInactive     = Color(0xFFEDE4D0),
    chipInactiveFg   = Color(0xFF8A6A3A),
    accentGold       = Gold600,
    accentGoldSubtle = Color(0x229E7D47),
    ishq             = IshqPink,
    ishqDim          = Color(0x1FE05C7A),
    dard             = DardBlue,
    dardDim          = Color(0x1F6B7FE0),
    zindagi          = ZindagiGreen,
    zindagiDim       = Color(0x1F58C49A),
    khushi           = KhushiAmber,
    khushiDim        = Color(0x1FF0A04A),
    judai            = JudaiPurple,
    judaiDim         = Color(0x1F9B72CF),
    wafa             = WafaTeal,
    wafaDim          = Color(0x1F4AC9B8),
)

// ── Public accessor ───────────────────────────────────────
val MaterialTheme.shayariColors: ShayariExtendedColors
    @Composable get() = LocalShayariColors.current

// ── Theme composable ──────────────────────────────────────
@Composable
fun ShayariWalaTheme(
    darkTheme      : Boolean = isSystemInDarkTheme(),
    dynamicColor   : Boolean = false,           // keep false — custom palette
    content        : @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme  -> DarkColorScheme
        else       -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtended else LightExtended

    CompositionLocalProvider(LocalShayariColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = ShayariTypography,
            content     = content
        )
    }
}