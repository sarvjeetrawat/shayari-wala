package com.kunpitech.shayariwala.ui.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.components.formatCount
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextPrimary
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun SavedScreen(
    onShayariClick : (String) -> Unit,
    viewModel      : SavedViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {

            // ── Header ────────────────────────────────────
            SavedHeader(count = uiState.savedShayari.size)

            // ── Content ───────────────────────────────────
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color    = Gold400,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.error != null -> {
                        ErrorState(
                            message  = uiState.error!!,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.savedShayari.isEmpty() -> {
                        EmptyState(modifier = Modifier.align(Alignment.Center))
                    }

                    else -> {
                        SavedList(
                            list           = uiState.savedShayari,
                            onShayariClick = onShayariClick,
                            onUnsave       = { viewModel.unsave(it) },
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────
@Composable
private fun SavedHeader(count: Int) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text  = "Saved ♡",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text       = "Aapki pasandida shayari",
                fontSize   = 11.sp,
                color      = TextDisabled,
                fontFamily = DmSans,
            )
        }

        if (count > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141418))
                    .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text  = "$count shayari",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color      = Gold400,
                        fontFamily = DmSans,
                        fontSize   = 12.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Saved List
// ─────────────────────────────────────────────────────────
@Composable
private fun SavedList(
    list           : List<Shayari>,
    onShayariClick : (String) -> Unit,
    onUnsave       : (String) -> Unit,
) {
    LazyColumn(
        contentPadding      = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top        = 4.dp,
            bottom     = 100.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            items = list,
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
                exit         = fadeOut(tween(200)) +
                        slideOutHorizontally(tween(250)) { -it },
            ) {
                SavedShayariCard(
                    shayari        = shayari,
                    onClick        = { onShayariClick(shayari.id) },
                    onUnsave       = { onUnsave(shayari.id) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Saved Card
// ─────────────────────────────────────────────────────────
@Composable
private fun SavedShayariCard(
    shayari  : Shayari,
    onClick  : () -> Unit,
    onUnsave : () -> Unit,
) {
    val accentColor = categoryColor(shayari.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111118))
            .border(0.5.dp, Color(0xFF1E1E2A), RoundedCornerShape(16.dp))
            .clickable { onClick() },
    ) {

        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .matchParentSize()
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(
                        topStart    = 16.dp,
                        bottomStart = 16.dp,
                        topEnd      = 0.dp,
                        bottomEnd   = 0.dp,
                    ),
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start  = 18.dp,
                    end    = 14.dp,
                    top    = 14.dp,
                    bottom = 12.dp,
                ),
        ) {

            // ── Top row: category + unsave button ─────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Category dot + label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(accentColor, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = shayari.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color         = accentColor,
                            fontFamily    = DmSans,
                            fontSize      = 11.sp,
                            letterSpacing = 1.sp,
                        ),
                    )
                }

                // Unsave button
                Box(
                    modifier         = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141418))
                        .border(0.5.dp, Color(0xFF2A2A3A), CircleShape)
                        .clickable { onUnsave() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.BookmarkRemove,
                        contentDescription = "Remove from saved",
                        tint               = TextDisabled,
                        modifier           = Modifier.size(14.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Shayari text ──────────────────────────────
            Text(
                text     = shayari.hindiText,
                style    = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(8.dp))

            // ── Poet name ─────────────────────────────────
            Text(
                text  = "— ${shayari.poet}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color      = TextMuted,
                    fontFamily = DmSans,
                    fontSize   = 12.sp,
                ),
            )

            Spacer(Modifier.height(14.dp))

            // ── Action row ────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Likes count (read only on saved screen)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = "♥",
                        color = TextDisabled,
                        fontSize = 13.sp,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = formatCount(shayari.likes),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color      = TextDisabled,
                            fontFamily = DmSans,
                            fontSize   = 13.sp,
                        ),
                    )
                }

                Spacer(Modifier.width(14.dp))

                // Comments count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = androidx.compose.material.icons.Icons.Outlined.ModeComment,
                        contentDescription = null,
                        tint               = TextDisabled,
                        modifier           = Modifier.size(13.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = formatCount(shayari.comments),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color      = TextDisabled,
                            fontFamily = DmSans,
                            fontSize   = 13.sp,
                        ),
                    )
                }

                Spacer(Modifier.weight(1f))

                // Share pill
                Box(
                    modifier         = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF141418))
                        .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint               = TextMuted,
                            modifier           = Modifier.size(12.dp),
                        )
                        Text(
                            text  = "Share",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color      = TextMuted,
                                fontFamily = DmSans,
                                fontSize   = 12.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Bookmark illustration
        Box(
            modifier         = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF141418))
                .border(0.5.dp, Color(0xFF2A2A3A), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = "♡",
                color    = Gold400,
                fontSize = 32.sp,
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text      = "Koi saved shayari nahi",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text      = "Jo shayari pasand aaye,\nusse save karo yahan se milegi",
            style     = MaterialTheme.typography.bodySmall.copy(
                fontFamily = PlayfairDisplay,
                fontSize   = 13.sp,
                lineHeight = 22.sp,
            ),
            textAlign = TextAlign.Center,
            color     = TextMuted,
        )

        Spacer(Modifier.height(28.dp))

        // Hint
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141418))
                .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text  = "Shayari kholke ⊕ tap karo",
                style = MaterialTheme.typography.labelMedium.copy(
                    color      = TextMuted,
                    fontFamily = DmSans,
                    fontSize   = 12.sp,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Error State
// ─────────────────────────────────────────────────────────
@Composable
private fun ErrorState(
    message  : String,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier            = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("✦", color = Gold400, fontSize = 28.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text  = "Kuch gadbad ho gayi",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = message,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}