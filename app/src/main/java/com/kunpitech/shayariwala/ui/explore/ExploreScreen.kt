package com.kunpitech.shayariwala.ui.explore

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.data.model.Poet
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryDimColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.CategoryTagStyle
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.PoetNameStyle
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextPrimary
import com.kunpitech.shayariwala.ui.theme.TextSecondary
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun ExploreScreen(
    onShayariClick : (String) -> Unit,
    onMoodClick    : (String) -> Unit,
    viewModel      : ExploreViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext = MaterialTheme.shayariColors

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Header ────────────────────────────────────
            ExploreHeader()

            // ── Search Bar ────────────────────────────────
            SearchBar(
                query     = uiState.searchQuery,
                onChange  = viewModel::onQueryChange,
                onClear   = viewModel::clearSearch,
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            Spacer(Modifier.height(12.dp))

            // ── Search Results overlay ────────────────────
            if (uiState.isSearching) {
                SearchResultsList(
                    results        = uiState.searchResults,
                    onShayariClick = onShayariClick,
                    modifier       = Modifier.fillMaxSize(),
                )
                return@Scaffold
            }

            // ── Tab Bar ───────────────────────────────────
            TabBar(
                active   = uiState.activeTab,
                onSelect = viewModel::setTab,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(12.dp))

            // ── Tab Content ───────────────────────────────
            AnimatedContent(
                targetState   = uiState.activeTab,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    slideInHorizontally(tween(300)) { it * direction } + fadeIn(tween(300)) togetherWith
                            slideOutHorizontally(tween(300)) { -it * direction } + fadeOut(tween(300))
                },
                label = "tabContent",
            ) { tab ->
                when (tab) {
                    ExploreTab.DISCOVER -> DiscoverTab(
                        shayariList = uiState.trendingShayari,
                        isLoading   = uiState.isTrendingLoading,
                        onClick     = onShayariClick,
                    )
                    ExploreTab.POETS -> PoetsTab(
                        poets              = uiState.poets,
                        isLoading          = uiState.isPoetsLoading,
                        selectedPoet       = uiState.selectedPoet,
                        poetShayari        = uiState.poetShayari,
                        isPoetShayariLoading = uiState.isPoetShayariLoading,
                        onPoetClick        = viewModel::selectPoet,
                        onPoetBack         = viewModel::clearSelectedPoet,
                        onShayariClick     = onShayariClick,
                    )
                    ExploreTab.MOODS -> MoodsTab(
                        onMoodClick = onMoodClick,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────
@Composable
private fun ExploreHeader() {
    Row(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text  = "Explore ✦",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text       = "Dhundho, padho, mehsoos karo",
                fontSize   = 11.sp,
                color      = TextDisabled,
                fontFamily = DmSans,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Search Bar
// ─────────────────────────────────────────────────────────
@Composable
private fun SearchBar(
    query    : String,
    onChange : (String) -> Unit,
    onClear  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors

    Row(
        modifier          = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector        = Icons.Outlined.Search,
            contentDescription = null,
            tint               = if (query.isNotBlank()) Gold400 else TextDisabled,
            modifier           = Modifier.size(18.dp),
        )

        BasicTextField(
            value         = query,
            onValueChange = onChange,
            modifier      = Modifier.weight(1f),
            singleLine    = true,
            cursorBrush   = SolidColor(Gold400),
            textStyle     = MaterialTheme.typography.bodySmall.copy(
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontFamily = DmSans,
            ),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text  = "Shayar ya alfaaz dhundho...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color    = TextDisabled,
                            fontSize = 14.sp,
                        ),
                    )
                }
                inner()
            }
        )

        if (query.isNotBlank()) {
            IconButton(
                onClick  = onClear,
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Close,
                    contentDescription = "Clear",
                    tint               = TextMuted,
                    modifier           = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Tab Bar
// ─────────────────────────────────────────────────────────
private val tabLabels = mapOf(
    ExploreTab.DISCOVER to "Trending",
    ExploreTab.POETS    to "Shayar",
    ExploreTab.MOODS    to "Mood",
)

@Composable
private fun TabBar(
    active   : ExploreTab,
    onSelect : (ExploreTab) -> Unit,
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
        ExploreTab.entries.forEach { tab ->
            val isActive = tab == active
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isActive) Gold400 else ext.cardBackground)
                    .clickable { onSelect(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = tabLabels[tab] ?: "",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color      = if (isActive) MaterialTheme.colorScheme.background else TextMuted,
                        fontFamily = DmSans,
                        fontSize   = 13.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Search Results
// ─────────────────────────────────────────────────────────
@Composable
private fun SearchResultsList(
    results        : List<Shayari>,
    onShayariClick : (String) -> Unit,
    modifier       : Modifier = Modifier,
) {
    if (results.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✦", color = Gold400, fontSize = 28.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    text  = "Koi nateeja nahi mila",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Doosre alfaaz try karo",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        return
    }

    LazyColumn(
        modifier       = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                text     = "${results.size} nateeje mile",
                style    = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        itemsIndexed(results, key = { _, s -> s.id }) { index, shayari ->
            val visible = remember {
                MutableTransitionState(false).also { it.targetState = true }
            }
            AnimatedVisibility(
                visibleState = visible,
                enter        = fadeIn(tween(300, delayMillis = index * 40)) +
                        slideInVertically(tween(400, delayMillis = index * 40)) { it / 3 },
            ) {
                SearchResultCard(shayari = shayari, onClick = { onShayariClick(shayari.id) })
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    shayari : Shayari,
    onClick : () -> Unit,
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
                .height(48.dp)
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
            Text(
                text  = "— ${shayari.poet}",
                style = PoetNameStyle,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text  = "→",
            color = TextDisabled,
            fontSize = 16.sp,
        )
    }
}

// ─────────────────────────────────────────────────────────
// Discover Tab — Trending
// ─────────────────────────────────────────────────────────
@Composable
private fun DiscoverTab(
    shayariList : List<Shayari>,
    isLoading   : Boolean,
    onClick     : (String) -> Unit,
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Gold400)
        }
        return
    }

    if (shayariList.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Koi trending shayari nahi", style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            SectionLabel(label = "Aaj ki Trending")
        }
        itemsIndexed(shayariList, key = { _, s -> s.id }) { index, shayari ->
            val visible = remember {
                MutableTransitionState(false).also { it.targetState = true }
            }
            AnimatedVisibility(
                visibleState = visible,
                enter        = fadeIn(tween(300, delayMillis = index * 50)) +
                        slideInVertically(tween(400, delayMillis = index * 50)) { it / 3 },
            ) {
                TrendingShayariCard(
                    rank    = index + 1,
                    shayari = shayari,
                    onClick = { onClick(shayari.id) },
                )
            }
        }
    }
}

@Composable
private fun TrendingShayariCard(
    rank    : Int,
    shayari : Shayari,
    onClick : () -> Unit,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(shayari.category)
    val dimColor    = categoryDimColor(shayari.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Rank badge
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (rank <= 3) dimColor else ext.iconButtonBg)
                .border(0.5.dp, if (rank <= 3) accentColor.copy(0.4f) else ext.cardBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "$rank",
                style = MaterialTheme.typography.labelLarge.copy(
                    color      = if (rank <= 3) accentColor else TextMuted,
                    fontFamily = DmSans,
                    fontSize   = 12.sp,
                ),
            )
        }

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
                    text  = "♥ ${formatCount(shayari.likes)}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextDisabled),
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        Text("→", color = TextDisabled, fontSize = 14.sp)
    }
}

// ─────────────────────────────────────────────────────────
// Poets Tab
// ─────────────────────────────────────────────────────────
@Composable
private fun PoetsTab(
    poets                : List<Poet>,
    isLoading            : Boolean,
    selectedPoet         : Poet?,
    poetShayari          : List<Shayari>,
    isPoetShayariLoading : Boolean,
    onPoetClick          : (Poet) -> Unit,
    onPoetBack           : () -> Unit,
    onShayariClick       : (String) -> Unit,
) {
    // If a poet is selected, show their shayari list
    if (selectedPoet != null) {
        PoetShayariList(
            poet       = selectedPoet,
            shayari    = poetShayari,
            isLoading  = isPoetShayariLoading,
            onBack     = onPoetBack,
            onShayariClick = onShayariClick,
        )
        return
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Gold400)
        }
        return
    }

    LazyVerticalGrid(
        columns             = GridCells.Fixed(2),
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            SectionLabel("Mashhoor Shayar")
        }
        items(poets, key = { it.id }) { poet ->
            PoetCard(poet = poet, onClick = { onPoetClick(poet) })
        }
    }
}

