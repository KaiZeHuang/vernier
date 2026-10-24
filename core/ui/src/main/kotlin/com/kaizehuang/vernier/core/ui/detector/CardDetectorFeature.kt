package com.kaizehuang.vernier.core.ui.detector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaizehuang.vernier.core.detector.Detector
import com.kaizehuang.vernier.core.evidence.DetectorId
import com.kaizehuang.vernier.core.report.DetectorHeadline
import com.kaizehuang.vernier.core.report.DetectorReport
import com.kaizehuang.vernier.core.scan.DetectorSummary
import kotlinx.coroutines.flow.StateFlow

/**
 * Shows [detector] on the dashboard as a card whose content is [card].
 *
 * This is the whole UI integration of a detector whose card only renders its model, which is
 * every detector except those whose card keeps state of its own. The session scans through the
 * detector's scanner, and it summarizes and exports from the card model that is currently shown.
 */
public class CardDetectorFeature<R : Any, M : DetectorHeadline>(
    private val detector: Detector<R, M>,
    override val consentCards: List<ConsentCard> = emptyList(),
    private val card: @Composable (M) -> Unit,
) : DetectorFeature {
    override val id: DetectorId = detector.id

    @Composable
    override fun rememberSession(): DetectorSession {
        val context = LocalContext.current
        // Every card session shares this view model class, so the detector id keeps them apart.
        val viewModel: CardDetectorViewModel<R, M> = viewModel(
            key = detector.id.value,
            factory = remember(context) {
                CardDetectorViewModel.factory(detector) { detector.createScanner(context.applicationContext) }
            },
        )
        return remember(viewModel) { CardDetectorSession(detector, viewModel, card) }
    }
}

private class CardDetectorSession<R : Any, M : DetectorHeadline>(
    private val detector: Detector<R, M>,
    private val viewModel: CardDetectorViewModel<R, M>,
    private val card: @Composable (M) -> Unit,
) : DetectorSession {
    override val id: DetectorId = detector.id

    override val summary: StateFlow<DetectorSummary> = viewModel.summary

    override fun report(): DetectorReport = detector.export(viewModel.state.value.cardModel)

    override fun rescan() {
        viewModel.rescan()
    }

    @Composable
    override fun Card() {
        val state by viewModel.state.collectAsState()
        card(state.cardModel)
    }
}
