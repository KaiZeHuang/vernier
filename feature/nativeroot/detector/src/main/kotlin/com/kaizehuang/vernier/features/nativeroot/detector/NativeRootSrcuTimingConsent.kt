package com.kaizehuang.vernier.features.nativeroot.detector

import android.content.Context
import com.kaizehuang.vernier.core.detector.ConsentDecision
import com.kaizehuang.vernier.core.detector.ConsentId
import com.kaizehuang.vernier.core.detector.DetectorConsent
import com.kaizehuang.vernier.features.nativeroot.data.probes.SrcuTimingConsentStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow

/** Optional active diagnostic, disabled until the host records an explicit choice. */
public object NativeRootSrcuTimingConsent : DetectorConsent {
    override val id: ConsentId = ConsentId("srcu_timing_experiment")
    override fun decisions(context: Context): Flow<ConsentDecision> =
        SrcuTimingConsentStore(context).decisions
    override suspend fun decide(context: Context, granted: Boolean): Unit = withContext(Dispatchers.IO) {
        SrcuTimingConsentStore(context).decide(granted)
    }
}
