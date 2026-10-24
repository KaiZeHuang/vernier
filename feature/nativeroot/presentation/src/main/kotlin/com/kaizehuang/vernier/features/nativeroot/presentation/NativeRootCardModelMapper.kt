package com.kaizehuang.vernier.features.nativeroot.presentation

import com.kaizehuang.vernier.features.nativeroot.domain.NativeRootReport
import com.kaizehuang.vernier.features.nativeroot.domain.toDetectorStatus
import com.kaizehuang.vernier.features.nativeroot.presentation.model.NativeRootCardModel

class NativeRootCardModelMapper {

    fun map(
        report: NativeRootReport,
    ): NativeRootCardModel {
        return NativeRootCardModel(
            title = "Native Root",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            nativeRows = buildNativeRows(report),
            runtimeRows = buildRuntimeRows(report),
            kernelRows = buildKernelRows(report),
            propertyRows = buildPropertyRows(report),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            scanRows = buildScanRows(report),
        )
    }
}
