package com.kaizehuang.vernier.features.nativeroot.data.probes

import android.content.Context
import com.kaizehuang.vernier.core.detector.ConsentDecision
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Only the host process reads this preference; the carrier receives no persistent permissions. */
class SrcuTimingConsentStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("srcu_timing", Context.MODE_PRIVATE)
    fun decision(): ConsentDecision = when {
        !prefs.contains("enabled") -> ConsentDecision.UNDECIDED
        prefs.getBoolean("enabled", false) -> ConsentDecision.GRANTED
        else -> ConsentDecision.DECLINED
    }
    val decisions: Flow<ConsentDecision> get() = callbackFlow {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(decision())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(decision())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
    fun decide(granted: Boolean) {
        check(prefs.edit().putBoolean("enabled", granted).commit()) { "Could not save experiment choice" }
    }
}
