package com.example.tagscanner.domain.repository

import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.PendingScan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PendingScanResultRepository {

    private val pendingScan = MutableStateFlow<PendingScan?>(null)

    fun observePendingScan(): Flow<PendingScan?> {
        return pendingScan.asStateFlow()
    }

    fun setPendingScan(scan: PendingScan) {
        pendingScan.value = scan
    }

    fun clearPendingScan() {
        pendingScan.value = null
    }
}