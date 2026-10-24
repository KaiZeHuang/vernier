package com.kaizehuang.vernier.features.update.presentation

import com.kaizehuang.vernier.features.update.domain.AvailableNightlyUpdate

enum class UpdateCheckStatus {
    IDLE,
    CHECKING,
    CURRENT,
    AVAILABLE,
    FAILED,
}

sealed interface UpdateDownloadResolution {
    data class Ready(val url: String) : UpdateDownloadResolution

    data object Refreshed : UpdateDownloadResolution

    data object Current : UpdateDownloadResolution

    data object Failed : UpdateDownloadResolution
}

data class UpdateUiState(
    val status: UpdateCheckStatus = UpdateCheckStatus.IDLE,
    val availableUpdate: AvailableNightlyUpdate? = null,
    val isDialogVisible: Boolean = false,
)
