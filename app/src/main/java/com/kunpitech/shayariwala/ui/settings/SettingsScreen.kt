package com.kunpitech.shayariwala.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunpitech.shayariwala.ui.theme.Bg500
import com.kunpitech.shayariwala.ui.theme.Bg600
import com.kunpitech.shayariwala.ui.theme.Bg700
import com.kunpitech.shayariwala.ui.theme.Bg900
import com.kunpitech.shayariwala.ui.theme.Border100
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.Gold400
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import com.kunpitech.shayariwala.ui.theme.TextDisabled
import com.kunpitech.shayariwala.ui.theme.TextMuted
import com.kunpitech.shayariwala.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    val versionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    Scaffold(
        containerColor = Bg900,
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Top Bar ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Bg600)
                        .border(0.5.dp, Border100, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Gold400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = PlayfairDisplay,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Settings Group Card ──────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Bg700)
                    .border(0.5.dp, Bg500, RoundedCornerShape(16.dp))
            ) {
                // 1. Share App
                SettingsRow(
                    icon = Icons.Outlined.Share,
                    title = "Share ShayariWala",
                    subtitle = "App doston ke sath share karein",
                    onClick = { shareApp(context) }
                )

                HorizontalDivider(color = Bg500, thickness = 0.5.dp)

                // 2. Rate App
                SettingsRow(
                    icon = Icons.Outlined.StarOutline,
                    title = "Rate Us",
                    subtitle = "Play Store par rating dein",
                    onClick = { rateApp(context) }
                )

                HorizontalDivider(color = Bg500, thickness = 0.5.dp)

                // 3. Privacy Policy
                SettingsRow(
                    icon = Icons.Outlined.Lock,
                    title = "Privacy Policy",
                    subtitle = "Humari privacy policy padhein",
                    onClick = { openPrivacy(context) }
                )

                HorizontalDivider(color = Bg500, thickness = 0.5.dp)

                // 4. About Developer
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    title = "About App",
                    subtitle = "Developer aur app ke baare mein",
                    onClick = { showAboutDialog = true }
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Footer ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ShayariWala",
                    fontFamily = PlayfairDisplay,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold400
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Version $versionName",
                    fontFamily = DmSans,
                    fontSize = 11.sp,
                    color = TextDisabled
                )
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = Bg700,
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = "About ShayariWala",
                    fontFamily = PlayfairDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Gold400
                )
            },
            text = {
                Column {
                    Text(
                        text = "ShayariWala is a premium Hindi and Urdu Shayari application designed to let you explore, save, and share beautiful poetic expressions. Browse through carefully curated categories, customize designs, and share Shayari as customizable images or text.",
                        fontFamily = DmSans,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Crafted with ❤️ by KunpiTech",
                        fontFamily = DmSans,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showAboutDialog = false }
                ) {
                    Text(
                        text = "Close",
                        fontFamily = DmSans,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold400
                    )
                }
            },
            modifier = Modifier.border(0.5.dp, Border100, RoundedCornerShape(28.dp))
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Bg600)
                .border(0.5.dp, Border100, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold400,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // Text labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = DmSans,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = DmSans,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        // Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Actions ──────────────────────────────────────────────────

private fun shareApp(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out ShayariWala app for premium Hindi and Urdu Shayari: https://play.google.com/store/apps/details?id=${context.packageName}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share App"))
}

private fun rateApp(context: Context) {
    val uri = Uri.parse("market://details?id=${context.packageName}")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        context.startActivity(goToMarket)
    } catch (e: Exception) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${context.packageName}")
            )
        )
    }
}

private fun openPrivacy(context: Context) {
    val browserIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://docs.google.com/document/d/11zvGYf0jQEEBomBZrf2ljBxtfP80pVt2vBwtlPqVZWc/edit?usp=sharing")
    )
    context.startActivity(browserIntent)
}

// openAbout removed
