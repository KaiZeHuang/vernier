package com.kaizehuang.vernier.features.deviceinfo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoReport
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoScanner
import com.kaizehuang.vernier.features.deviceinfo.domain.DeviceInfoStage
import com.kaizehuang.vernier.features.deviceinfo.presentation.DeviceInfoCardModelMapper
import com.kaizehuang.vernier.features.deviceinfo.presentation.DeviceInfoUiStage
import com.kaizehuang.vernier.features.deviceinfo.presentation.DeviceInfoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceInfoViewModel(
    private val repository: DeviceInfoScanner,
    private val mapper: DeviceInfoCardModelMapper = DeviceInfoCardModelMapper(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DeviceInfoUiState(
            stage = DeviceInfoUiStage.LOADING,
            report = DeviceInfoReport.loading(),
            cardModel = mapper.map(DeviceInfoReport.loading()),
        ),
    )
    val uiState: StateFlow<DeviceInfoUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loading = DeviceInfoReport.loading()
            _uiState.update {
                it.copy(
                    stage = DeviceInfoUiStage.LOADING,
                    report = loading,
                    cardModel = mapper.map(loading),
                )
            }

            val report = repository.scan()
            _uiState.update {
                it.copy(
                    stage = if (report.stage == DeviceInfoStage.FAILED) {
                        DeviceInfoUiStage.FAILED
                    } else {
                        DeviceInfoUiStage.READY
                    },
                    report = report,
                    cardModel = mapper.map(report),
                )
            }
        }
    }

    companion object {
        fun factory(createScanner: () -> DeviceInfoScanner): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DeviceInfoViewModel(createScanner()) as T
                }
            }
        }
    }
}
