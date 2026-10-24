package com.kaizehuang.vernier.features.mount.data.repository

import com.kaizehuang.vernier.features.mount.domain.MountFinding
import com.kaizehuang.vernier.features.mount.domain.MountFindingGroup
import com.kaizehuang.vernier.features.mount.domain.MountFindingOrigin
import com.kaizehuang.vernier.features.mount.domain.MountFindingSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class MountPreloadFindingsTest {

    @Test
    fun `a startup mount id gap alone keeps its startup origin`() {
        val merged = mergePreloadFindings(emptyList(), listOf(startupMountIdGap))

        assertEquals(listOf(MountFindingOrigin.STARTUP_PRELOAD), merged.map { it.origin })
    }

    @Test
    fun `a startup mount id gap merged with a runtime one is seen by both captures`() {
        val merged = mergePreloadFindings(listOf(runtimeMountIdGap), listOf(startupMountIdGap))

        assertEquals(listOf(MountFindingOrigin.STARTUP_AND_RUNTIME), merged.map { it.origin })
    }

    @Test
    fun `runtime mount id gaps collapsed without a startup capture stay runtime`() {
        val merged = mergePreloadFindings(listOf(runtimeMountIdGap, runtimeMountIdGap.copy(value = "2")), emptyList())

        assertEquals(listOf(MountFindingOrigin.RUNTIME), merged.map { it.origin })
    }

    private val startupMountIdGap = MountFinding(
        id = "early_preload_mount_id_loophole",
        label = "Mount ID loophole",
        value = "Startup preload",
        group = MountFindingGroup.CONSISTENCY,
        severity = MountFindingSeverity.DANGER,
        detail = "Source=startup preload",
        origin = MountFindingOrigin.STARTUP_PRELOAD,
    )

    private val runtimeMountIdGap = MountFinding(
        id = "mount_id_loophole",
        label = "Mount ID loophole",
        value = "1",
        group = MountFindingGroup.CONSISTENCY,
        severity = MountFindingSeverity.DANGER,
        detail = "Missing mount ID 42",
    )
}
