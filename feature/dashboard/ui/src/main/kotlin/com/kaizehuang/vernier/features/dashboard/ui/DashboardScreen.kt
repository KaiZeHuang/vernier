package com.kaizehuang.vernier.features.dashboard.ui

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.core.designsystem.components.StatusBarProtection
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTheme
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.theme.SurfaceTypography
import com.kaizehuang.vernier.core.designsystem.theme.VernierStatus
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.ui.components.LocalDetectorCardExpansionSignal
import com.kaizehuang.vernier.core.ui.detector.DetectorSession
import com.kaizehuang.vernier.core.ui.detector.DeviceProfileSession
import com.kaizehuang.vernier.core.ui.LocalAppBuildInfo
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.core.ui.presentation.formatBuildTimeUtc
import com.kaizehuang.vernier.features.dashboard.presentation.export.DashboardExport
import com.kaizehuang.vernier.features.dashboard.presentation.export.DashboardReportRenderer
import com.kaizehuang.vernier.features.dashboard.presentation.export.ExportHeader
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardFindingModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardOverviewModel
import com.kaizehuang.vernier.features.dashboard.presentation.model.DashboardUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveCircularProgressIndicator
import io.github.xiaotong6666.uihelper.adaptive.AdaptiveContent
import io.github.xiaotong6666.uihelper.adaptive.adaptiveContainerContentColor
import io.github.xiaotong6666.uihelper.adaptive.adaptiveScrollableOverscrollEffect
import io.github.xiaotong6666.uihelper.adaptive.adaptiveSecondaryTextColor
import io.github.xiaotong6666.uihelper.adaptive.adaptiveValue
import io.github.xiaotong6666.uihelper.adaptive.adaptiveVerticalScrollFeedback
import io.github.xiaotong6666.uihelper.adaptive.adaptiveViewportOverscroll
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    detectors: List<DetectorSession>,
    deviceProfile: DeviceProfileSession,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
    pageModifier: Modifier = Modifier,
) {
    val materialOverscrollEffect = rememberOverscrollEffect()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var expansionRequest by remember { mutableStateOf<DashboardExpansionRequest?>(null) }
    val hostedHorizontalInset = adaptiveValue(material = 16.dp, miuix = 12.dp)
    val pageSpacing = adaptiveValue(material = 16.dp, miuix = 12.dp)
    val showStandaloneMaterialChrome = adaptiveValue(
        material = scaffoldPadding == null,
        miuix = false,
    )
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val resources = LocalResources.current
    val buildInfo = LocalAppBuildInfo.current
    val orderedDetectors = remember(uiState.cardOrder, detectors) {
        val detectorsById = detectors.associateBy { it.id }
        uiState.cardOrder.mapNotNull { detectorsById[it] }
    }
    // The group rows need each check's own status: a lamp is the worst status of its group,
    // never an average of the group.
    val summaries = orderedDetectors.associate { detector ->
        detector.id to detector.summary.collectAsState().value
    }
    val sections = remember(orderedDetectors, summaries) {
        vernierGroupOrder.mapNotNull { group ->
            val members = orderedDetectors.filter { it.id.measurementGroup() == group }
            if (members.isEmpty()) return@mapNotNull null
            val checked = members.count { summaries[it.id]?.ready == true }
            val status = worstStatus(
                members.map { summaries[it.id]?.status?.toVernierStatus() ?: VernierStatus.NOT_CHECKED },
            )
            VernierSection(group = group, status = status, checked = checked, members = members)
        }
    }
    val headerCount = (if (showStandaloneMaterialChrome) 1 else 0) + 1
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        if (uri != null) {
            try {
                val text = DashboardReportRenderer.render(
                    DashboardExport(
                        header = ExportHeader(
                            versionName = buildInfo.versionName,
                            versionCode = buildInfo.versionCode,
                            buildHash = buildInfo.buildHash,
                            buildTime = formatBuildTimeUtc(buildInfo.buildTimeUtc),
                            reportTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z)", Locale.US).format(Date()),
                        ),
                        overview = uiState.overview,
                        topFindings = uiState.topFindings,
                        detectors = orderedDetectors.map { it.report() },
                        device = deviceProfile.report(),
                    ),
                )
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(text.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(
                    context,
                    resources.getString(R.string.dashboard_report_saved),
                    Toast.LENGTH_SHORT,
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message
                        ?.takeIf(String::isNotBlank)
                        ?.let { message ->
                            resources.getString(R.string.dashboard_report_save_failed, message)
                        }
                        ?: resources.getString(R.string.dashboard_report_save_failed_unknown),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceTheme.palette.groupedBackground)
            // Render at the entire page viewport, not at an inset card or scroll item.
            // The LazyColumn below supplies the scroll deltas to the same native effect.
            .adaptiveViewportOverscroll(materialOverscrollEffect),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                // uihelper preserves the native MIUIX ordering: elastic overscroll remains outside
                // the top-bar nested-scroll observer so its rebound cannot collapse chrome.
                .adaptiveVerticalScrollFeedback()
                .then(pageModifier),
            overscrollEffect = adaptiveScrollableOverscrollEffect(materialOverscrollEffect),
            verticalArrangement = Arrangement.spacedBy(pageSpacing),
            contentPadding = if (scaffoldPadding != null) {
                // MIUIX owns the top bar and navigation bar. Their insets must not be re-applied.
                PaddingValues(
                    start = scaffoldPadding.calculateStartPadding(layoutDirection) + hostedHorizontalInset,
                    top = scaffoldPadding.calculateTopPadding() + 12.dp,
                    end = scaffoldPadding.calculateEndPadding(layoutDirection) + hostedHorizontalInset,
                    bottom = scaffoldPadding.calculateBottomPadding() + 16.dp,
                )
            } else {
                WindowInsets.safeDrawing
                    .add(WindowInsets(left = 16.dp, top = 12.dp, right = 16.dp, bottom = 96.dp))
                    .asPaddingValues()
            },
        ) {
            // Hosted pages put brand actions in the real app bar. Keep the original
            // inline brand header only for standalone/dashboard preview hosts.
            if (showStandaloneMaterialChrome) {
                item { BrandHeader() }
            }
            item {
                DashboardSummarySection(
                    overview = uiState.overview,
                    findings = uiState.topFindings,
                    showLoadingOverlay = uiState.isLoading,
                    onExportReport = { exportLauncher.launch(generateExportReportFileName()) },
                    onFindingClick = { finding ->
                        val detectorId = finding.detectorId ?: return@DashboardSummarySection
                        val index = indexOfDetector(
                            sections = sections,
                            detectorId = detectorId,
                            headerCount = headerCount,
                        )
                        if (index >= 0) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                expansionRequest = DashboardExpansionRequest(
                                    detectorId = detectorId,
                                    signal = (expansionRequest?.signal ?: 0L) + 1L,
                                )
                            }
                        }
                    },
                )
            }
            // The measure page reads as five groups, not as one list of eighteen checks.
            sections.forEach { section ->
                item(key = "group-${section.group.name}") {
                    VernierGroupRow(
                        group = section.group,
                        status = section.status,
                        checkedCount = section.checked,
                        totalCount = section.members.size,
                    )
                }
                items(
                    items = section.members,
                    key = { detector -> detector.id.value },
                ) { detector ->
                    val expansionSignal = expansionRequest
                        ?.takeIf { request -> request.detectorId == detector.id }
                        ?.signal
                        ?: 0L
                    CompositionLocalProvider(
                        LocalDetectorCardExpansionSignal provides expansionSignal,
                    ) {
                        detector.Card()
                    }
                }
            }
            item {
                deviceProfile.Card()
            }
        }

        if (showStandaloneMaterialChrome) {
            StatusBarProtection(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

private data class DashboardExpansionRequest(
    val detectorId: DetectorId,
    val signal: Long,
)

/** One measurement group and the checks it holds, in page order. */
private data class VernierSection(
    val group: VernierGroup,
    val status: VernierStatus,
    val checked: Int,
    val members: List<DetectorSession>,
)

/**
 * LazyColumn index of a check: the page opens with the brand/summary headers, then every group
 * contributes one header row plus its own cards.
 */
private fun indexOfDetector(
    sections: List<VernierSection>,
    detectorId: DetectorId,
    headerCount: Int,
): Int {
    var index = headerCount
    for (section in sections) {
        index += 1
        val position = section.members.indexOfFirst { it.id == detectorId }
        if (position >= 0) return index + position
        index += section.members.size
    }
    return -1
}

@Composable
private fun DashboardSummarySection(
    overview: DashboardOverviewModel,
    findings: List<DashboardFindingModel>,
    showLoadingOverlay: Boolean,
    onExportReport: () -> Unit,
    onFindingClick: (DashboardFindingModel) -> Unit,
) {
    if (showLoadingOverlay) {
        // Do not draw the finished Danger card behind an opaque loading overlay. Its independent
        // Material radius leaked through the MIUIX squircle corners, and made the placeholder
        // inherit the combined height of cards that are not ready yet.
        DashboardLoadingOverlay()
    } else {
        // The status hero and finding queue already have their own internal spacing.
        // An additional 24dp here produced the empty band between the two cards.
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Vernier readout: the status word is the subject, the scale is the progress,
            // the counter is the count. The instrument-styled hero leads the measure page.
            VernierVerdictHero(model = overview)
            DashboardOverviewCard(model = overview, onExportReport = onExportReport)
            DashboardFindingsCard(
                findings = findings,
                onFindingClick = onFindingClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DashboardLoadingOverlay(
    modifier: Modifier = Modifier,
) {
    SurfacePanel(
        modifier = modifier,
        contentPadding = PaddingValues(start = 28.dp, end = 28.dp, top = 44.dp, bottom = 44.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AdaptiveContent(
                miuix = {
                    // Native MIUIX indeterminate progress: grey track and moving accent arc.
                    AdaptiveCircularProgressIndicator(size = 48.dp)
                },
                material = { LoadingIndicator(modifier = Modifier.size(56.dp)) },
            )
            WrapSafeText(
                text = stringResource(R.string.dashboard_loading_title),
                modifier = Modifier.padding(top = 6.dp),
                style = SurfaceTypography.LoadingTitle,
                color = adaptiveContainerContentColor(),
                textAlign = TextAlign.Center,
            )
            WrapSafeText(
                text = stringResource(R.string.dashboard_loading_summary),
                style = SurfaceTypography.LoadingSupporting,
                color = adaptiveSecondaryTextColor(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

internal fun generateExportReportFileName(
    model: String = Build.MODEL,
    nowEpochMillis: Long = System.currentTimeMillis(),
): String {
    val sanitizedModel = model.trim().ifBlank { "unknown" }
        .replace(Regex("[^a-zA-Z0-9._-]"), "_")
    // Filenames are machine-readable and must not vary with the device language/locale.
    val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val timestamp = timestampFormat.format(Date(nowEpochMillis))
    return "vernier_detector_report_${sanitizedModel}_$timestamp.txt"
}
