package com.kaizehuang.vernier.ui

import com.kaizehuang.vernier.features.settings.presentation.model.SettingsUpdateStatus
import com.kaizehuang.vernier.features.update.presentation.UpdateCheckStatus

internal fun UpdateCheckStatus.toSettingsUpdateStatus(): SettingsUpdateStatus = when (this) {
    UpdateCheckStatus.IDLE -> SettingsUpdateStatus.IDLE
    UpdateCheckStatus.CHECKING -> SettingsUpdateStatus.CHECKING
    UpdateCheckStatus.CURRENT -> SettingsUpdateStatus.CURRENT
    UpdateCheckStatus.AVAILABLE -> SettingsUpdateStatus.AVAILABLE
    UpdateCheckStatus.FAILED -> SettingsUpdateStatus.FAILED
}
