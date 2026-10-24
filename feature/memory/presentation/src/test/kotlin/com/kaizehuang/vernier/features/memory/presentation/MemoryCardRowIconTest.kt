package com.kaizehuang.vernier.features.memory.presentation

import com.kaizehuang.vernier.features.memory.domain.MemoryFinding
import com.kaizehuang.vernier.features.memory.domain.MemoryFindingSection
import com.kaizehuang.vernier.features.memory.domain.MemoryFindingSeverity
import com.kaizehuang.vernier.features.memory.domain.MemoryMethod
import com.kaizehuang.vernier.features.memory.domain.MemoryMethodOutcome
import com.kaizehuang.vernier.features.memory.domain.MemoryMethodResult
import com.kaizehuang.vernier.features.memory.domain.MemoryReport
import com.kaizehuang.vernier.features.memory.domain.MemoryStage
import com.kaizehuang.vernier.features.memory.presentation.model.MemoryRowIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class MemoryCardRowIconTest {

    private val mapper = MemoryCardModelMapper()

    @Test
    fun `vDSO and signal findings carry their icons and other findings carry none`() {
        val model = mapper.map(
            MemoryReport.loading().copy(
                stage = MemoryStage.READY,
                findings = listOf(
                    finding(MemoryFindingSection.VDSO, "vDSO base mismatch"),
                    finding(MemoryFindingSection.SIGNAL, "Anonymous signal handler"),
                    finding(MemoryFindingSection.LINKER, "Hidden loader entry"),
                ),
            ),
        )

        assertEquals(
            mapOf(
                "vDSO base mismatch" to MemoryRowIcon.VDSO,
                "Anonymous signal handler" to MemoryRowIcon.SIGNAL_HANDLER,
                "Hidden loader entry" to null,
            ),
            model.loaderRows.associate { it.label to it.icon },
        )
    }

    @Test
    fun `the signal handler method carries the signal icon, loading or ready`() {
        val loading = mapper.map(MemoryReport.loading())
        val ready = mapper.map(
            MemoryReport.loading().copy(
                stage = MemoryStage.READY,
                methods = MemoryMethod.entries.map { method ->
                    MemoryMethodResult(method, "Clean", MemoryMethodOutcome.CLEAN, "detail")
                },
            ),
        )

        listOf(loading, ready).forEach { model ->
            assertEquals(
                listOf(null, null, null, null, MemoryRowIcon.SIGNAL_HANDLER, null),
                model.methodRows.map { it.icon },
            )
            assertEquals(MemoryMethod.entries.map { it.label }, model.methodRows.map { it.label })
        }
    }

    private fun finding(section: MemoryFindingSection, label: String) = MemoryFinding(
        id = label,
        section = section,
        category = section.name,
        label = label,
        detail = "detail",
        severity = MemoryFindingSeverity.MEDIUM,
        detailMonospace = false,
    )
}
