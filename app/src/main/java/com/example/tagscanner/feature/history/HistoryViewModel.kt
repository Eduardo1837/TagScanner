package com.example.tagscanner.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val scanRepository: ScanRepository = FakeScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        observeScans()
    }

    private fun observeScans() {
        viewModelScope.launch {
            scanRepository.observeScans().collect { scans ->
                _uiState.value = HistoryUiState(
                    scans = scans
                )
            }
        }
    }
}