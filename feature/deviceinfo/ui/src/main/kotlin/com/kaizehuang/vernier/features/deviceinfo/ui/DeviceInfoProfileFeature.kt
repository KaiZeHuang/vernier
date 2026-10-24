package com.kaizehuang.vernier.features.deviceinfo.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaizehuang.vernier.core.report.DeviceReport
import com.kaizehuang.vernier.core.ui.detector.DeviceProfileFeature
import com.kaizehuang.vernier.core.ui.detector.DeviceProfileSession
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoScanner
import com.kaizehuang.vernier.features.deviceinfo.presentation.toDeviceReport
import com.kaizehuang.vernier.features.deviceinfo.ui.card.DeviceInfoCard

class DeviceInfoProfileFeature(
    private val createScanner: (Context) -> DeviceInfoScanner,
) : DeviceProfileFeature {
    @Composable
    override fun rememberSession(): DeviceProfileSession {
        val context = LocalContext.current
        val viewModel: DeviceInfoViewModel = viewModel(
            factory = remember(context) { DeviceInfoViewModel.factory { createScanner(context.applicationContext) } },
        )
        return remember(viewModel) { DeviceInfoProfileSession(viewModel) }
    }
}

private class DeviceInfoProfileSession(
    private val viewModel: DeviceInfoViewModel,
) : DeviceProfileSession {
    override fun report(): DeviceReport = viewModel.uiState.value.cardModel.toDeviceReport()

    @Composable
    override fun Card() {
        val state by viewModel.uiState.collectAsState()
        DeviceInfoCard(model = state.cardModel)
    }
}
