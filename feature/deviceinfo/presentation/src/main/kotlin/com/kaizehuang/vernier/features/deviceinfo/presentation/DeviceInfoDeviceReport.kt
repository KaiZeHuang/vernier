package com.kaizehuang.vernier.features.deviceinfo.presentation

import com.kaizehuang.vernier.core.report.DeviceReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportDeviceIdentity
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoCardModel
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoHeaderFact

fun DeviceInfoCardModel.toDeviceReport(): DeviceReport = DeviceReport(
    identity = ReportDeviceIdentity(
        brand = headerFactValue(DeviceInfoHeaderFact.BRAND),
        model = headerFactValue(DeviceInfoHeaderFact.MODEL),
        androidRelease = headerFactValue(DeviceInfoHeaderFact.ANDROID),
        sdk = headerFactValue(DeviceInfoHeaderFact.SDK),
    ),
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    sections = sections.map { section ->
        val maxLabelLength = section.rows.maxOfOrNull { it.label.length } ?: 16
        val labelWidth = (maxLabelLength + 2).coerceIn(16, 28)
        ReportBlock.Verbatim(
            title = section.title,
            lines = section.rows.map { row -> "    • ${row.label.padEnd(labelWidth)}: ${row.value}" },
        )
    },
)

private fun DeviceInfoCardModel.headerFactValue(fact: DeviceInfoHeaderFact): String? =
    headerFacts.firstOrNull { it.fact == fact }?.value
