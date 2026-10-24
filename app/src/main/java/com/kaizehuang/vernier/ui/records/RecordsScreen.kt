package com.kaizehuang.vernier.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizehuang.vernier.R
import com.kaizehuang.vernier.core.designsystem.components.SurfacePanel
import com.kaizehuang.vernier.core.designsystem.theme.VernierStatus
import com.kaizehuang.vernier.core.designsystem.theme.VernierTheme
import com.kaizehuang.vernier.core.designsystem.theme.VernierTypography
import com.kaizehuang.vernier.core.ui.components.WrapSafeText
import com.kaizehuang.vernier.features.dashboard.presentation.model.OverviewVerdict
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The records page: the instrument log of past measurement passes.
 *
 * Each row is a time, a verdict and the counts — the same reading rules as the measure page,
 * applied across time instead of across groups.
 */
@Composable
internal fun RecordsScreen(
    store: ScanRecordStore,
    modifier: Modifier = Modifier,
) {
    val palette = VernierTheme.palette
    val records by produceState<List<ScanRecord>?>(initialValue = null, key1 = store) {
        store.records.collect { value = it }
    }
    val entries = records
    if (entries.isNullOrEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WrapSafeText(
                text = stringResource(R.string.records_title),
                style = VernierTypography.pageTitle,
                color = palette.ink,
            )
            SurfacePanel(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                WrapSafeText(
                    text = stringResource(R.string.records_empty),
                    style = VernierTypography.description,
                    color = palette.ink2,
                )
            }
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            WrapSafeText(
                text = stringResource(R.string.records_title),
                style = VernierTypography.pageTitle,
                color = palette.ink,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        items(items = entries, key = { it.atEpochMillis }) { entry ->
            RecordRow(entry = entry)
        }
    }
}

@Composable
private fun RecordRow(entry: ScanRecord) {
    val palette = VernierTheme.palette
    val status = entry.verdict.toVernierStatus()
    val lamp = palette.statusColor(status)
    SurfacePanel(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = lamp)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                WrapSafeText(
                    text = formatRecordTime(entry.atEpochMillis),
                    style = VernierTypography.mono,
                    color = palette.ink,
                )
                WrapSafeText(
                    text = "${entry.verdict.name} · ${entry.ready}/${entry.total}",
                    style = VernierTypography.count,
                    color = palette.ink3,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                WrapSafeText(
                    text = verdictWord(status),
                    style = VernierTypography.statusWord,
                    color = lamp,
                )
                WrapSafeText(
                    text = stringResource(R.string.records_counts, entry.danger, entry.warning),
                    style = VernierTypography.count,
                    color = palette.ink3,
                )
            }
        }
    }
}

@Composable
private fun verdictWord(status: VernierStatus): String = when (status) {
    VernierStatus.CLEAR -> stringResource(com.kaizehuang.vernier.features.dashboard.ui.R.string.verdict_all_clear)
    VernierStatus.WARNING -> stringResource(com.kaizehuang.vernier.features.dashboard.ui.R.string.verdict_warning)
    VernierStatus.DANGER -> stringResource(com.kaizehuang.vernier.features.dashboard.ui.R.string.verdict_danger)
    VernierStatus.NOT_CHECKED -> stringResource(com.kaizehuang.vernier.features.dashboard.ui.R.string.verdict_not_checked)
}

private fun OverviewVerdict.toVernierStatus(): VernierStatus = when (this) {
    OverviewVerdict.DANGER -> VernierStatus.DANGER
    OverviewVerdict.WARNING,
    OverviewVerdict.INFO -> VernierStatus.WARNING

    OverviewVerdict.OK -> VernierStatus.CLEAR
    OverviewVerdict.READY,
    OverviewVerdict.PENDING -> VernierStatus.NOT_CHECKED
}

private fun formatRecordTime(epochMillis: Long): String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(epochMillis))
