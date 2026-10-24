package com.kaizehuang.vernier.features.update.data

import com.kaizehuang.vernier.features.update.domain.UpdateChangelogEntry
import org.json.JSONObject

internal data class GitHubComparePage(
    val totalCommits: Int,
    val commits: List<UpdateChangelogEntry>,
)

internal class GitHubCompareParser {

    fun parse(rawJson: String): GitHubComparePage {
        val root = JSONObject(rawJson)
        val totalCommits = root.optInt("total_commits", 0).coerceAtLeast(0)
        val commitsJson = root.optJSONArray("commits") ?: return GitHubComparePage(
            totalCommits = totalCommits,
            commits = emptyList(),
        )
        val commits = buildList {
            for (index in 0 until commitsJson.length()) {
                val item = commitsJson.optJSONObject(index) ?: continue
                val parents = item.optJSONArray("parents")
                if (parents != null && parents.length() > 1) {
                    continue
                }
                val sha = item.optString("sha", "").lowercase()
                val commit = item.optJSONObject("commit") ?: continue
                val subject = commit.optString("message", "")
                    .lineSequence()
                    .firstOrNull()
                    .orEmpty()
                    .trim()
                if (!COMMIT_SHA_REGEX.matches(sha) || subject.isBlank()) {
                    continue
                }
                val authorName = commit.optJSONObject("author")
                    ?.optString("name", "")
                    ?.trim()
                    .orEmpty()
                add(
                    UpdateChangelogEntry(
                        sha = sha,
                        subject = subject,
                        authorName = authorName,
                    ),
                )
            }
        }
        return GitHubComparePage(
            totalCommits = totalCommits,
            commits = commits,
        )
    }

    private companion object {
        private val COMMIT_SHA_REGEX = Regex("^[0-9a-f]{40}$")
    }
}
