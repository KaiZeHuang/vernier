package com.kaizehuang.vernier.features.update.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UpdateManifestParserTest {
    private val parser = UpdateManifestParser()

    @Test
    fun `parses a valid legacy master Nightly manifest`() {
        val manifest = parser.parse(validUpdateManifestJson())

        assertEquals(1, manifest.schemaVersion)
        assertEquals("nightly", manifest.channel)
        assertEquals("master", manifest.branch)
        assertEquals(TEST_HEAD_SHA, manifest.commit.sha)
        assertEquals(500, manifest.versionCode)
        assertEquals("Vernier.Detector-test.apk", manifest.apk.name)
    }

    @Test
    fun `parses a valid main Nightly manifest`() {
        val manifest = parser.parse(validUpdateManifestJson(branch = "main"))

        assertEquals("main", manifest.branch)
    }

    @Test
    fun `rejects an unexpected Nightly branch`() {
        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(validUpdateManifestJson(branch = "development"))
        }
    }

    @Test
    fun `rejects unsupported schema`() {
        val json = JSONObject(validUpdateManifestJson())
            .put("schemaVersion", 2)
            .toString()

        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(json)
        }
    }

    @Test
    fun `rejects an invalid build timestamp`() {
        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(validUpdateManifestJson(builtAtUtc = "2026-08-08 12:30"))
        }
    }

    @Test
    fun `rejects download URLs outside the official Nightly release`() {
        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(
                validUpdateManifestJson(
                    downloadUrl = "https://example.com/Vernier.Detector-test.apk",
                ),
            )
        }
    }

    @Test
    fun `rejects a mismatched APK filename`() {
        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(
                validUpdateManifestJson(
                    downloadUrl =
                        "https://github.com/KaiZeHuang/vernier/releases/download/nightly/another.apk",
                ),
            )
        }
    }

    @Test
    fun `rejects numeric values encoded as strings`() {
        val json = JSONObject(validUpdateManifestJson())
            .put("versionCode", "500")
            .toString()

        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(json)
        }
    }

    @Test
    fun `rejects unsafe APK asset names`() {
        val json = JSONObject(validUpdateManifestJson())
        json.getJSONObject("apk")
            .put("name", "../Vernier.Detector-test.apk")

        assertThrows(UpdateManifestValidationException::class.java) {
            parser.parse(json.toString())
        }
    }
}
