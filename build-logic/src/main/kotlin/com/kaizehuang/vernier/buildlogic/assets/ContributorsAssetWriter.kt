package com.kaizehuang.vernier.buildlogic.assets

import java.io.File
import org.json.JSONArray
import org.json.JSONObject

/**
 * Writes [contributors] to [contributorsFile], downloads the avatar of each one with a remote avatar
 * into [avatarDirectory], and deletes the avatars no contributor names any more.
 */
internal fun writeContributorsAsset(
    contributors: List<ContributorRecord>,
    contributorsFile: File,
    avatarDirectory: File,
    downloadAvatar: (url: String) -> ByteArray,
) {
    val payload = JSONArray()
    val expectedAvatarFiles = linkedSetOf<String>()

    contributors.forEach { contributor ->
        expectedAvatarFiles += contributor.avatarFileName
        contributor.avatarUrl?.let { avatarUrl ->
            avatarDirectory.resolve(contributor.avatarFileName).writeBytes(downloadAvatar(avatarUrl))
        }
        payload.put(
            JSONObject()
                .put("login", contributor.login)
                .put("name", contributor.name)
                .put("profileUrl", contributor.profileUrl)
                .put("avatarAssetPath", contributor.avatarAssetPath)
                .put("contributions", contributor.contributions)
                .put("summaryKey", contributor.summaryKey)
                .put("contributionKeys", JSONArray(contributor.contributionKeys))
        )
    }

    avatarDirectory.listFiles().orEmpty()
        .filter { it.isFile && it.name !in expectedAvatarFiles }
        .forEach { it.delete() }

    contributorsFile.writeText(payload.toString(2), Charsets.UTF_8)
}
