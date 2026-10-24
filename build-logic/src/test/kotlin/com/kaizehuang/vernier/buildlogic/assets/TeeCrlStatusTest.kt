package com.kaizehuang.vernier.buildlogic.assets

import org.gradle.api.GradleException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class TeeCrlStatusTest {

    @Test
    fun `the fallback floor keeps its entries and gains only remote-only ones`() {
        val remote = """{"entries": {"aa": {"status": "REVOKED"}, "bb": {"status": "SUSPENDED"}}}"""
        val fallback = """{"entries": {"aa": {"status": "KEY_COMPROMISE"}}}"""

        val entries = JSONObject(mergeWithFallback(remote, fallback)).getJSONObject("entries")

        assertEquals("KEY_COMPROMISE", entries.getJSONObject("aa").getString("status"))
        assertEquals("SUSPENDED", entries.getJSONObject("bb").getString("status"))
    }

    @Test
    fun `a remote revocation of the locally flagged serial replaces it and records its source`() {
        val serial = "8616ef30679ed43cc2b43e3c97a2319e"
        val remote = """{"entries": {"00$serial": {"status": "REVOKED"}}}"""
        val fallback = """{"entries": {"$serial": {"status": "LOCAL"}}}"""

        val entries = JSONObject(mergeWithFallback(remote, fallback)).getJSONObject("entries")

        assertEquals("REVOKED", entries.getJSONObject(serial).getString("status"))
        assertEquals("REMOTE", entries.getJSONObject(serial).getString("_vernierDetectorSource"))
        assertFalse(entries.has("00$serial"))
    }

    @Test
    fun `feeds without entries are rejected`() {
        listOf("[]", "{}", "not json").forEach { json ->
            assertThrows(json, GradleException::class.java) { validatedStatusJson(json) }
        }
    }
}
