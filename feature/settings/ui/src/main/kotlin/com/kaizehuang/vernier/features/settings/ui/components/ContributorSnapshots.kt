package com.kaizehuang.vernier.features.settings.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaizehuang.vernier.features.settings.ui.R
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Assemblyscript
import compose.icons.simpleicons.Cplusplus
import compose.icons.simpleicons.Figma
import compose.icons.simpleicons.Kotlin
import org.json.JSONArray

internal data class AuthorProfile(
    val login: String,
    val name: String,
    val profileUrl: String,
    val avatarAssetPath: String?,
    val contributionSummary: String,
    val contributions: List<AuthorContribution>,
)

internal data class ContributorSnapshot(
    val login: String,
    val name: String,
    val profileUrl: String,
    val avatarAssetPath: String?,
    val summaryKey: String?,
    val contributionKeys: List<String>,
)

internal sealed class AuthorContribution(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
) {
    data object Ui : AuthorContribution(
        label = "UI",
        icon = SimpleIcons.Figma,
        tint = Color(0xFFF24E1E),
    )

    data object Cpp : AuthorContribution(
        label = "C++",
        icon = SimpleIcons.Cplusplus,
        tint = Color(0xFF00599C),
    )

    data object Asm : AuthorContribution(
        label = "ASM",
        icon = SimpleIcons.Assemblyscript,
        tint = Color(0xFF007AAC),
    )

    data object Kotlin : AuthorContribution(
        label = "Kotlin",
        icon = SimpleIcons.Kotlin,
        tint = Color(0xFF7F52FF),
    )

    data object Security : AuthorContribution(
        label = "Security",
        icon = Icons.Rounded.BugReport,
        tint = Color(0xFFD32F2F),
    )
}

internal fun loadContributorSnapshots(context: android.content.Context): List<ContributorSnapshot> {
    val assetManager = context.assets
    val payload = runCatching {
        assetManager.open(CONTRIBUTORS_ASSET_FILE_NAME).bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readText()
        }
    }.getOrNull() ?: return emptyList()
    return runCatching {
        val array = JSONArray(payload)
        buildList(array.length()) {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val login = item.optString("login").trim()
                val name = item.optString("name").trim().ifBlank { login }
                if (login.isBlank()) {
                    continue
                }
                add(
                    ContributorSnapshot(
                        login = login,
                        name = name,
                        profileUrl = item.optString("profileUrl").trim().ifBlank { "https://github.com/$login" },
                        avatarAssetPath = item.optString("avatarAssetPath").trim().ifBlank { null },
                        summaryKey = item.optString("summaryKey").trim().ifBlank { null },
                        contributionKeys = item.optJSONArray("contributionKeys").toStringList(),
                    )
                )
            }
        }
    }.getOrDefault(emptyList())
}

internal fun summaryResIdForKey(summaryKey: String?): Int {
    return when (summaryKey) {
        "author_summary_owner" -> R.string.author_summary_owner
        else -> R.string.author_summary_default
    }
}

internal fun authorContributionForKey(key: String): AuthorContribution? {
    return when (key.lowercase()) {
        "ui" -> AuthorContribution.Ui
        "cpp" -> AuthorContribution.Cpp
        "asm" -> AuthorContribution.Asm
        "kotlin" -> AuthorContribution.Kotlin
        "security" -> AuthorContribution.Security
        else -> null
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) {
        return emptyList()
    }
    return buildList(length()) {
        for (index in 0 until length()) {
            val value = optString(index).trim()
            if (value.isNotEmpty()) {
                add(value)
            }
        }
    }
}

private const val CONTRIBUTORS_ASSET_FILE_NAME = "github_contributors.json"
