package com.kaizehuang.vernier.features.bootloader.data.widevine

import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFinding
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingGroup
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderFindingSeverity
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderImpact
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderMethodOutcome
import com.kaizehuang.vernier.features.bootloader.domain.BootloaderMethodResult

internal data class WidevineBootloaderEvidence(
    val findings: List<BootloaderFinding>,
    val impacts: List<BootloaderImpact>,
    val method: BootloaderMethodResult,
    val anomalyCount: Int,
)

internal class WidevineCredentialRepository(
    private val source: WidevineCredentialSource = WidevineCredentialProbe(),
    private val classifier: WidevineCredentialClassifier = WidevineCredentialClassifier(),
) {

    fun inspect(bootContext: WidevineBootContext): WidevineBootloaderEvidence {
        val snapshot = try {
            source.collect()
        } catch (_: Exception) {
            WidevineCredentialSnapshot()
        }
        val assessment = classifier.classify(snapshot, bootContext)
        return WidevineBootloaderEvidence(
            findings = assessment.findings.map { finding ->
                BootloaderFinding(
                    id = finding.id,
                    label = finding.label,
                    value = finding.value,
                    group = BootloaderFindingGroup.CONSISTENCY,
                    severity = finding.severity.toFindingSeverity(),
                    detail = finding.detail,
                    corroborating = finding.corroborating,
                )
            },
            impacts = assessment.impact?.let { impact ->
                listOf(
                    BootloaderImpact(
                        text = impact.detail,
                        severity = impact.severity.toFindingSeverity(),
                    ),
                )
            }.orEmpty(),
            method = BootloaderMethodResult(
                label = "Widevine credential",
                summary = assessment.methodSummary,
                outcome = assessment.methodSeverity.toMethodOutcome(),
                detail = assessment.methodDetail,
            ),
            anomalyCount = assessment.anomalyCount,
        )
    }

    private fun WidevineAssessmentSeverity.toFindingSeverity(): BootloaderFindingSeverity {
        return when (this) {
            WidevineAssessmentSeverity.SAFE -> BootloaderFindingSeverity.SAFE
            WidevineAssessmentSeverity.WARNING -> BootloaderFindingSeverity.WARNING
            WidevineAssessmentSeverity.SUPPORT -> BootloaderFindingSeverity.INFO
        }
    }

    private fun WidevineAssessmentSeverity.toMethodOutcome(): BootloaderMethodOutcome {
        return when (this) {
            WidevineAssessmentSeverity.SAFE -> BootloaderMethodOutcome.CLEAN
            WidevineAssessmentSeverity.WARNING -> BootloaderMethodOutcome.WARNING
            WidevineAssessmentSeverity.SUPPORT -> BootloaderMethodOutcome.SUPPORT
        }
    }
}
