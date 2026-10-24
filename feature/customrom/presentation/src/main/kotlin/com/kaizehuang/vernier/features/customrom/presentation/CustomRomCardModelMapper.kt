package com.kaizehuang.vernier.features.customrom.presentation

import com.kaizehuang.vernier.features.customrom.domain.CustomRomReport
import com.kaizehuang.vernier.features.customrom.domain.toDetectorStatus
import com.kaizehuang.vernier.features.customrom.presentation.model.CustomRomCardModel

class CustomRomCardModelMapper {
    fun map(
        report: CustomRomReport,
    ): CustomRomCardModel {
        return CustomRomCardModel(
            title = "Custom ROM",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            buildRows = buildBuildRows(report),
            runtimeRows = buildRuntimeRows(report),
            frameworkRows = buildFrameworkRows(report),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            scanRows = buildScanRows(report),
        )
    }
}
