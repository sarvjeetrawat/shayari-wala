package com.kunpitech.shayariwala.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.ui.theme.Bg500
import com.kunpitech.shayariwala.ui.theme.Bg600
import com.kunpitech.shayariwala.ui.theme.Bg700
import com.kunpitech.shayariwala.ui.theme.Border100
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.IshqPink
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextSecondary
import com.kunpitech.shayariwala.ui.theme.shayariColors

// ── Category helpers ───────────────────────────────────────────────────────
@Composable
fun categoryColor(category: String): Color {
    val ext = MaterialTheme.shayariColors
    return when (category.lowercase()) {
        "ishq"    -> ext.ishq
        "dard"    -> ext.dard
        "zindagi" -> ext.zindagi
        "khushi"  -> ext.khushi
        "judai"   -> ext.judai
        "wafa"    -> ext.wafa
        else      -> Gold400
    }
}

@Composable
fun categoryDimColor(category: String): Color {
    val ext = MaterialTheme.shayariColors
    return when (category.lowercase()) {
        "ishq"    -> ext.ishqDim
        "dard"    -> ext.dardDim
        "zindagi" -> ext.zindagiDim
        "khushi"  -> ext.khushiDim
        "judai"   -> ext.judaiDim
        "wafa"    -> ext.wafaDim
        else      -> ext.accentGoldSubtle
    }
}

fun categoryLabel(category: String): String = when (category.lowercase()) {
    "ishq"    -> "♥ Ishq"
    "dard"    -> "💧 Dard"
    "zindagi" -> "✦ Zindagi"
    "khushi"  -> "☀ Khushi"
    "judai"   -> "☽ Judai"
    "wafa"    -> "◈ Wafa"
    else      -> category.replaceFirstChar { it.uppercaseChar() }
}

fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000f)}M"
    n >= 1_000     -> "${"%.1f".format(n / 1_000f)}k"
    else           -> "$n"
}

// ── Main Card with Left Color Bar ──────────────────────────────────────────
@Composable
fun ShayariCard(
    shayari  : Shayari,
    isLiked  : Boolean,
    onLike   : () -> Unit,
    onShare  : () -> Unit,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
) {
    val accentColor = categoryColor(shayari.category)

    val likeScale by animateFloatAsState(
        targetValue   = if (isLiked) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "likeScale",
    )
    val likeColor by animateColorAsState(
        targetValue   = if (isLiked) IshqPink else TextDisabled,
        animationSpec = tween(300),
        label         = "likeColor",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Bg700)
                .border(0.5.dp, Bg500, RoundedCornerShape(16.dp))
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // ── Left Color Bar ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
            )

            // ── Card Content ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {

                // ── Row 1: category dot + label ───────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(accentColor, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = categoryLabel(shayari.category),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color      = accentColor,
                            fontFamily = DmSans,
                            fontSize   = 11.sp,
                            letterSpacing = 1.sp,
                        ),
                    )
                    if (shayari.isTrending) {
                        Spacer(Modifier.width(8.dp))
                        TrendingBadge()
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Row 2: shayari text ───────────────────────────────────
                Text(
                    text  = shayari.hindiText,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(Modifier.height(10.dp))

                // ── Row 3: poet name ──────────────────────────────────────
                Text(
                    text  = "— ${shayari.poet}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color      = TextMuted,
                        fontFamily = DmSans,
                        fontSize   = 12.sp,
                    ),
                )

                Spacer(Modifier.height(14.dp))

                // ── Row 4: actions ────────────────────────────────────────
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    // Like icon + count
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onLike() }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector        = if (isLiked) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint               = likeColor,
                                modifier           = Modifier
                                    .size(16.dp)
                                    .scale(likeScale),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text  = formatCount(
                                    if (isLiked) shayari.likes + 1 else shayari.likes
                                ),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color      = likeColor,
                                    fontFamily = DmSans,
                                    fontSize   = 13.sp,
                                ),
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Comment icon + count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Outlined.ModeComment,
                            contentDescription = "Comments",
                            tint               = TextDisabled,
                            modifier           = Modifier.size(15.dp),
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

                    // Share pill button
                    Box(
                        modifier         = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Bg600)
                            .border(0.5.dp, Border100, RoundedCornerShape(20.dp))
                            .clickable { onShare() }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
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

// ── Trending badge ─────────────────────────────────────────────────────────
@Composable
private fun TrendingBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Gold400.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text  = "TRENDING",
            style = MaterialTheme.typography.labelSmall.copy(
                color         = Gold400,
                fontFamily    = DmSans,
                fontSize      = 9.sp,
                letterSpacing = 0.8.sp,
            ),
        )
    }
}