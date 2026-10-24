package com.kaizehuang.vernier.buildlogic.assets

import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Test

class ContributorRecordsTest {

    private fun remote(login: String, contributions: Int) =
        GitHubContributor(login, login, "https://github.com/$login", "https://a/$login", contributions, codeVolume = 0)

    @Test
    fun `GitHub records keep their recorded keys and get sanitised avatar files`() {
        val records = contributorRecords(
            listOf(remote("Some.User", 3)),
            recorded = mapOf("Some.User" to ContributorMetadata("summary", listOf("tee"))),
        )
        val record = records.first { it.login == "Some.User" }

        assertEquals("some_user.jpg", record.avatarFileName)
        assertEquals("github_contributors/avatars/some_user.jpg", record.avatarAssetPath)
        assertEquals("summary", record.summaryKey)
        assertEquals(listOf("tee"), record.contributionKeys)
        assertEquals(listOf("Some.User"), records.map { it.login })
    }

    @Test
    fun `records are ordered by contributions then code volume`() {
        val records = contributorRecords(
            listOf(
                remote("low", 1),
                remote("high", 40),
                remote("tie", 1),
            ),
            recorded = emptyMap(),
        )

        assertEquals(listOf("high", "low", "tie"), records.map { it.login })
    }

    @Test
    fun `metadata that cannot be read is empty`() {
        val directory = Files.createTempDirectory("contributors").toFile()
        val invalid = directory.resolve("invalid.json").apply { writeText("not json") }

        assertEquals(emptyMap<String, ContributorMetadata>(), readRecordedMetadata(directory.resolve("missing.json")))
        assertEquals(emptyMap<String, ContributorMetadata>(), readRecordedMetadata(invalid))
    }
}
