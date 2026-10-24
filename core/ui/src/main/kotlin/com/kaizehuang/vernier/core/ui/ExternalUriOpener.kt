package com.kaizehuang.vernier.core.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent

public fun openExternalUri(context: Context, rawUri: String): Boolean {
    val uri = runCatching { Uri.parse(rawUri) }.getOrNull() ?: return false
    if (CustomTabsClient.getPackageName(context, null)?.isNotBlank() == true) {
        val customTabOpened = runCatching {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(context, uri)
        }.isSuccess
        if (customTabOpened) {
            return true
        }
    }

    val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (intent.resolveActivity(context.packageManager) == null) {
        return false
    }
    return runCatching { context.startActivity(intent) }.isSuccess
}
