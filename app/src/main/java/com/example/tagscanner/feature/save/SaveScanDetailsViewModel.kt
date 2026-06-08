package com.example.tagscanner.feature.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.core.util.qualityScoreFor
import com.example.tagscanner.data.remote.storage.SupabaseImageStorage
import com.example.tagscanner.data.repository.SupabaseScanRepository
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.PendingScan
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.model.ScanSource
import com.example.tagscanner.domain.repository.ActiveLabelProfileRepository
import com.example.tagscanner.domain.repository.PendingScanResultRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaveScanDetailsViewModel(
    private val pendingResultScanRepository: PendingScanResultRepository = PendingScanResultRepository,
    private val scanRepository: ScanRepository = SupabaseScanRepository(),
    private val imageStorage: SupabaseImageStorage = SupabaseImageStorage()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveScanDetailsUiState())
    val uiState: StateFlow<SaveScanDetailsUiState> = _uiState.asStateFlow()
    private var pendingScan: PendingScan? = null

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
        val pending = pendingScan ?: return

        if (!state.canSave) return

        val details = ScanDetails(
            provider = state.provider.trim(),
            product = state.product.trim(),
            batch = state.batch.trim(),
            category = state.category.trim().takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
        val imagePath = pending.previewJpegBytes?.let {bytes ->
            imageStorage.uploadScanPreview(bytes)
        }

        val scanResult = ScanResult(
            id = "",
            timestampMillis = System.currentTimeMillis(),
            source = pending.source,
            colorMeasurement = pending.result.colorMeasurement,
            interpretation = pending.result.interpretation,
            regionOfInterest = pending.result.regionOfInterest,
            details = details,
            qualityScore = qualityScoreFor(pending.result),
            note = state.note.trim().takeIf { it.isNotBlank() },
            imagePath = imagePath
        )

            scanRepository.saveScan(scanResult)

            _uiState.value = _uiState.value.copy(
                pendingReuseDetails = details
            )
        }
    }

    fun clearPendingReuseDetails() {
        _uiState.value = _uiState.value.copy(
            pendingReuseDetails = null
        )
    }

    private fun observePendingResult() {
        viewModelScope.launch {
            pendingResultScanRepository.observePendingScan().collect { scan ->
                pendingScan = scan

                val profileName = ActiveLabelProfileRepository.currentProfile().displayName
                _uiState.value = _uiState.value.copy(
                    scanResult = scan?.result,
                    provider = scan?.initialDetails?.provider.orEmpty(),
                    product = scan?.initialDetails?.product.orEmpty(),
                    batch = scan?.initialDetails?.batch.orEmpty(),
                    category = profileName,
                    categoryLocked = true
                )
            }
        }
    }
}