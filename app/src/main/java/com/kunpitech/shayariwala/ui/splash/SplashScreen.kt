package com.kunpitech.shayariwala.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold300
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onReady   : () -> Unit,
    viewModel : SplashViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // ── Animation values ──────────────────────────────────
    val logoScale   = remember { Animatable(0.3f) }
    val logoAlpha   = remember { Animatable(0f)   }
    val glowScale   = remember { Animatable(0.5f) }
    val glowAlpha   = remember { Animatable(0f)   }
    val titleAlpha  = remember { Animatable(0f)   }
    val titleOffset = remember { Animatable(30f)  }
    val taglineAlpha= remember { Animatable(0f)   }
    val dotsAlpha   = remember { Animatable(0f)   }

    // ── Run entrance animations ───────────────────────────
    LaunchedEffect(Unit) {
        // Glow pulse in
        launch {
            glowAlpha.animateTo(0.6f, tween(600, easing = EaseOutCubic))
            glowScale.animateTo(1.2f, tween(800, easing = EaseOutCubic))
        }

        // Logo pop in
        delay(200)
        launch {
            logoScale.animateTo(1f, tween(600, easing = EaseOutBack))
            logoAlpha.animateTo(1f, tween(400))
        }

        // Title slide up
        delay(600)
        launch {
            titleAlpha.animateTo(1f, tween(500))
            titleOffset.animateTo(0f, tween(500, easing = EaseOutCubic))
        }

        // Tagline fade in
        delay(1000)
        launch {
            taglineAlpha.animateTo(1f, tween(500))
        }

        // Dots fade in
        delay(1300)
        launch {
            dotsAlpha.animateTo(1f, tween(400))
        }
    }

    // ── Navigate when ready ───────────────────────────────
    LaunchedEffect(state) {
        if (state is SplashState.Ready) {
            // Fade out delay
            delay(300)
            onReady()
        }
    }

    // ── UI ────────────────────────────────────────────────
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F)),
        contentAlignment = Alignment.Center,
    ) {

        // ── Background glow ───────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(glowScale.value)
                .alpha(glowAlpha.value)
                .blur(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x44C9A96E),
                            Color(0x00C9A96E),
                        ),
                    ),
                    shape = CircleShape,
                )
        )

        // ── Center content ────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            // Logo icon
            Box(
                modifier         = Modifier
                    .size(100.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x33C9A96E),
                                Color(0x11C9A96E),
                                Color(0x00C9A96E),
                            ),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text     = "✦",
                    color    = Gold400,
                    fontSize = 52.sp,
                )
            }

            Spacer(Modifier.height(24.dp))

            // App name
            Box(
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .padding(bottom = titleOffset.value.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text      = "Shayari",
                        style     = androidx.compose.ui.text.TextStyle(
                            fontFamily = PlayfairDisplay,
                            fontSize   = 42.sp,
                            color      = Color(0xFFE8D5B0),
                            letterSpacing = 1.sp,
                        ),
                    )
                    Text(
                        text      = "Wala",
                        style     = androidx.compose.ui.text.TextStyle(
                            fontFamily = PlayfairDisplay,
                            fontSize   = 42.sp,
                            color      = Gold400,
                            letterSpacing = 1.sp,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Urdu tagline
            Text(
                text      = "روح کی آواز",
                style     = androidx.compose.ui.text.TextStyle(
                    fontFamily = DmSans,
                    fontSize   = 14.sp,
                    color      = TextMuted,
                ),
                modifier  = Modifier.alpha(taglineAlpha.value),
            )

            Spacer(Modifier.height(8.dp))

            // Hindi tagline
            Text(
                text      = "Dil ki baat, alfazon mein",
                style     = androidx.compose.ui.text.TextStyle(
                    fontFamily  = PlayfairDisplay,
                    fontSize    = 13.sp,
                    fontStyle   = FontStyle.Italic,
                    color       = TextDisabled,
                ),
                modifier  = Modifier.alpha(taglineAlpha.value),
            )
        }

        // ── Loading dots at bottom ────────────────────────
        Column(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(dotsAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoadingDots()
            Spacer(Modifier.height(12.dp))
            Text(
                text  = "Loading...",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = DmSans,
                    fontSize   = 11.sp,
                    color      = TextDisabled,
                ),
            )
        }
    }
}

// ── Animated loading dots ──────────────────────────────────
@Composable
private fun LoadingDots() {
    val dot1 = remember { Animatable(0.3f) }
    val dot2 = remember { Animatable(0.3f) }
    val dot3 = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        // Staggered pulse loop
        while (true) {
            launch {
                dot1.animateTo(1f, tween(300))
                dot1.animateTo(0.3f, tween(300))
            }
            delay(150)
            launch {
                dot2.animateTo(1f, tween(300))
                dot2.animateTo(0.3f, tween(300))
            }
            delay(150)
            launch {
                dot3.animateTo(1f, tween(300))
                dot3.animateTo(0.3f, tween(300))
            }
            delay(600)
        }
    }

    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        listOf(dot1, dot2, dot3).forEach { dot ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(dot.value)
                    .background(Gold400, CircleShape)
            )
        }
    }
}