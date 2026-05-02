package com.example.tagscanner.domain.repository

import androidx.compose.runtime.MutableState
import com.example.tagscanner.data.repository.FakeScanData
import com.example.tagscanner.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeScanRepository : ScanRepository {
    private val scans = MutableStateFlow(FakeScanData.scans)

    override fun observeScans(): Flow<List<ScanResult>> {
        return scans.asStateFlow()
    }

    override suspend fun getScanById(id: Long): ScanResult? {
        return scans.value.firstOrNull{ scan ->
            scan.id == id
        }
    }

    override suspend fun saveScan(scanResult: ScanResult) {
        val updatedScans = listOf(scanResult) + scans.value
        scans.value = updatedScans
    }
}