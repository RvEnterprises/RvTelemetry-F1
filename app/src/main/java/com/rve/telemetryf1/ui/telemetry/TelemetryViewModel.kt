package com.rve.telemetryf1.ui.telemetry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.rve.telemetryf1.data.TelemetryRepository
import com.rve.telemetryf1.data.PlayerTelemetry
import javax.inject.Inject

@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val telemetryRepository: TelemetryRepository
) : ViewModel() {

    val uiState: StateFlow<PlayerTelemetry> = telemetryRepository
        .telemetry
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerTelemetry())

    init {
        viewModelScope.launch {
            telemetryRepository.startListening()
        }
    }
}
