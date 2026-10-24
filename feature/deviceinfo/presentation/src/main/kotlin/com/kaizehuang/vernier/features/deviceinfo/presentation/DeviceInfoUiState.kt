package com.kaizehuang.vernier.features.deviceinfo.presentation

import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoReport
import com.kaizehuang.vernier.features.deviceinfo.presentation.model.DeviceInfoCardModel

enum class DeviceInfoUiStage {
    LOADING,
    READY,
    FAILED,
}

data class DeviceInfoUiState(
    val stage: DeviceInfoUiStage,
    val report: DeviceInfoReport,
    val cardModel: DeviceInfoCardModel,
)
