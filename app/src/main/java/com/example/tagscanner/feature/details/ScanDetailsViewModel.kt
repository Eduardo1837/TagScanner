package com.example.tagscanner.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanDetailsViewModel(
    private val scanRepository: ScanRepository = FakeScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanDetailsUiState(isLoading = true))
    val uiState: StateFlow<ScanDetailsUiState> = _uiState.asStateFlow()

    fun loadScan(scanId: Long) {
        viewModelScope.launch {
            val scan = scanRepository.getScanById(scanId)

            _uiState.value = ScanDetailsUiState(
                scan = scan,
                isLoading = false,
                errorMessage = if (scan == null) "Scan not found" else null
            )
        }
    }
}