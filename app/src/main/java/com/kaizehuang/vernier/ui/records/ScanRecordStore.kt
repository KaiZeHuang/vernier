package com.kaizehuang.vernier.ui.records

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kaizehuang.vernier.features.dashboard.presentation.model.OverviewVerdict
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** One finished measurement pass: when it ended, what it concluded, and the counts behind it. */
data class ScanRecord(
    val atEpochMillis: Long,
    val verdict: OverviewVerdict,
    val ready: Int,
    val total: Int,
    val danger: Int,
    val warning: Int,
)

/**
 * The instrument log: the last passes of the inspector, newest first.
 *
 * A flat pipe-encoded string keeps this dependency-free — the records are a handful of numbers
 * and a word, not a document.
 */
class ScanRecordStore private constructor(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("scan_records") },
    )

    val records: Flow<List<ScanRecord>> = dataStore.data.map { prefs ->
        val raw = prefs[KEY_RECORDS].orEmpty()
        if (raw.isBlank()) return@map emptyList()
        raw.split(RECORD_SEPARATOR).mapNotNull { decode(it) }.sortedByDescending { it.atEpochMillis }
    }

    /** Append one pass; the same instant is never recorded twice. */
    suspend fun record(entry: ScanRecord) {
        dataStore.edit { prefs ->
            val existing = prefs[KEY_RECORDS].orEmpty()
                .split(RECORD_SEPARATOR)
                .mapNotNull { decode(it) }
                .filter { it.atEpochMillis != entry.atEpochMillis }
            val next = (listOf(entry) + existing)
                .sortedByDescending { it.atEpochMillis }
                .take(MAX_RECORDS)
            prefs[KEY_RECORDS] = next.joinToString(RECORD_SEPARATOR) { encode(it) }
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(KEY_RECORDS) }
    }

    private fun encode(entry: ScanRecord): String = listOf(
        entry.atEpochMillis.toString(),
        entry.verdict.name,
        entry.ready.toString(),
        entry.total.toString(),
        entry.danger.toString(),
        entry.warning.toString(),
    ).joinToString(FIELD_SEPARATOR)

    private fun decode(raw: String): ScanRecord? {
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size != 6) return null
        val at = parts[0].toLongOrNull() ?: return null
        val ready = parts[2].toIntOrNull() ?: return null
        val total = parts[3].toIntOrNull() ?: return null
        val danger = parts[4].toIntOrNull() ?: return null
        val warning = parts[5].toIntOrNull() ?: return null
        val verdict = OverviewVerdict.entries.firstOrNull { it.name == parts[1] }
            ?: return null
        return ScanRecord(
            atEpochMillis = at,
            verdict = verdict,
            ready = ready,
            total = total,
            danger = danger,
            warning = warning,
        )
    }

    companion object {
        private const val MAX_RECORDS = 30
        private const val RECORD_SEPARATOR = ";"
        private const val FIELD_SEPARATOR = "|"
        private val KEY_RECORDS = stringPreferencesKey("records")

        @Volatile
        private var instance: ScanRecordStore? = null

        fun getInstance(context: Context): ScanRecordStore =
            instance ?: synchronized(this) {
                instance ?: ScanRecordStore(context.applicationContext).also { instance = it }
            }
    }
}
