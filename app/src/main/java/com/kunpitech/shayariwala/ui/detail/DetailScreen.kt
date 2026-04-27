package com.kunpitech.shayariwala.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryDimColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.CategoryTagStyle
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.IshqPink
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.PoetNameStyle
import com.kunpitech.shayariwala.ui.theme.ShayariDetailStyle
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.UrduTextStyle
import com.kunpitech.shayariwala.ui.theme.shayariColors
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    shayariId  : String,
    onBack     : () -> Unit,
    onRelatedClick : (String) -> Unit,
    viewModel  : DetailViewModel = viewModel(factory = DetailViewModel.Factory(shayariId)),
) {
    val uiState        = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarState  = remember { SnackbarHostState() }
    val scope          = rememberCoroutineScope()
    val context        = LocalContext.current
    val ext            = MaterialTheme.shayariColors

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost   = { SnackbarHost(snackbarState) },
    ) { innerPadding ->

        when {
            uiState.isLoading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold400)
                }
            }

            uiState.error != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✦", color = Gold400, fontSize = 32.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text  = "Kuch gadbad ho gayi",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text  = uiState.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            uiState.shayari != null -> {
                val shayari = uiState.shayari

                LazyColumn(
                    modifier       = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 40.dp),
                ) {

                    // ── Top bar ───────────────────────────
                    item {
                        DetailTopBar(
                            onBack = onBack,
                            onShare = {
                                shareShayari(context, shayari)
                            },
                        )
                    }

                    // ── Main shayari card ─────────────────
                    item {
                        val visibleState = remember {
                            MutableTransitionState(false).also { it.targetState = true }
                        }
                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter        = fadeIn(tween(400)) +
                                    slideInVertically(tween(500)) { it / 4 },
                        ) {
                            MainShayariCard(
                                shayari = shayari,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }

                    // ── Action buttons row ────────────────
                    item {
                        val visibleState = remember {
                            MutableTransitionState(false).also { it.targetState = true }
                        }
                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter        = fadeIn(tween(400, delayMillis = 150)),
                        ) {
                            ActionButtonsRow(
                                isLiked   = uiState.isLiked,
                                isSaved   = uiState.isSaved,
                                likeCount = shayari.likes + if (uiState.isLiked) 1 else 0,
                                onLike    = { viewModel.toggleLike() },
                                onSave    = { viewModel.toggleSave() },
                                onShare   = { shareShayari(context, shayari) },
                                onCopy    = {
                                    copyToClipboard(context, shayari.hindiText)
                                    scope.launch {
                                        snackbarState.showSnackbar("Shayari copy ho gayi ✦")
                                    }
                                },
                                modifier  = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }

                    // ── Divider + poet info ───────────────
                    item {
                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(
                            color     = MaterialTheme.shayariColors.divider,
                            thickness = 0.5.dp,
                            modifier  = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        PoetInfoRow(
                            poet     = shayari.poet,
                            category = shayari.category,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(24.dp))
                    }

                    // ── Related shayari label ─────────────
                    if (uiState.related.isNotEmpty()) {
                        item {
                            Text(
                                text     = "Aur Shayari",
                                style    = MaterialTheme.typography.labelLarge.copy(
                                    color         = TextMuted,
                                    letterSpacing = 0.8.sp,
                                ),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // ── Related cards ─────────────────
                        items(
                            items = uiState.related,
                            key   = { it.id },
                        ) { related ->
                            val visibleState = remember {
                                MutableTransitionState(false).also { it.targetState = true }
                            }
                            AnimatedVisibility(
                                visibleState = visibleState,
                                enter        = fadeIn(tween(300)) +
                                        slideInVertically(tween(400)) { it / 3 },
                            ) {
                                RelatedShayariCard(
                                    shayari  = related,
                                    onClick  = { onRelatedClick(related.id) },
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical   = 4.dp,
                                    ),
                                )
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
private fun DetailTopBar(
    onBack  : () -> Unit,
    onShare : () -> Unit,
) {
    val ext = MaterialTheme.shayariColors

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Back button
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ext.iconButtonBg)
                .border(0.5.dp, ext.cardBorder, CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "←",
                color = TextMuted,
                fontSize = 16.sp,
            )
        }

        Text(
            text  = "Shayari",
            style = MaterialTheme.typography.titleMedium,
        )

        // Share button
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ext.iconButtonBg)
                .border(0.5.dp, ext.cardBorder, CircleShape)
                .clickable { onShare() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Outlined.Share,
                contentDescription = "Share",
                tint               = TextMuted,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Main Shayari Card
// ─────────────────────────────────────────────────────────
@Composable
private fun MainShayariCard(
    shayari  : Shayari,
    modifier : Modifier = Modifier,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(shayari.category)
    val dimColor    = categoryDimColor(shayari.category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(20.dp))
    ) {
        // Subtle glow at top center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = dimColor,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
        )

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Category badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(dimColor)
                    .border(0.5.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
            ) {
                Text(
                    text  = categoryLabel(shayari.category).uppercase(),
                    style = CategoryTagStyle.copy(color = accentColor),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Main shayari text
            Text(
                text      = shayari.hindiText,
                style     = ShayariDetailStyle,
                textAlign = TextAlign.Center,
            )

            // Urdu text
            if (shayari.urduText.isNotBlank()) {
                Spacer(Modifier.height(16.dp))

                // Ornamental divider
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier              = Modifier.fillMaxWidth(),
                ) {
                    HorizontalDivider(
                        modifier  = Modifier.width(40.dp),
                        color     = accentColor.copy(alpha = 0.3f),
                        thickness = 0.5.dp,
                    )
                    Text(
                        text     = "  ✦  ",
                        color    = accentColor.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                    )
                    HorizontalDivider(
                        modifier  = Modifier.width(40.dp),
                        color     = accentColor.copy(alpha = 0.3f),
                        thickness = 0.5.dp,
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text      = shayari.urduText,
                    style     = UrduTextStyle,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Gold divider line
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(1.dp)
                    .background(accentColor.copy(alpha = 0.4f))
            )

            Spacer(Modifier.height(14.dp))

            // Poet
            Text(
                text  = "— ${shayari.poet}",
                style = PoetNameStyle.copy(fontSize = 13.sp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Action Buttons Row  (Like · Save · Share · Copy)
// ─────────────────────────────────────────────────────────
@Composable
private fun ActionButtonsRow(
    isLiked   : Boolean,
    isSaved   : Boolean,
    likeCount : Int,
    onLike    : () -> Unit,
    onSave    : () -> Unit,
    onShare   : () -> Unit,
    onCopy    : () -> Unit,
    modifier  : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors

    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Like
        ActionTile(
            onClick  = onLike,
            modifier = Modifier.weight(1f),
        ) {
            val scale by animateFloatAsState(
                targetValue   = if (isLiked) 1.2f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label         = "likeScale",
            )
            Icon(
                imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint               = if (isLiked) IshqPink else TextDisabled,
                modifier           = Modifier.size(20.dp).scale(scale),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = formatCount(likeCount),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isLiked) IshqPink else TextDisabled,
                ),
            )
        }

        // Save
        ActionTile(
            onClick  = onSave,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector        = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Save",
                tint               = if (isSaved) Gold400 else TextDisabled,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = if (isSaved) "Saved" else "Save",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (isSaved) Gold400 else TextDisabled,
                ),
            )
        }

        // Share
        ActionTile(
            onClick  = onShare,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector        = Icons.Outlined.Share,
                contentDescription = "Share",
                tint               = TextDisabled,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Share",
                style = MaterialTheme.typography.labelMedium,
            )
        }

        // Copy
        ActionTile(
            onClick  = onCopy,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector        = Icons.Outlined.ContentCopy,
                contentDescription = "Copy",
                tint               = TextDisabled,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Copy",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun ActionTile(
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    content  : @Composable () -> Unit,
) {
    val ext = MaterialTheme.shayariColors
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}

// ─────────────────────────────────────────────────────────
// Poet Info Row
// ─────────────────────────────────────────────────────────
@Composable
private fun PoetInfoRow(
    poet     : String,
    category : String,
    modifier : Modifier = Modifier,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(category)
    val dimColor    = categoryDimColor(category)

    Row(
        modifier          = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar circle
        Box(
            modifier         = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(dimColor)
                .border(0.5.dp, accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = poet.firstOrNull()?.uppercaseChar()?.toString() ?: "✦",
                style    = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = PlayfairDisplay,
                    color      = accentColor,
                ),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text  = poet,
                style = MaterialTheme.typography.titleSmall.copy(color = accentColor),
            )
            Text(
                text  = categoryLabel(category),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Related Shayari Card
// ─────────────────────────────────────────────────────────
@Composable
private fun RelatedShayariCard(
    shayari  : Shayari,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(shayari.category)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(52.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(2.dp),
                )
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = shayari.hindiText,
                style    = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = "— ${shayari.poet}",
                style = PoetNameStyle,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────
private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000f)}M"
    n >= 1_000     -> "${"%.1f".format(n / 1_000f)}k"
    else           -> "$n"
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Shayari", text))
}

private fun shareShayari(context: Context, shayari: Shayari) {
    val shareText = buildString {
        appendLine(shayari.hindiText)
        if (shayari.urduText.isNotBlank()) {
            appendLine()
            appendLine(shayari.urduText)
        }
        appendLine()
        append("— ${shayari.poet}")
        appendLine()
        appendLine()
        append("Shayari Wala app se")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type    = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Shayari share karo"))
}