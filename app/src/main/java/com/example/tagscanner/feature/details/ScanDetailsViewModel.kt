package com.example.tagscanner.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.data.repository.SupabaseScanRepository
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanDetailsViewModel(
    private val scanRepository: ScanRepository = SupabaseScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanDetailsUiState(isLoading = true))
    val uiState: StateFlow<ScanDetailsUiState> = _uiState.asStateFlow()

    fun loadScan(scanId: String) {
        viewModelScope.launch {
            val scan = scanRepository.getScanById(scanId)

            _uiState.value = ScanDetailsUiState(
                scan = scan,
                isLoading = false,
                errorMessage = if (scan == null) "Scan not found" else null
            )
        }
    }

    fun deleteCurrentScan() {
        val scan = _uiState.value.scan ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isDeleting = true,
                    errorMessage = null
                )

                scanRepository.deleteScan(scan)

                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    deleteCompleted = true
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = exception.message ?: "Could not delete scan"
                )
            }
        }
    }
}