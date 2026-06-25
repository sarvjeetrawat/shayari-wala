package com.kunpitech.shayariwala.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import com.kunpitech.shayariwala.data.model.Shayari

object ShareUtils {

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Shayari", text))
    }

    fun shareShayari(context: Context, shayari: Shayari) {
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
}
