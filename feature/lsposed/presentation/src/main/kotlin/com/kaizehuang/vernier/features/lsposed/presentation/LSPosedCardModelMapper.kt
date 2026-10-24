package com.kaizehuang.vernier.features.lsposed.presentation

import com.kaizehuang.vernier.features.lsposed.domain.LSPosedReport
import com.kaizehuang.vernier.features.lsposed.domain.LSPosedSignalGroup
import com.kaizehuang.vernier.features.lsposed.domain.toDetectorStatus
import com.kaizehuang.vernier.features.lsposed.presentation.model.LSPosedCardModel

class LSPosedCardModelMapper {

    fun map(report: LSPosedReport): LSPosedCardModel {
        return LSPosedCardModel(
            title = "LSPosed",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            runtimeRows = buildRowsForGroup(report, LSPosedSignalGroup.RUNTIME, "Runtime probes"),
            binderRows = buildRowsForGroup(report, LSPosedSignalGroup.BINDER, "Binder probes"),
            packageRows = buildRowsForGroup(report, LSPosedSignalGroup.PACKAGES, "Packages"),
            policyRows = buildRowsForGroup(report, LSPosedSignalGroup.POLICY, "SELinux policy"),
            nativeRows = buildRowsForGroup(report, LSPosedSignalGroup.NATIVE, "Native traces"),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            scanRows = buildScanRows(report),
        )
    }
}
