package com.kaizehuang.vernier.features.customrom.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.customrom.domain.CustomRomPackageVisibility
import com.kaizehuang.vernier.features.customrom.domain.CustomRomReport
import com.kaizehuang.vernier.features.customrom.domain.CustomRomStage
import com.kaizehuang.vernier.features.customrom.domain.hasReducedCoverage
import com.kaizehuang.vernier.features.customrom.presentation.model.CustomRomImpactItemModel

internal fun buildImpactItems(report: CustomRomReport): List<CustomRomImpactItemModel> {
    return when (report.stage) {
        CustomRomStage.LOADING -> listOf(
            CustomRomImpactItemModel(
                text = "Gathering local firmware branding and framework evidence.",
                status = DetectorStatus.info(InfoKind.SUPPORT),
            ),
        )

        CustomRomStage.FAILED -> listOf(
            CustomRomImpactItemModel(
                text = report.errorMessage ?: "Custom ROM scan failed.",
                status = DetectorStatus.info(InfoKind.ERROR),
            ),
        )

        CustomRomStage.READY -> when {
            report.hasIndicators -> buildList {
                add(
                    CustomRomImpactItemModel(
                        text = "Aftermarket firmware can legitimately alter build properties, property storage, bootloader state, privileged services, and security defaults.",
                        status = DetectorStatus.warning(),
                    ),
                )
                add(
                    CustomRomImpactItemModel(
                        text = "Attestation behavior, Play Integrity, and some banking or DRM apps may differ on custom ROMs or modified boot chains.",
                        status = DetectorStatus.warning(),
                    ),
                )
                add(
                    CustomRomImpactItemModel(
                        text = "This signal alone does not prove malicious compromise or active root access.",
                        status = DetectorStatus.info(InfoKind.SUPPORT),
                    ),
                )
            }

            else -> buildList {
                if (!report.nativeAvailable) {
                    add(
                        CustomRomImpactItemModel(
                            text = "Native coverage was unavailable on this build, so clean native trace results are not available.",
                            status = DetectorStatus.info(InfoKind.SUPPORT),
                        ),
                    )
                } else if (report.hasReducedCoverage()) {
                    add(
                        CustomRomImpactItemModel(
                            text = "No custom ROM signature surfaced from available probes, but package visibility, service, property-area, or native symbol coverage was incomplete.",
                            status = DetectorStatus.info(InfoKind.SUPPORT),
                        ),
                    )
                } else {
                    add(
                        CustomRomImpactItemModel(
                            text = "No common aftermarket firmware branding, property-area, or framework traces were found.",
                            status = DetectorStatus.allClear(),
                        ),
                    )
                }
                if (report.packageVisibility != CustomRomPackageVisibility.FULL) {
                    add(
                        CustomRomImpactItemModel(
                            text = if (report.packageVisibility == CustomRomPackageVisibility.RESTRICTED) {
                                "Package visibility was scoped, so clean app-level evidence may be incomplete."
                            } else {
                                "Package inventory was unavailable or anomalous, so app-level absence is inconclusive."
                            },
                            status = DetectorStatus.info(InfoKind.SUPPORT),
                        ),
                    )
                }
                add(
                    CustomRomImpactItemModel(
                        text = "A determined ROM can remove obvious signatures, so absence is not proof of stock firmware.",
                        status = DetectorStatus.info(InfoKind.SUPPORT),
                    ),
                )
            }
        }
    }
}
