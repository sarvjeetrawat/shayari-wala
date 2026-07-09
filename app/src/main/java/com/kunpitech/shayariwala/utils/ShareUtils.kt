package com.kunpitech.shayariwala.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.LinearGradient
import android.graphics.Typeface
import android.text.TextPaint
import android.text.StaticLayout
import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import android.graphics.Path
import android.graphics.RectF
import com.kunpitech.shayariwala.data.model.Shayari
import com.kunpitech.shayariwala.ui.components.categoryLabel
import com.kunpitech.shayariwala.ui.theme.DmSans
import com.kunpitech.shayariwala.ui.theme.PlayfairDisplay
import java.io.File
import java.io.FileOutputStream

object ShareManager {
    var activeShayari by mutableStateOf<Shayari?>(null)
        private set

    fun triggerShare(shayari: Shayari) {
        activeShayari = shayari
    }

    fun dismiss() {
        activeShayari = null
    }
}

@Composable
fun ShareDialog(
    context: Context,
    onDismiss: () -> Unit
) {
    val shayari = ShareManager.activeShayari ?: return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF111118), // Deep dark matching cardBackground
            border = BorderStroke(0.5.dp, Color(0x33C9A96E)), // 20% opacity gold border
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Share Shayari",
                    fontSize = 18.sp,
                    fontFamily = PlayfairDisplay,
                    color = Color(0xFFC9A96E), // Premium gold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Option 1: Share as formatted text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF161622)) // Slightly lighter dark surface
                        .clickable {
                            ShareUtils.shareAsText(context, shayari)
                            onDismiss()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = "Text Option",
                        tint = Color(0xFFC9A96E),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Share as Formatted Text",
                            fontSize = 14.sp,
                            color = Color(0xFFF5E6C8),
                            fontFamily = DmSans
                        )
                        Text(
                            text = "Formatted message with download link",
                            fontSize = 10.sp,
                            color = Color(0xFF888880),
                            fontFamily = DmSans
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option 2: Share as custom Canvas-drawn Image card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF161622))
                        .clickable {
                            ShareUtils.shareAsImage(context, shayari)
                            onDismiss()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Image Option",
                        tint = Color(0xFFC9A96E),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Share as Image Card",
                            fontSize = 14.sp,
                            color = Color(0xFFF5E6C8),
                            fontFamily = DmSans
                        )
                        Text(
                            text = "Beautiful graphic for Status & Stories",
                            fontSize = 10.sp,
                            color = Color(0xFF888880),
                            fontFamily = DmSans
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFFC9A96E),
                        fontFamily = DmSans,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

