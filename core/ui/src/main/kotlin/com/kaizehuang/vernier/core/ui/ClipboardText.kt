package com.kaizehuang.vernier.core.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast

/**
 * Puts [text] on the clipboard under [label], and shows [confirmation] where the system does not
 * confirm the copy itself: from Android 13 it does for every clipboard write, and a toast would
 * repeat it.
 *
 * @return false when the device offers no clipboard.
 */
public fun copyPlainTextToClipboard(
    context: Context,
    label: String,
    text: String,
    confirmation: String,
): Boolean {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return false
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, confirmation, Toast.LENGTH_SHORT).show()
    }
    return true
}
