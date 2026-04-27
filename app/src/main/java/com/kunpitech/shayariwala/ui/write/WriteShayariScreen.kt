package com.kunpitech.shayariwala.ui.write

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kunpitech.shayariwala.ui.components.categoryColor
import com.kunpitech.shayariwala.ui.components.categoryDimColor
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextPrimary
import com.kunpitech.shayariwala.ui.theme.TextSecondary
import com.kunpitech.shayariwala.ui.theme.shayariColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WriteShayariScreen(
    onBack      : () -> Unit,
    onSubmitted : () -> Unit,
    viewModel   : WriteViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ext = MaterialTheme.shayariColors
    val scrollState = rememberScrollState()

    // Navigate back after successful submit
    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onSubmitted()
            viewModel.reset()
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(scrollState),
            ) {

                // ── Top bar ───────────────────────────
                WriteTopBar(
                    onBack       = onBack,
                    isSubmitting = uiState.isSubmitting,
                    canSubmit    = uiState.hindiText.isNotBlank() && uiState.poet.isNotBlank(),
                    onSubmit     = viewModel::submit,
                )

                // ── Subtitle ──────────────────────────
                Text(
                    text     = "Apne dil ki baat likho",
                    style    = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = PlayfairDisplay,
                        fontStyle  = FontStyle.Italic,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )

                Spacer(Modifier.height(16.dp))

                // ── Hindi shayari field ───────────────
                FieldLabel(
                    label    = "Shayari (Hindi / Roman Urdu)",
                    required = true,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(6.dp))
                ShayariTextField(
                    value        = uiState.hindiText,
                    onValueChange= viewModel::onHindiTextChange,
                    placeholder  = "Yahan apni shayari likho...\nDil se likho, alfaaz khud aayenge...",
                    minLines     = 5,
                    isPoetic     = true,
                    modifier     = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(16.dp))

                // ── Urdu field ────────────────────────
                FieldLabel(
                    label    = "Urdu Script (Ikhtiyaari)",
                    required = false,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(6.dp))
                ShayariTextField(
                    value         = uiState.urduText,
                    onValueChange = viewModel::onUrduTextChange,
                    placeholder   = "اردو میں لکھیں (اختیاری)...",
                    minLines      = 3,
                    isPoetic      = false,
                    isRtl         = true,
                    modifier      = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(16.dp))

                // ── Poet name field ───────────────────
                FieldLabel(
                    label    = "Aapka Naam",
                    required = true,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(6.dp))
                NameTextField(
                    value         = uiState.poet,
                    onValueChange = viewModel::onPoetChange,
                    placeholder   = "Shayar ka naam...",
                    modifier      = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    color     = ext.divider,
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(Modifier.height(16.dp))

                // ── Category picker ───────────────────
                FieldLabel(
                    label    = "Category Chuniye",
                    required = false,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Spacer(Modifier.height(10.dp))

                FlowRow(
                    modifier             = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement= Arrangement.spacedBy(8.dp),
                    verticalArrangement  = Arrangement.spacedBy(8.dp),
                ) {
                    writeCategories.forEach { cat ->
                        CategoryChip(
                            category   = cat,
                            isSelected = cat == uiState.selectedCategory,
                            onClick    = { viewModel.onCategoryChange(cat) },
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Live preview ──────────────────────
                if (uiState.hindiText.isNotBlank()) {
                    HorizontalDivider(
                        color     = ext.divider,
                        thickness = 0.5.dp,
                        modifier  = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    FieldLabel(
                        label    = "Preview",
                        required = false,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    ShayariPreviewCard(uiState = uiState)
                    Spacer(Modifier.height(16.dp))
                }

                // ── Error message ─────────────────────
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter   = fadeIn(tween(200)),
                    exit    = fadeOut(tween(200)),
                ) {
                    Text(
                        text     = uiState.error ?: "",
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                }

                Spacer(Modifier.height(40.dp))
            }

            // ── Full screen submitting overlay ────────
            AnimatedVisibility(
                visible = uiState.isSubmitting,
                enter   = fadeIn(tween(200)) + scaleIn(tween(200)),
                exit    = fadeOut(tween(200)) + scaleOut(tween(200)),
                modifier= Modifier.align(Alignment.Center),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ext.cardBackground)
                        .border(0.5.dp, ext.cardBorder, RoundedCornerShape(20.dp))
                        .padding(32.dp),
                ) {
                    CircularProgressIndicator(color = Gold400, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Shayari bhej rahe hain...", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────
@Composable
private fun WriteTopBar(
    onBack       : () -> Unit,
    isSubmitting : Boolean,
    canSubmit    : Boolean,
    onSubmit     : () -> Unit,
) {
    val ext = MaterialTheme.shayariColors
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ext.iconButtonBg)
                .border(0.5.dp, ext.cardBorder, CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Text("←", color = TextMuted, fontSize = 16.sp)
        }

        Text(
            text  = "✦  Shayari Likho",
            style = MaterialTheme.typography.headlineMedium,
        )

        // Publish button
        Box(
            modifier         = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (canSubmit && !isSubmitting) Gold400
                    else Gold400.copy(alpha = 0.3f)
                )
                .clickable(enabled = canSubmit && !isSubmitting) { onSubmit() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Check,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.background,
                    modifier           = Modifier.size(14.dp),
                )
                Text(
                    text  = "Post",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color    = MaterialTheme.colorScheme.background,
                        fontSize = 13.sp,
                    ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Input fields
// ─────────────────────────────────────────────────────────
@Composable
private fun FieldLabel(
    label    : String,
    required : Boolean,
    modifier : Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelLarge.copy(color = TextMuted),
        )
        if (required) {
            Spacer(Modifier.width(4.dp))
            Text("*", color = Gold400, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ShayariTextField(
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    minLines      : Int,
    isPoetic      : Boolean,
    isRtl         : Boolean = false,
    modifier      : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors

    BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        minLines      = minLines,
        cursorBrush   = SolidColor(Gold400),
        textStyle     = if (isPoetic) {
            MaterialTheme.typography.bodyLarge.copy(
                textAlign = if (isRtl) TextAlign.End else TextAlign.Start,
            )
        } else {
            MaterialTheme.typography.bodySmall.copy(
                fontFamily = DmSans,
                fontSize   = 14.sp,
                color      = TextSecondary,
                textAlign  = if (isRtl) TextAlign.End else TextAlign.Start,
            )
        },
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(
                    text      = placeholder,
                    style     = MaterialTheme.typography.bodySmall.copy(
                        color     = TextDisabled,
                        fontSize  = 13.sp,
                        textAlign = if (isRtl) TextAlign.End else TextAlign.Start,
                    ),
                    modifier  = Modifier.fillMaxWidth(),
                )
            }
            inner()
        },
    )
}

@Composable
private fun NameTextField(
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    modifier      : Modifier = Modifier,
) {
    val ext = MaterialTheme.shayariColors
    BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, ext.cardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        singleLine    = true,
        cursorBrush   = SolidColor(Gold400),
        textStyle     = MaterialTheme.typography.titleSmall.copy(color = TextPrimary),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(
                    text  = placeholder,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDisabled),
                )
            }
            inner()
        },
    )
}

// ─────────────────────────────────────────────────────────
// Category chip
// ─────────────────────────────────────────────────────────
@Composable
private fun CategoryChip(
    category   : String,
    isSelected : Boolean,
    onClick    : () -> Unit,
) {
    val accentColor = categoryColor(category)
    val dimColor    = categoryDimColor(category)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) dimColor else MaterialTheme.shayariColors.cardBackground)
            .border(
                width = if (isSelected) 1.dp else 0.5.dp,
                color = if (isSelected) accentColor.copy(alpha = 0.6f)
                else MaterialTheme.shayariColors.cardBorder,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = categoryLabel(category),
            style = MaterialTheme.typography.labelLarge.copy(
                color      = if (isSelected) accentColor else TextMuted,
                fontFamily = DmSans,
                fontSize   = 12.sp,
            ),
        )
    }
}

// ─────────────────────────────────────────────────────────
// Live Preview Card
// ─────────────────────────────────────────────────────────
@Composable
private fun ShayariPreviewCard(uiState: WriteUiState) {
    val ext         = MaterialTheme.shayariColors
    val accentColor = categoryColor(uiState.selectedCategory)
    val dimColor    = categoryDimColor(uiState.selectedCategory)

    Column(
        modifier            = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ext.cardBackground)
            .border(0.5.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(dimColor)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text  = categoryLabel(uiState.selectedCategory).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color         = accentColor,
                    letterSpacing = 0.8.sp,
                ),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text      = uiState.hindiText.trim(),
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        if (uiState.urduText.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text      = uiState.urduText.trim(),
                style     = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = com.kunpitech.shayariwala.ui.theme.NotoNastaliq,
                    color      = TextMuted,
                    fontSize   = 13.sp,
                ),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 1.dp)
                .background(accentColor.copy(alpha = 0.4f))
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text  = "— ${uiState.poet.ifBlank { "Aapka Naam" }}",
            style = MaterialTheme.typography.labelMedium.copy(color = TextDisabled),
        )
    }
}