object ShareUtils {

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Shayari", text))
    }

    fun shareShayari(context: Context, shayari: Shayari) {
        // Trigger global share state dialog instead of showing system Alert Dialog
        ShareManager.triggerShare(shayari)
    }

    fun shareAsText(context: Context, shayari: Shayari) {
        val shareText = buildString {
            appendLine("✨ *ShayariWala* ✨")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine(shayari.hindiText)
            if (shayari.urduText.isNotBlank()) {
                appendLine()
                appendLine(shayari.urduText)
            }
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("✍️ *${shayari.poet}*")
            appendLine()
            appendLine("📲 Download ShayariWala App for more:")
            append("👉 https://play.google.com/store/apps/details?id=${context.packageName}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type    = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Shayari"))
    }

    private fun getHeaderColor(category: String): Int {
        return when (category.lowercase()) {
            "ishq" -> 0xFF281C1C.toInt() // Deep reddish warm tint
            "dard", "judai" -> 0xFF1C1D2A.toInt() // Deep slate/blue tint
            "zindagi", "wafa", "dosti" -> 0xFF18221D.toInt() // Deep forest/teal tint
            "khushi", "mazahiya" -> 0xFF241F1A.toInt() // Deep warm bronze/gold tint
            "hosla", "inspiration" -> 0xFF1C2222.toInt() // Deep dark cyan/grey tint
            else -> 0xFF1E1D1C.toInt()
        }
    }

    fun shareAsImage(context: Context, shayari: Shayari) {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Path for rounded corner card bounding box
        val cardPath = Path().apply {
            val rect = RectF(32f, 32f, (width - 32).toFloat(), (height - 32).toFloat())
            addRoundRect(rect, 64f, 64f, Path.Direction.CW)
        }

        val paint = Paint().apply { isAntiAlias = true }

        // Clip drawing to rounded card path
        canvas.save()
        canvas.clipPath(cardPath)

        // 2. Entire card background (deep dark slate black)
        paint.color = 0xFF0E0D12.toInt()
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // 3. Draw top header background color
        val headerColor = getHeaderColor(shayari.category)
        paint.color = headerColor
        canvas.drawRect(32f, 32f, (width - 32).toFloat(), 260f, paint)

        canvas.restore()

        // 4. Draw outer gold borders
        paint.color = 0x22C9A96E.toInt() // 12% opacity gold
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawPath(cardPath, paint)

        // 5. Draw Category Badge Pill inside Header
        val pillRect = RectF(380f, 116f, 700f, 176f)
        paint.color = 0x33C9A96E.toInt() // 20% opacity gold border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRoundRect(pillRect, 30f, 30f, paint)

        val chipPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFFC9A96E.toInt() // Premium Gold text
            textSize = 22f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val chipText = categoryLabel(shayari.category).uppercase()
        val chipTextY = 146f - ((chipPaint.descent() + chipPaint.ascent()) / 2f)
        canvas.drawText(chipText, (width / 2).toFloat(), chipTextY, chipPaint)

        // 6. Draw Hindi Shayari Text using StaticLayout
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = 0xFFF5E6C8.toInt() // Cream
            textSize = 44f
            typeface = Typeface.create("serif", Typeface.ITALIC)
        }

        val textWidth = width - 200
        val hindiLayout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(shayari.hindiText, 0, shayari.hindiText.length, textPaint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(14f, 1f)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                shayari.hindiText,
                textPaint,
                textWidth,
                Layout.Alignment.ALIGN_CENTER,
                1f,
                14f,
                true
            )
        }

        // Draw Hindi Shayari
        canvas.save()
        canvas.translate(((width - textWidth) / 2).toFloat(), 340f)
        hindiLayout.draw(canvas)
        canvas.restore()

        // 7. Middle star divider
        val dividerY = 560f
        val linePaint = Paint().apply {
            isAntiAlias = true
            color = 0x33C9A96E.toInt() // 20% gold
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        canvas.drawLine(150f, dividerY, 480f, dividerY, linePaint)
        canvas.drawLine(590f, dividerY, 930f, dividerY, linePaint)

        val symbolPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFFC9A96E.toInt() // Gold
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        val symbolY = dividerY - ((symbolPaint.descent() + symbolPaint.ascent()) / 2f)
        canvas.drawText("✦", (width / 2).toFloat(), symbolY, symbolPaint)

        // 8. Draw Urdu Shayari Text using StaticLayout
        val urduLayout = if (shayari.urduText.isNotBlank()) {
            val urduTextPaint = TextPaint().apply {
                isAntiAlias = true
                color = 0xFFC5B89F.toInt() // Dim cream
                textSize = 40f
                typeface = Typeface.create("serif", Typeface.NORMAL)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(shayari.urduText, 0, shayari.urduText.length, urduTextPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(14f, 1f)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(
                    shayari.urduText,
                    urduTextPaint,
                    textWidth,
                    Layout.Alignment.ALIGN_CENTER,
                    1f,
                    14f,
                    true
                )
            }
        } else null

        // Draw Urdu Shayari
        if (urduLayout != null) {
            canvas.save()
            canvas.translate(((width - textWidth) / 2).toFloat(), 620f)
            urduLayout.draw(canvas)
            canvas.restore()
        }

        // 9. Poet Divider (short horizontal line)
        val poetDividerY = 820f
        canvas.drawLine(470f, poetDividerY, 610f, poetDividerY, linePaint)

        // 10. Poet Name
        val poetPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFF8E8A84.toInt() // Muted gold/grey
            textSize = 32f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("serif", Typeface.ITALIC)
        }
        canvas.drawText("— ${shayari.poet}", (width / 2).toFloat(), 880f, poetPaint)

        // 11. Subtle Watermark at the very bottom
        val watermarkPaint = Paint().apply {
            isAntiAlias = true
            color = 0x22F5E6C8.toInt() // 12% opacity
            textSize = 18f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        canvas.drawText("ShayariWala App", (width / 2).toFloat(), 995f, watermarkPaint)

        // 12. Save image to cache and share
        try {
            val file = File(context.cacheDir, "shayari_card.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Shayari Card"))
        } catch (e: Exception) {
            e.printStackTrace()
            shareAsText(context, shayari)
        }
    }
}


