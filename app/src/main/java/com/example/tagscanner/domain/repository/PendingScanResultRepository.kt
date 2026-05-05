package com.example.tagscanner.domain.repository

import com.example.tagscanner.domain.model.AnalysisResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PendingScanResultRepository {

    private val pendingResult = MutableStateFlow<AnalysisResult?>(null)

    fun observePendingResult(): Flow<AnalysisResult?> {
        return pendingResult.asStateFlow()
    }

    fun setPendingResult(result: AnalysisResult) {
        pendingResult.value = result
    }

    fun clearPendingResult() {
        pendingResult.value = null
    }
}