@Composable
private fun PoetCard(
    poet    : Poet,
    onClick : () -> Unit,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(poet.category)
    val dimColor    = categoryDimColor(poet.category)

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Avatar
        Box(
            modifier         = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(dimColor)
                .border(0.5.dp, accentColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = poet.name.firstOrNull()?.uppercaseChar()?.toString() ?: "✦",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = PlayfairDisplay,
                    color      = accentColor,
                    fontSize   = 20.sp,
                ),
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text      = poet.name,
            style     = MaterialTheme.typography.titleSmall.copy(color = accentColor),
            textAlign = TextAlign.Center,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
        )

        if (poet.urduName.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text      = poet.urduName,
                style     = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text  = "${poet.shayariCount} shayari",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun PoetShayariList(
    poet           : Poet,
    shayari        : List<Shayari>,
    isLoading      : Boolean,
    onBack         : () -> Unit,
    onShayariClick : (String) -> Unit,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(poet.category)
    val dimColor    = categoryDimColor(poet.category)

    LazyColumn(
        contentPadding      = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Poet hero header
        item {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier         = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ext.iconButtonBg)
                            .border(0.5.dp, ext.cardBorder, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("←", color = TextMuted, fontSize = 14.sp)
                    }
                    Spacer(Modifier.weight(1f))
                }

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier         = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(dimColor)
                        .border(1.dp, accentColor.copy(0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = poet.name.firstOrNull()?.uppercaseChar()?.toString() ?: "✦",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairDisplay,
                            color      = accentColor,
                        ),
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text  = poet.name,
                    style = MaterialTheme.typography.headlineSmall.copy(color = accentColor),
                )

                if (poet.urduName.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = poet.urduName,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                if (poet.bio.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = poet.bio,
                        style     = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        maxLines  = 3,
                        overflow  = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "${poet.shayariCount} shayari",
                    style = MaterialTheme.typography.labelMedium.copy(color = accentColor),
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = ext.divider, thickness = 0.5.dp)
            }
        }

        if (isLoading) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Gold400)
                }
            }
        } else {
            itemsIndexed(shayari, key = { _, s -> s.id }) { index, s ->
                val visible = remember {
                    MutableTransitionState(false).also { it.targetState = true }
                }
                AnimatedVisibility(
                    visibleState = visible,
                    enter        = fadeIn(tween(300, delayMillis = index * 40)) +
                            slideInVertically(tween(400, delayMillis = index * 40)) { it / 3 },
                ) {
                    SearchResultCard(
                        shayari = s,
                        onClick = { onShayariClick(s.id) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Moods Tab
// ─────────────────────────────────────────────────────────
@Composable
private fun MoodsTab(onMoodClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns               = GridCells.Fixed(2),
        contentPadding        = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement   = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            SectionLabel("Apna Mood Chuniye")
        }
        items(moodCategories, key = { it.first }) { (slug, label, tagline) ->
            MoodCard(
                slug     = slug,
                label    = label,
                tagline  = tagline,
                onClick  = { onMoodClick(slug) },
            )
        }
    }
}

@Composable
private fun MoodCard(
    slug    : String,
    label   : String,
    tagline : String,
    onClick : () -> Unit,
) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(slug)
    val dimColor    = categoryDimColor(slug)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(dimColor)
            .border(0.5.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color      = accentColor,
                fontFamily = DmSans,
                fontSize   = 15.sp,
            ),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = tagline,
            style = MaterialTheme.typography.labelSmall.copy(
                color    = accentColor.copy(alpha = 0.7f),
                fontSize = 11.sp,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text  = "Dekho →",
                style = MaterialTheme.typography.labelMedium.copy(
                    color    = accentColor,
                    fontSize = 11.sp,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// Shared helpers
// ─────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text     = label,
        style    = MaterialTheme.typography.labelLarge.copy(
            color         = TextMuted,
            letterSpacing = 0.8.sp,
        ),
        modifier = modifier.padding(bottom = 4.dp, top = 4.dp),
    )
}

// Fix: add modifier param to SearchResultCard so PoetShayariList can pass padding
@Composable
private fun SearchResultCard(
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp)
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
            Text(text = "— ${shayari.poet}", style = PoetNameStyle)
        }
        Spacer(Modifier.width(8.dp))
        Text("→", color = TextDisabled, fontSize = 16.sp)
    }
}

private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000f)}M"
    n >= 1_000     -> "${"%.1f".format(n / 1_000f)}k"
    else           -> "$n"
}