package com.kunpitech.shayariwala.ui.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.ads.BannerAdView
import com.kunpitech.shayariwala.ui.components.ShayariCard
import com.kunpitech.shayariwala.ui.theme.Bg600
import com.kunpitech.shayariwala.ui.theme.Bg900
import com.kunpitech.shayariwala.ui.theme.Border100
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun HomeScreen(
    onShayariClick : (shayariId: String) -> Unit,
    onSearchClick  : () -> Unit,
    viewModel      : HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext = MaterialTheme.shayariColors

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Top App Bar ───────────────────────────────
            TopBar(onSearchClick = onSearchClick)

            // ── Category Chips ────────────────────────────
            CategoryChips(
                selected = uiState.selectedCategory,
                onSelect = viewModel::selectCategory,
            )

            // ── Feed ──────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color    = Gold400,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.error != null -> {
                        ErrorState(
                            message   = uiState.error!!,
                            modifier  = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.shayariList.isEmpty() -> {
                        EmptyState(modifier = Modifier.align(Alignment.Center))
                    }

                    else -> {
                        LazyColumn(
                            contentPadding      = PaddingValues(
                                start  = 16.dp,
                                end    = 16.dp,
                                top    = 8.dp,
                                bottom = 100.dp,
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
                                    Column {
                                        ShayariCard(
                                            shayari  = shayari,
                                            isLiked  = shayari.id in uiState.likedIds,
                                            onLike   = { viewModel.toggleLike(shayari.id) },
                                            onShare  = { },
                                            onClick  = { onShayariClick(shayari.id) },
                                        )

                                        // ── Show banner ad after every 5th card ───
                                        if ((index + 1) % 5 == 0) {
                                            Spacer(Modifier.height(8.dp))
                                            BannerAdView(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
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
    }
}

// ─────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────
@Composable
private fun TopBar(onSearchClick: () -> Unit) {
    val ext = MaterialTheme.shayariColors

    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // App title
        Column {
            Text(
                text  = "Shayari ✦",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text     = "روح کی آواز",
                fontSize = 11.sp,
                color    = TextDisabled,
                fontFamily = DmSans,
            )
        }

        // Action icons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButtonRound(onClick = { /* notifications */ }) {
                Icon(
                    imageVector        = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint               = TextMuted,
                    modifier           = Modifier.size(18.dp)
                )
            }
            IconButtonRound(onClick = onSearchClick) {
                Icon(
                    imageVector        = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint               = Gold400,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun IconButtonRound(
    onClick  : () -> Unit,
    content  : @Composable () -> Unit,
) {
    val ext = MaterialTheme.shayariColors
    Box(
        modifier            = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ext.iconButtonBg)
            .border(0.5.dp, ext.cardBorder, CircleShape)
            .clickable { onClick() },
        contentAlignment    = Alignment.Center,
    ) { content() }
}

// ─────────────────────────────────────────────────────────
// Category chips
// ─────────────────────────────────────────────────────────
private val chipLabels = mapOf(
    "all"     to "Sab",
    "ishq"    to "♥ Ishq",
    "dard"    to "💧 Dard",
    "zindagi" to "✦ Zindagi",
    "khushi"  to "☀ Khushi",
    "judai"   to "☽ Judai",
    "wafa"    to "◈ Wafa",
)

@Composable
private fun CategoryChips(
    selected : String,
    onSelect : (String) -> Unit,
) {
    val ext = MaterialTheme.shayariColors

    LazyRow(
        contentPadding      = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier            = Modifier.padding(bottom = 10.dp),
    ) {
        items(categories) { cat ->
            val isActive = cat == selected
            val label    = chipLabels[cat] ?: cat

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        // active = gold, inactive = dark Bg600
                        if (isActive) ext.chipActive else Bg600
                    )
                    .border(
                        width = 0.5.dp,
                        color = if (isActive) ext.chipActive else Border100,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .clickable { onSelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = if (isActive) Bg900 else TextMuted,
                        fontFamily = DmSans,
                        fontSize   = 12.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Empty / Error states
// ─────────────────────────────────────────────────────────
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text  = "✦",
            color = Gold400,
            fontSize = 36.sp,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text  = "Koi shayari nahi mili",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = "Is category mein abhi kuch nahi hai",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text     = "Kuch gadbad ho gayi",
            style    = MaterialTheme.typography.headlineSmall,
            color    = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = message,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}