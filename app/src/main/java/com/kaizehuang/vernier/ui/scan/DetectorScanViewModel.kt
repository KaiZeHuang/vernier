package com.kaizehuang.vernier.ui.scan

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaizehuang.vernier.core.scan.DetectorSummary
import com.kaizehuang.vernier.core.scan.ScanClock
import com.kaizehuang.vernier.core.scan.ScanCoordinator
import kotlinx.coroutines.flow.StateFlow

/**
 * Hosts the scan coordinator for the lifetime of the activity's view models.
 *
 * Detector view models survive configuration changes, so the session they belong to must survive
 * them too; timing kept in composition restarted on every recreation and reported a near-zero
 * duration for a scan that had long finished.
 */
internal class DetectorScanViewModel(
    detectors: List<StateFlow<DetectorSummary>>,
) : ViewModel() {

    val coordinator = ScanCoordinator(AndroidScanClock, viewModelScope, detectors)

    companion object {
        fun factory(detectors: List<StateFlow<DetectorSummary>>): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetectorScanViewModel(detectors) as T
                }
            }
        }
    }
}

private object AndroidScanClock : ScanClock {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()

    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
