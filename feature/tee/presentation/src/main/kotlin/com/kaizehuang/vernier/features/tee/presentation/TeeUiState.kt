package com.kaizehuang.vernier.features.tee.presentation

import com.kaizehuang.vernier.features.tee.domain.TeeReport
import com.kaizehuang.vernier.features.tee.presentation.model.TeeCardModel

enum class TeeUiStage {
    LOADING,
    READY,
    FAILED,
}

data class TeeUiState(
    val stage: TeeUiStage,
    val report: TeeReport,
    val cardModel: TeeCardModel,
    val showDetailsDialog: Boolean = false,
    val showCertificatesDialog: Boolean = false,
)
