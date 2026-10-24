package com.kaizehuang.vernier.features.deviceinfo.presentation

import com.kaizehuang.vernier.core.evidence.DetectorStatus
import com.kaizehuang.vernier.core.evidence.InfoKind
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoKey
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoReport
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoSectionKind
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoStage
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoCardModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFact
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFactModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoRowModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoSectionModel

class DeviceInfoCardModelMapper {

    fun map(report: DeviceInfoReport): DeviceInfoCardModel {
        return DeviceInfoCardModel(
            title = "Device Info",
            subtitle = buildSubtitle(report),
            status = if (report.stage == DeviceInfoStage.FAILED) {
                DetectorStatus.info(InfoKind.ERROR)
            } else {
                DetectorStatus.info(InfoKind.SUPPORT)
            },
            verdict = buildVerdict(report),
            summary = buildSummary(report),
            headerFacts = buildHeaderFacts(report),
            sections = buildSections(report),
        )
    }

    private fun buildSubtitle(report: DeviceInfoReport): String {
        return when (report.stage) {
            DeviceInfoStage.LOADING -> "identity + build + android + runtime + display"
            DeviceInfoStage.FAILED -> "local device profile unavailable"
            DeviceInfoStage.READY -> "${report.totalCount} local device facts"
        }
    }

    private fun buildVerdict(report: DeviceInfoReport): String {
        return when (report.stage) {
            DeviceInfoStage.LOADING -> "Collecting local device profile"
            DeviceInfoStage.FAILED -> "Device profile unavailable"
            DeviceInfoStage.READY -> "Have a good day"
        }
    }

    private fun buildSummary(report: DeviceInfoReport): String {
        return when (report.stage) {
            DeviceInfoStage.LOADING -> "This card is purely contextual and does not affect detector severity or ranking."
            DeviceInfoStage.FAILED -> report.errorMessage ?: "Device info collection failed."
            DeviceInfoStage.READY -> "This card is informational only. It gives you a fixed local profile snapshot to read alongside the detector cards above."
        }
    }

    private fun buildHeaderFacts(report: DeviceInfoReport): List<DeviceInfoHeaderFactModel> {
        fun valueOf(key: DeviceInfoKey): String {
            return report.sections.asSequence()
                .flatMap { it.entries }
                .firstOrNull { it.key == key }
                ?.value
                ?: when (report.stage) {
                    DeviceInfoStage.LOADING -> "Pending"
                    DeviceInfoStage.FAILED -> "Error"
                    DeviceInfoStage.READY -> "Unavailable"
                }
        }

        return listOf(
            DeviceInfoHeaderFactModel(DeviceInfoHeaderFact.BRAND, valueOf(DeviceInfoKey.BRAND)),
            DeviceInfoHeaderFactModel(DeviceInfoHeaderFact.MODEL, valueOf(DeviceInfoKey.MODEL)),
            DeviceInfoHeaderFactModel(DeviceInfoHeaderFact.ANDROID, valueOf(DeviceInfoKey.ANDROID_RELEASE)),
            DeviceInfoHeaderFactModel(DeviceInfoHeaderFact.SDK, valueOf(DeviceInfoKey.SDK)),
        )
    }

    private fun buildSections(report: DeviceInfoReport): List<DeviceInfoSectionModel> {
        return when (report.stage) {
            DeviceInfoStage.LOADING -> DeviceInfoSectionKind.entries.map(::placeholderSection)

            DeviceInfoStage.FAILED -> listOf(
                DeviceInfoSectionModel(
                    title = "Unavailable",
                    rows = listOf(
                        DeviceInfoRowModel(
                            label = "Reason",
                            value = report.errorMessage ?: "Unknown error",
                        ),
                    ),
                ),
            )

            DeviceInfoStage.READY -> report.sections.map { section ->
                DeviceInfoSectionModel(
                    title = section.title,
                    kind = section.kind,
                    rows = section.entries.map { entry ->
                        DeviceInfoRowModel(
                            label = entry.label,
                            value = entry.value,
                            detailMonospace = entry.detailMonospace,
                        )
                    },
                )
            }
        }
    }

    private fun placeholderSection(kind: DeviceInfoSectionKind): DeviceInfoSectionModel {
        return DeviceInfoSectionModel(
            title = kind.title,
            kind = kind,
            rows = listOf(
                DeviceInfoRowModel("Loading", "Pending"),
            ),
        )
    }
}
