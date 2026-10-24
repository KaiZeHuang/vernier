package com.kaizehuang.vernier.features.systemproperties.presentation

import com.kaizehuang.vernier.features.systemproperties.domain.SystemPropertiesReport
import com.kaizehuang.vernier.features.systemproperties.domain.toDetectorStatus
import com.kaizehuang.vernier.features.systemproperties.presentation.model.SystemPropertiesCardModel

class SystemPropertiesCardModelMapper {
    fun map(
        report: SystemPropertiesReport,
    ): SystemPropertiesCardModel {
        return SystemPropertiesCardModel(
            title = "System Properties",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            coreRows = buildCoreRows(report),
            bootRows = buildBootRows(report),
            buildRows = buildBuildRows(report),
            sourceRows = buildSourceRows(report),
            consistencyRows = buildConsistencyRows(report),
            infoRows = buildInfoRows(report),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            scanRows = buildScanRows(report),
        )
    }
}
