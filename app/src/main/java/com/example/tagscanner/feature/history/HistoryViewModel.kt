package com.example.tagscanner.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.data.repository.SupabaseScanRepository
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.temporal.TemporalQuery

class HistoryViewModel(
    private val scanRepository: ScanRepository = SupabaseScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        refreshScans()
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun refreshScans() {
        viewModelScope.launch {
            scanRepository.observeScans().collect { scans ->
                _uiState.value = _uiState.value.copy(scans = scans)
            }
        }
    }

    fun onProviderSelected(provider: String?) {
        _uiState.value = _uiState.value.copy(selectedProvider = provider)
    }

    fun onProductSelected(product: String?) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun onBatchSelected(batch: String?) {
        _uiState.value = _uiState.value.copy(selectedBatch = batch)
    }
}