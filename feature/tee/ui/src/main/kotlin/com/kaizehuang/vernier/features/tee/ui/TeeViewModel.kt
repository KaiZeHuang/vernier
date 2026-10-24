package com.kaizehuang.vernier.features.tee.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaizehuang.vernier.core.detector.DetectorScanner
import com.kaizehuang.vernier.core.scan.DetectorSummary
import com.kaizehuang.vernier.core.scan.ScanSessionRunner
import com.kaizehuang.vernier.core.scan.summarize
import com.kaizehuang.vernier.features.tee.detector.TeeDetector
import com.kaizehuang.vernier.features.tee.domain.TeeReport
import com.kaizehuang.vernier.features.tee.domain.TeeScanStage
import com.kaizehuang.vernier.features.tee.presentation.TeeCardModelMapper
import com.kaizehuang.vernier.features.tee.presentation.TeeUiStage
import com.kaizehuang.vernier.features.tee.presentation.TeeUiState
import com.kaizehuang.vernier.features.tee.presentation.model.TeeFooterActionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TeeViewModel(
    private val repository: DetectorScanner<TeeReport>,
    private val mapper: TeeCardModelMapper = TeeCardModelMapper(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeeUiState(
            stage = TeeUiStage.LOADING,
            report = TeeReport.loading(),
            cardModel = mapper.map(TeeReport.loading(), isExpanded = false),
        ),
    )
    val uiState: StateFlow<TeeUiState> = _uiState.asStateFlow()

    val summary: StateFlow<DetectorSummary> = uiState
        .map { it.toDetectorSummary() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, uiState.value.toDetectorSummary())

    private fun TeeUiState.toDetectorSummary(): DetectorSummary =
        cardModel.summarize(TeeDetector.id, ready = stage != TeeUiStage.LOADING)

    private val scans = ScanSessionRunner(viewModelScope)

    init {
        rescan()
    }

    fun onExpandedChange(expanded: Boolean) {
        _uiState.update { state ->
            // Expanding is presentation-only. Rebuilding all attestation evidence on every tap
            // blocks the main thread, especially when the report contains large key chains.
            if (state.cardModel.isExpanded == expanded) state
            else state.copy(cardModel = state.cardModel.copy(isExpanded = expanded))
        }
    }

    fun onFooterAction(actionId: TeeFooterActionId) {
        when (actionId) {
            TeeFooterActionId.RESCAN -> rescan()
            TeeFooterActionId.DETAILS -> _uiState.update { it.copy(showDetailsDialog = true) }
            TeeFooterActionId.CERTIFICATES -> _uiState.update { it.copy(showCertificatesDialog = true) }
        }
    }

    fun dismissDetails() {
        _uiState.update { it.copy(showDetailsDialog = false) }
    }

    fun dismissCertificates() {
        _uiState.update { it.copy(showCertificatesDialog = false) }
    }

    fun rescan() {
        scans.launch(
            begin = {
                val expanded = _uiState.value.cardModel.isExpanded
                val loading = TeeReport.loading()
                _uiState.update { state ->
                    state.copy(
                        stage = TeeUiStage.LOADING,
                        report = loading,
                        cardModel = mapper.map(loading, expanded),
                    )
                }
            },
            collect = { repository.scan() },
            publish = { report ->
                val stage = when {
                    report.stage == TeeScanStage.FAILED -> TeeUiStage.FAILED
                    else -> TeeUiStage.READY
                }
                _uiState.update { state ->
                    state.copy(
                        stage = stage,
                        report = report,
                        cardModel = mapper.map(report, state.cardModel.isExpanded),
                    )
                }
            },
        )
    }

    companion object {
        fun factory(createScanner: () -> DetectorScanner<TeeReport>): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TeeViewModel(createScanner()) as T
                }
            }
        }
    }
}
