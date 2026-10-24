package com.kaizehuang.vernier.capability.helperprocess.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class HelperProbePayloadBuilderTest(
    private val profile: HelperProcessProfile,
) {
    @Test
    fun `helper skips renderer and preserves diagnostic evidence`() {
        val environment = FakeEnvironment(profile)
        val nativeSnapshot = VirtualizationNativeSnapshot(
            available = true,
            mountNamespaceInode = "mnt:[4026531840]",
            apexMountKey = "21|8:1|/|/apex|ext4|/dev/block/dm-1",
            systemMountKey = "22|8:1|/|/system|ext4|/dev/block/dm-2",
            vendorMountKey = "23|8:1|/|/vendor|ext4|/dev/block/dm-3",
            findings = listOf(
                VirtualizationNativeFinding(
                    group = "TRANSLATION",
                    severity = "WARNING",
                    label = "Mapped translation library",
                    value = "libndk_translation.so",
                    detail = "/system/lib64/libndk_translation.so",
                ),
            ),
        )
        val bridge = RecordingNativeBridge(nativeSnapshot)

        val payload = HelperProbePayloadBuilder.buildSnapshotPayload(
            environment = environment,
            profile = profile,
            nativeBridge = bridge,
        )
        val snapshot = HelperProcessSnapshot.parse(payload)

        // Assert outside the builder's runCatching so a swallowed failure cannot pass this test.
        assertEquals(listOf(false), bridge.rendererRequests)
        assertTrue(snapshot.available)
        assertTrue(snapshot.nativeAvailable)
        assertEquals(profile, snapshot.profile)
        assertEquals(environment.packageName, snapshot.packageName)
        assertEquals(environment.observeUidIdentity().uid, snapshot.uid)
        assertEquals(environment.observeUidIdentity().packagesForUid, snapshot.packagesForUid)
        assertEquals(environment.observeDexPath().classPathEntries, snapshot.classPathEntries)
        assertEquals(environment.codePath, snapshot.codePath)
        assertEquals(nativeSnapshot.mountNamespaceInode, snapshot.mountNamespaceInode)
        assertEquals(nativeSnapshot.apexMountKey, snapshot.apexMountKey)
        assertEquals(nativeSnapshot.systemMountKey, snapshot.systemMountKey)
        assertEquals(nativeSnapshot.vendorMountKey, snapshot.vendorMountKey)
        assertEquals(nativeSnapshot.findings, snapshot.findings)
        assertFalse(payload.lineSequence().any { it.startsWith("EGL_") })
        if (profile == HelperProcessProfile.ISOLATED) {
            assertEquals(0, environment.storageReads)
            assertEquals("", snapshot.filesDir)
            assertEquals("", snapshot.cacheDir)
        } else {
            assertEquals(2, environment.storageReads)
            assertEquals("/data/user/0/vernier/files", snapshot.filesDir)
            assertEquals("/data/user/0/vernier/cache", snapshot.cacheDir)
        }
    }

    @Test
    fun `native unavailable still preserves managed helper evidence without renderer retry`() {
        val bridge = RecordingNativeBridge(VirtualizationNativeSnapshot(available = false))

        val snapshot = HelperProcessSnapshot.parse(
            HelperProbePayloadBuilder.buildSnapshotPayload(
                environment = FakeEnvironment(profile),
                profile = profile,
                nativeBridge = bridge,
            ),
        )

        assertEquals(listOf(false), bridge.rendererRequests)
        assertTrue(snapshot.available)
        assertFalse(snapshot.nativeAvailable)
        assertEquals(profile, snapshot.profile)
        assertEquals("com.kaizehuang.vernier", snapshot.packageName)
        assertEquals(listOf("/data/app/vernier/base.apk"), snapshot.classPathEntries)
    }

    @Test
    fun `platform read failure stays inside payload error boundary`() {
        val bridge = RecordingNativeBridge(VirtualizationNativeSnapshot(available = true))
        val environment = object : FakeEnvironment(profile) {
            override val codePath: String get() = error("code path unavailable")
        }

        val snapshot = HelperProcessSnapshot.parse(
            HelperProbePayloadBuilder.buildSnapshotPayload(
                environment = environment,
                profile = profile,
                nativeBridge = bridge,
            ),
        )

        assertEquals(listOf(false), bridge.rendererRequests)
        assertFalse(snapshot.available)
        assertFalse(snapshot.nativeAvailable)
        assertEquals(profile, snapshot.profile)
        assertEquals("code path unavailable", snapshot.errorDetail)
    }

    private class RecordingNativeBridge(
        private val snapshot: VirtualizationNativeSnapshot,
    ) : VirtualizationNativeBridge() {
        val rendererRequests = mutableListOf<Boolean>()

        override fun collectSnapshot(probeRenderer: Boolean): VirtualizationNativeSnapshot {
            rendererRequests += probeRenderer
            return snapshot
        }
    }

    private open class FakeEnvironment(
        private val profile: HelperProcessProfile,
    ) : HelperProbePayloadBuilder.SnapshotEnvironment {
        var storageReads = 0
            private set

        override val packageName = "com.kaizehuang.vernier"
        override val codePath: String get() = "/data/app/vernier/base.apk"
        override val filesDir: String get() = readStorage("files")
        override val cacheDir: String get() = readStorage("cache")

        override fun observeDexPath() = DexPathObservation(
            classPathEntries = listOf("/data/app/vernier/base.apk"),
            sourceDir = "/data/app/vernier/base.apk",
        )

        override fun observeUidIdentity() = UidIdentityObservation(
            uid = if (profile == HelperProcessProfile.ISOLATED) 99001 else 10123,
            packageName = packageName,
            packagesForUid = if (profile == HelperProcessProfile.ISOLATED) {
                emptyList()
            } else {
                listOf(packageName)
            },
        )

        private fun readStorage(directory: String): String {
            storageReads += 1
            check(profile != HelperProcessProfile.ISOLATED) {
                "Isolated helper must not access app-private storage"
            }
            return "/data/user/0/vernier/$directory"
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun profiles(): List<Array<HelperProcessProfile>> =
            HelperProcessProfile.entries.map { arrayOf(it) }
    }
}
