package com.example.tagscanner.feature.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.PendingScanResultRepository
import com.example.tagscanner.domain.repository.PendingScanResultRepository.observePendingResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaveScanDetailsViewModel(
    private val pendingResultScanRepository: PendingScanResultRepository = PendingScanResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveScanDetailsUiState())
    val uiState: StateFlow<SaveScanDetailsUiState> = _uiState.asStateFlow()

    init {
        observePendingResult()
    }

    fun setScanResult(result: AnalysisResult) {
        _uiState.value = _uiState.value.copy(scanResult = result)
    }

    fun onProviderChanged(value: String) {
        _uiState.value = _uiState.value.copy(provider = value)
    }

    fun onProductChanged(value: String){
        _uiState.value = _uiState.value.copy(product = value)
    }

    fun onBatchChanged(value: String) {
        _uiState.value = _uiState.value.copy(batch = value)
    }

    fun onCategoryChanged(value: String) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onNoteChanged(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun applyProviderSuggestion(provider: String) {
        _uiState.value = _uiState.value.copy(provider = provider)
    }

    fun onSaveScanClicked() {
        val state = _uiState.value

        if (!state.canSave) return

        _uiState.value = state.copy(
            pendingReuseDetails = ScanDetails(
                provider = state.provider.trim(),
                product = state.product.trim(),
                batch = state.batch.trim(),
                category = state.category.trim().takeIf { it.isNotBlank() }
            )
        )
    }

    fun clearPendingReuseDetails() {
        _uiState.value = _uiState.value.copy(
            pendingReuseDetails = null
        )
    }

    private fun observePendingResult() {
        viewModelScope.launch {
            pendingResultScanRepository.observePendingResult().collect { result ->
                _uiState.value = _uiState.value.copy(
                    scanResult = result
                )
            }
        }
    }
}