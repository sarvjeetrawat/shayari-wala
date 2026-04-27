package com.kunpitech.shayariwala.ui.moodfeed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.ui.components.ShayariCard
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryDimColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun MoodFeedScreen(
    category       : String,
    onBack         : () -> Unit,
    onShayariClick : (String) -> Unit,
    viewModel      : MoodFeedViewModel = viewModel(
        factory = MoodFeedViewModel.Factory(category)
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(category)
    val dimColor    = categoryDimColor(category)
    val label       = categoryLabel(category)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {

            // ── Header ────────────────────────────────
            MoodHeader(
                label       = label,
                accentColor = accentColor,
                dimColor    = dimColor,
                count       = uiState.shayariList.size,
                onBack      = onBack,
            )

            // ── Feed ──────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color    = accentColor,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.error != null -> {
                        Column(
                            modifier            = Modifier.align(Alignment.Center).padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("✦", color = Gold400, fontSize = 28.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text  = "Kuch gadbad ho gayi",
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }

                    uiState.shayariList.isEmpty() -> {
                        Column(
                            modifier            = Modifier.align(Alignment.Center).padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(label.take(2), fontSize = 36.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text      = "Is mood mein abhi koi shayari nahi",
                                style     = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            contentPadding      = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top        = 8.dp,
                                bottom     = 40.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            itemsIndexed(
                                items = uiState.shayariList,
                                key   = { _, s -> s.id },
                            ) { index, shayari ->
                                val visible = remember {
                                    MutableTransitionState(false).also { it.targetState = true }
                                }
                                androidx.compose.animation.AnimatedVisibility(
                                    visibleState = visible,
                                    enter        = fadeIn(tween(300, delayMillis = index * 50)) +
                                            slideInVertically(
                                                tween(400, delayMillis = index * 50)
                                            ) { it / 3 },
                                ) {
                                    ShayariCard(
                                        shayari  = shayari,
                                        isLiked  = shayari.id in uiState.likedIds,
                                        onLike   = { viewModel.toggleLike(shayari.id) },
                                        onShare  = { },
                                        onClick  = { onShayariClick(shayari.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Mood Header
// ─────────────────────────────────────────────────────────
@Composable
private fun MoodHeader(
    label       : String,
    accentColor : androidx.compose.ui.graphics.Color,
    dimColor    : androidx.compose.ui.graphics.Color,
    count       : Int,
    onBack      : () -> Unit,
) {
    val ext = MaterialTheme.shayariColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(dimColor)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Back button row
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ext.iconButtonBg)
                    .border(0.5.dp, ext.cardBorder, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = TextMuted, fontSize = 15.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text  = "Mood Feed",
                style = MaterialTheme.typography.titleMedium.copy(color = TextMuted),
            )
        }

        Spacer(Modifier.height(16.dp))

        // Mood label large
        Text(
            text  = label,
            style = MaterialTheme.typography.displaySmall.copy(color = accentColor),
        )

        Spacer(Modifier.height(4.dp))

        if (count > 0) {
            Text(
                text  = "$count shayari mil gayi",
                style = MaterialTheme.typography.labelMedium.copy(
                    color      = accentColor.copy(alpha = 0.7f),
                    fontFamily = DmSans,
                ),
            )
        }
    }
}