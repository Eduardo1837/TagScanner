package com.example.tagscanner.domain.repository


import com.example.tagscanner.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    fun observeScans(): Flow<List<ScanResult>>

    suspend fun getScanById(id: Long): ScanResult?

    suspend fun saveScan(scanResult: ScanResult)
}