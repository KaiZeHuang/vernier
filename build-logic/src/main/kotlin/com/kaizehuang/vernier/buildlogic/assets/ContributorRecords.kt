package com.kaizehuang.vernier.buildlogic.assets

import java.io.File
import org.json.JSONArray
import org.json.JSONException

internal const val GITHUB_CONTRIBUTORS_AVATAR_DIRECTORY = "github_contributors/avatars"

/** One contributor as the app's contributors asset records it. */
internal data class ContributorRecord(
    val login: String,
    val name: String,
    val profileUrl: String,
    val avatarUrl: String?,
    val avatarFileName: String,
    val avatarAssetPath: String,
    val contributions: Int,
    val codeVolume: Int,
    val summaryKey: String?,
    val contributionKeys: List<String>,
)

/** The summary and contribution keys a contributor already has in the committed asset. */
internal data class ContributorMetadata(
    val summaryKey: String?,
    val contributionKeys: List<String>,
)

/**
 * The records to publish: GitHub's contributors carrying the keys [recorded] already holds for them,
 * in contribution order.
 */
internal fun contributorRecords(
    remote: List<GitHubContributor>,
    recorded: Map<String, ContributorMetadata>,
): List<ContributorRecord> {
    return remote
        .map { contributor ->
            val assetFileName = sanitizeAssetFileName(contributor.login) + ".jpg"
            val metadata = recorded[contributor.login]
            ContributorRecord(
                login = contributor.login,
                name = contributor.name,
                profileUrl = contributor.profileUrl,
                avatarUrl = contributor.avatarUrl,
                avatarFileName = assetFileName,
                avatarAssetPath = "$GITHUB_CONTRIBUTORS_AVATAR_DIRECTORY/$assetFileName",
                contributions = contributor.contributions,
                codeVolume = contributor.codeVolume,
                summaryKey = metadata?.summaryKey,
                contributionKeys = metadata?.contributionKeys ?: emptyList(),
            )
        }
        .distinctBy { it.login }
        .sortedWith(
            compareByDescending<ContributorRecord> { it.contributions }
                .thenByDescending { it.codeVolume }
                .thenBy { it.login.lowercase() },
        )
}

/** The keys recorded per login in an existing contributors asset; empty when there is none to read. */
internal fun readRecordedMetadata(file: File): Map<String, ContributorMetadata> {
    if (!file.isFile) {
        return emptyMap()
    }
    val array = try {
        JSONArray(file.readText(Charsets.UTF_8))
    } catch (_: JSONException) {
        return emptyMap()
    }
    return buildMap {
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val login = item.optString("login").trim()
            if (login.isBlank()) {
                continue
            }
            put(
                login,
                ContributorMetadata(
                    summaryKey = item.optString("summaryKey").trim().ifBlank { null },
                    contributionKeys = item.optJSONArray("contributionKeys").toStringList(),
                )
            )
        }
    }
}

private fun sanitizeAssetFileName(login: String): String {
    return buildString(login.length) {
        login.forEach { character ->
            append(
                when {
                    character.isLetterOrDigit() -> character.lowercaseChar()
                    character == '-' || character == '_' -> character
                    else -> '_'
                }
            )
        }
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
