package com.kaizehuang.vernier.features.settings.presentation.model

data class SettingsUiState(
    val versionName: String,
    val versionCode: Int,
    val buildTimeUtc: String,
    val buildHash: String,
    val updateStatus: SettingsUpdateStatus,
    val gitHubAccelerationEnabled: Boolean,
)

enum class SettingsUpdateStatus {
    IDLE,
    CHECKING,
    CURRENT,
    AVAILABLE,
    FAILED,
}
