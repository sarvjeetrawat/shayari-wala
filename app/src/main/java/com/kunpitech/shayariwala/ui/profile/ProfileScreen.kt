package com.kunpitech.shayariwala.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.data.model.UserProfile
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.PoetNameStyle
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextPrimary
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun ProfileScreen(
    onShayariClick  : (String) -> Unit,
    onWriteClick    : () -> Unit,
    viewModel       : ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext = MaterialTheme.shayariColors

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->

        if (uiState.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = Gold400) }
            return@Scaffold
        }

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 40.dp),
        ) {

            // ── Hero section ──────────────────────────
            item {
                ProfileHero(
                    profile      = uiState.profile,
                    savedCount   = uiState.savedShayari.size,
                    writtenCount = uiState.writtenShayari.size,
                    totalLikes   = uiState.writtenShayari.sumOf { it.likes },
                    onWriteClick = onWriteClick,
                )
            }

            // ── Tab bar ───────────────────────────────
            item {
                ProfileTabBar(
                    active   = uiState.activeTab,
                    onSelect = viewModel::setTab,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // ── Tab content ───────────────────────────
            item {
                AnimatedContent(
                    targetState   = uiState.activeTab,
                    transitionSpec = {
                        val dir = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        slideInHorizontally(tween(280)) { it * dir } + fadeIn(tween(280)) togetherWith
                                slideOutHorizontally(tween(280)) { -it * dir } + fadeOut(tween(280))
                    },
                    label = "profileTab",
                ) { tab ->
                    when (tab) {
                        ProfileTab.SAVED -> {
                            if (uiState.savedShayari.isEmpty()) {
                                EmptyTabState(
                                    icon    = "♡",
                                    title   = "Koi saved shayari nahi",
                                    subtitle= "Pasandida shayari save karo",
                                )
                            } else {
                                ShayariTabList(
                                    list           = uiState.savedShayari,
                                    onShayariClick = onShayariClick,
                                    trailingIcon   = { shayari ->
                                        Icon(
                                            imageVector        = Icons.Outlined.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint               = TextDisabled,
                                            modifier           = Modifier
                                                .size(18.dp)
                                                .clickable { viewModel.unsave(shayari.id) },
                                        )
                                    },
                                )
                            }
                        }
                        ProfileTab.WRITTEN -> {
                            if (uiState.writtenShayari.isEmpty()) {
                                EmptyTabState(
                                    icon    = "✦",
                                    title   = "Abhi kuch likha nahi",
                                    subtitle= "Apni pehli shayari likho",
                                    actionLabel = "Likho",
                                    onAction    = onWriteClick,
                                )
                            } else {
                                ShayariTabList(
                                    list           = uiState.writtenShayari,
                                    onShayariClick = onShayariClick,
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
// Profile Hero
// ─────────────────────────────────────────────────────────
@Composable
private fun ProfileHero(
    profile      : UserProfile,
    savedCount   : Int,
    writtenCount : Int,
    totalLikes   : Int,
    onWriteClick : () -> Unit,
) {
    val ext = MaterialTheme.shayariColors

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Avatar
        Box(
            modifier         = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ext.accentGoldSubtle)
                .border(1.5.dp, Gold400.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = profile.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = PlayfairDisplay,
                    color      = Gold400,
                    fontSize   = 30.sp,
                ),
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text  = profile.displayName.ifBlank { "Shayar" },
            style = MaterialTheme.typography.titleLarge,
        )

        if (profile.bio.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text      = profile.bio,
                style     = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = PlayfairDisplay,
                    fontSize   = 13.sp,
                ),
                textAlign = TextAlign.Center,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(20.dp))

        // Stats row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatBox(label = "Saved",   value = "$savedCount",   modifier = Modifier.weight(1f))
            StatBox(label = "Likha",   value = "$writtenCount", modifier = Modifier.weight(1f))
            StatBox(label = "Likes",   value = formatCount(totalLikes), modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Write button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Gold400)
                .clickable { onWriteClick() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Create,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.background,
                    modifier           = Modifier.size(16.dp),
                )
                Text(
                    text  = "Shayari Likho",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = MaterialTheme.colorScheme.background,
                        fontFamily = DmSans,
                        fontSize   = 14.sp,
                    ),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(color = MaterialTheme.shayariColors.divider, thickness = 0.5.dp)
    }
}

@Composable
private fun StatBox(
    label    : String,
    value    : String,
    modifier : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text  = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color    = Gold400,
                fontSize = 20.sp,
            ),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// ─────────────────────────────────────────────────────────
// Profile Tab Bar
// ─────────────────────────────────────────────────────────
@Composable
private fun ProfileTabBar(
    active   : ProfileTab,
    onSelect : (ProfileTab) -> Unit,
    modifier : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors

    Row(
        modifier              = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ProfileTab.entries.forEach { tab ->
            val isActive = tab == active
            val label    = when (tab) {
                ProfileTab.SAVED   -> "♡  Saved"
                ProfileTab.WRITTEN -> "✦  Meri Shayari"
            }
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isActive) Gold400 else ext.cardBackground)
                    .clickable { onSelect(tab) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = if (isActive) MaterialTheme.colorScheme.background else TextMuted,
                        fontFamily = DmSans,
                        fontSize   = 12.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Shayari Tab List
// ─────────────────────────────────────────────────────────
@Composable
private fun ShayariTabList(
    list           : List<Shayari>,
    onShayariClick : (String) -> Unit,
    trailingIcon   : (@Composable (Shayari) -> Unit)? = null,
) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        list.forEachIndexed { index, shayari ->
            val visible = remember {
                MutableTransitionState(false).also { it.targetState = true }
            }
            AnimatedVisibility(
                visibleState = visible,
                enter        = fadeIn(tween(300, delayMillis = index * 40)) +
                        androidx.compose.animation.slideInVertically(
                            tween(400, delayMillis = index * 40)
                        ) { it / 3 },
            ) {
                ProfileShayariCard(
                    shayari        = shayari,
                    onClick        = { onShayariClick(shayari.id) },
                    trailingIcon   = trailingIcon?.let { { it(shayari) } },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ProfileShayariCard(
    shayari      : Shayari,
    onClick      : () -> Unit,
    trailingIcon : (@Composable () -> Unit)? = null,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(shayari.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(52.dp)
                .background(accentColor, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = shayari.hindiText,
                style    = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("— ${shayari.poet}", style = PoetNameStyle)
                Text("·", color = TextDisabled, fontSize = 10.sp)
                Text(
                    text  = categoryLabel(shayari.category),
                    style = MaterialTheme.typography.labelSmall.copy(color = accentColor),
                )
            }
        }
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            trailingIcon()
        }
    }
}

// ─────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────
@Composable
private fun EmptyTabState(
    icon        : String,
    title       : String,
    subtitle    : String,
    actionLabel : String? = null,
    onAction    : (() -> Unit)? = null,
) {
    val ext = MaterialTheme.shayariColors
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, color = Gold400, fontSize = 36.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Gold400)
                    .clickable { onAction() }
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(
                    text  = actionLabel,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color    = MaterialTheme.colorScheme.background,
                        fontSize = 13.sp,
                    ),
                )
            }
        }
    }
}

private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000f)}M"
    n >= 1_000     -> "${"%.1f".format(n / 1_000f)}k"
    else           -> "$n"
}