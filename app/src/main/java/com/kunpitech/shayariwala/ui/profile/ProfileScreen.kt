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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.AggregateSource
import android.content.Context
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
import com.kunpitech.shayariwala.ui.theme.ErrorRed
import com.kunpitech.shayariwala.ui.theme.SuccessGreen
import com.kunpitech.shayariwala.ui.theme.shayariColors

@Composable
fun ProfileScreen(
    onShayariClick  : (String) -> Unit,
    onWriteClick    : () -> Unit,
    viewModel       : ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isDebug = remember(context) {
        (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

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
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
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

            // ── Developer Admin Section ────────────────
            if (isDebug) {
                item {
                    Spacer(Modifier.height(24.dp))
                    DeveloperSection(context = context)
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

@Composable
private fun DeveloperSection(context: Context) {
    val scope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = Color(0xFF1E1E2A), thickness = 0.5.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Developer Admin Actions",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextDisabled,
                fontFamily = DmSans,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        
        // Button 1: Upload Shayari
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141418))
                .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) {
                    isLoading = true
                    statusText = "Uploading Shayari..."
                    scope.launch {
                        try {
                            val jsonString = context.assets.open("bulk_shayari.json")
                                .bufferedReader()
                                .use { it.readText() }
                            val jsonArray = org.json.JSONArray(jsonString)
                            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            val batch = db.batch()
                            
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                val docRef = db.collection("shayari").document()
                                batch.set(
                                    docRef,
                                    mapOf(
                                        "hindiText" to obj.optString("hindiText", ""),
                                        "urduText" to obj.optString("urduText", ""),
                                        "poet" to obj.optString("poet", "Unknown"),
                                        "category" to obj.optString("category", "all"),
                                        "isTrending" to obj.optBoolean("isTrending", false),
                                        "likes" to 0,
                                        "comments" to 0,
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                )
                            }
                            
                            batch.commit().await()
                            statusText = "${jsonArray.length()} Shayari successfully uploaded! 🎉"
                        } catch (e: Exception) {
                            statusText = "Upload failed: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Uploading..." else "Bulk Upload Shayari (bulk_shayari.json)",
                color = if (isLoading) TextDisabled else Gold400,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = DmSans)
            )
        }
        
        Spacer(Modifier.height(10.dp))
        
        // Button 2: Upload Poets & Compute counts
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141418))
                .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) {
                    isLoading = true
                    statusText = "Uploading Poets..."
                    scope.launch {
                        try {
                            // 1. Read bulk_poets.json
                            val poetsString = context.assets.open("bulk_poets.json")
                                .bufferedReader()
                                .use { it.readText() }
                            val poetsArray = org.json.JSONArray(poetsString)
                            
                            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            val batch = db.batch()
                            
                            for (i in 0 until poetsArray.length()) {
                                val obj = poetsArray.getJSONObject(i)
                                val name = obj.optString("name", "")
                                if (name.isEmpty()) continue
                                
                                // Query the actual count of shayari for this poet in Firestore
                                val countSnapshot = db.collection("shayari")
                                    .whereEqualTo("poet", name)
                                    .count()
                                    .get(AggregateSource.SERVER)
                                    .await()
                                val count = countSnapshot.count.toInt()
                                
                                val docRef = db.collection("poets").document(name)
                                batch.set(
                                    docRef,
                                    mapOf(
                                        "name" to name,
                                        "urduName" to obj.optString("urduName", ""),
                                        "bio" to obj.optString("bio", ""),
                                        "category" to obj.optString("category", ""),
                                        "shayariCount" to count
                                    )
                                )
                            }
                            
                            batch.commit().await()
                            statusText = "${poetsArray.length()} Poets successfully uploaded with dynamic counts! 🎉"
                        } catch (e: Exception) {
                            statusText = "Upload failed: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Uploading..." else "Bulk Upload Poets (bulk_poets.json)",
                color = if (isLoading) TextDisabled else Gold400,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = DmSans)
            )
        }
        
        Spacer(Modifier.height(10.dp))
        
        // Button 3: Check Database Stats
        var statsText by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF141418))
                .border(0.5.dp, Color(0xFF2A2A3A), RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) {
                    isLoading = true
                    statsText = "Loading stats..."
                    scope.launch {
                        try {
                            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            
                            // 1. Get total shayari count
                            val totalSnapshot = db.collection("shayari").count().get(AggregateSource.SERVER).await()
                            val totalShayari = totalSnapshot.count
                            
                            // 2. Get count for each poet
                            val poetsString = context.assets.open("bulk_poets.json")
                                .bufferedReader()
                                .use { it.readText() }
                            val poetsArray = org.json.JSONArray(poetsString)
                            val sb = StringBuilder()
                            sb.append("Total Shayari in DB: $totalShayari\n\nPoet counts in DB:\n")
                            
                            for (i in 0 until poetsArray.length()) {
                                val name = poetsArray.getJSONObject(i).optString("name", "")
                                if (name.isEmpty()) continue
                                val countSnapshot = db.collection("shayari")
                                    .whereEqualTo("poet", name)
                                    .count()
                                    .get(AggregateSource.SERVER)
                                    .await()
                                sb.append("• $name: ${countSnapshot.count}\n")
                            }
                            statsText = sb.toString()
                        } catch (e: Exception) {
                            statsText = "Failed to load stats: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Loading..." else "Check Database Stats",
                color = if (isLoading) TextDisabled else Gold400,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = DmSans)
            )
        }
        
        if (statusText.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = statusText,
                color = if (statusText.startsWith("Upload failed")) ErrorRed else SuccessGreen,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = DmSans,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        
        if (statsText.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = statsText,
                color = TextPrimary,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = DmSans,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}