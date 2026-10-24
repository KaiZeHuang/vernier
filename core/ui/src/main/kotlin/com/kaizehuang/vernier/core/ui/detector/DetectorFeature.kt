package com.kaizehuang.vernier.core.ui.detector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.report.DeviceReport
import com.kaizehuang.vernier.core.scan.DetectorSummary
import kotlinx.coroutines.flow.StateFlow

/**
 * A detector as the application composes it.
 *
 * This is the whole integration surface of a detector: the application's generated card list
 * collects every feature, and everything central (coordination, dashboard, export, notifications,
 * the startup policy and settings) works on sessions and consent cards, so adding or changing a
 * detector touches only that detector and its catalog entry.
 */
public interface DetectorFeature {
    public val id: DetectorId

    /** How the application asks for each of the detector's consents; most detectors have none. */
    public val consentCards: List<ConsentCard> get() = emptyList()

    /** Binds the detector's state holder to the current composition's owner and returns its session. */
    @Composable
    public fun rememberSession(): DetectorSession
}

@Stable
public interface DetectorSession {
    public val id: DetectorId

    /** Published summary of the current scan, observed by the scan coordinator. */
    public val summary: StateFlow<DetectorSummary>

    /** Export projection of the current scan. */
    public fun report(): DetectorReport

    public fun rescan()

    /** The detector's own dashboard card, including any dialogs it owns. */
    @Composable
    public fun Card()
}

/** The device specification shown under the detector cards and appended to exports. */
public interface DeviceProfileFeature {
    @Composable
    public fun rememberSession(): DeviceProfileSession
}

@Stable
public interface DeviceProfileSession {
    public fun report(): DeviceReport

    @Composable
    public fun Card()
}
