package com.kaizehuang.vernier.features.selinux.presentation

import com.kaizehuang.vernier.features.selinux.domain.SelinuxReport
import com.kaizehuang.vernier.features.selinux.domain.toDetectorStatus
import com.kaizehuang.vernier.features.selinux.presentation.model.SelinuxCardModel

class SelinuxCardModelMapper {

    fun map(report: SelinuxReport): SelinuxCardModel {
        return SelinuxCardModel(
            title = "SELinux",
            subtitle = buildSubtitle(report),
            status = report.toDetectorStatus(),
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            stateRows = buildStateRows(report),
            impactItems = buildImpactItems(report),
            methodRows = buildMethodRows(report),
            policyRows = buildPolicyRows(report.policyAnalysis),
            policyNotes = buildPolicyNotes(report.policyAnalysis),
            auditRows = buildAuditRows(report.auditIntegrity),
            auditNotes = buildAuditNotes(report.auditIntegrity),
            deviceRows = buildDeviceRows(report),
            references = buildReferences(),
        )
    }
}
