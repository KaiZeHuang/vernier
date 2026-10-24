package com.kaizehuang.vernier.features.dangerousapps.presentation

import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.ReportBlock
import com.kaizehuang.vernier.core.report.ReportFact
import com.kaizehuang.vernier.features.dangerousapps.presentation.model.DangerousAppsCardModel

private const val CATALOG_LINE_WIDTH = 95

fun DangerousAppsCardModel.toDetectorReport(): DetectorReport = DetectorReport(
    title = title,
    verdict = verdict,
    severity = status.severity,
    quickFacts = headerFacts.map { ReportFact(it.label, it.value) },
    blocks = buildList {
        hmaAlert?.let { alert ->
            add(
                ReportBlock.Verbatim(
                    title = "HMA Alert: ${alert.title}",
                    lines = buildList {
                        add("    Summary: ${alert.summary}")
                        if (alert.hiddenPackages.isNotEmpty()) {
                            add("    Hidden Packages:")
                            alert.hiddenPackages.forEach { pkg ->
                                add("      • ${pkg.appName} (${pkg.packageName}) [methods: ${pkg.methods.joinToString()}]")
                            }
                        }
                    },
                ),
            )
        }
        if (packageItems.isNotEmpty()) {
            add(
                ReportBlock.Verbatim(
                    title = "Detected Packages",
                    lines = packageItems.map { pkg ->
                        "    • ${pkg.appName} (${pkg.packageName}) [methods: ${pkg.methods.joinToString()}]"
                    },
                ),
            )
        }
        if (context.isNotEmpty()) {
            add(ReportBlock.Verbatim("Context & Inventory", context.map { "    • ${it.label}: ${it.value}" }))
        }
        if (targetApps.isNotEmpty()) {
            val byCategory = targetApps.groupBy { it.category }
            add(
                ReportBlock.Verbatim(
                    title = "Monitored Package Catalog (${targetApps.size} targets across ${byCategory.size} categories)",
                    lines = buildList {
                        byCategory.forEach { (category, apps) ->
                            add("    • $category (${apps.size}):")
                            addAll(wrapCatalogItems("        ", apps.map { "${it.appName} (${it.packageName})" }))
                        }
                    },
                ),
            )
        }
    },
)

/** Packs items onto lines of at most [CATALOG_LINE_WIDTH] columns without ever splitting an item. */
private fun wrapCatalogItems(indent: String, items: List<String>): List<String> {
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder(indent)
    items.forEachIndexed { index, item ->
        val suffix = if (index < items.lastIndex) ", " else ""
        if (currentLine.length + item.length + suffix.length > CATALOG_LINE_WIDTH && currentLine.trim().isNotEmpty()) {
            lines += currentLine.toString()
            currentLine = StringBuilder(indent).append(item).append(suffix)
        } else {
            currentLine.append(item).append(suffix)
        }
    }
    if (currentLine.trim().isNotEmpty()) {
        lines += currentLine.toString()
    }
    return lines
}
