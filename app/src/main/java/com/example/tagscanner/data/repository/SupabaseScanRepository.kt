package com.example.tagscanner.data.repository

import com.example.tagscanner.data.remote.SupabaseClientProvider
import com.example.tagscanner.data.remote.dto.ScanHistoryDto
import com.example.tagscanner.data.remote.mapper.toDomain
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.repository.ScanRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SupabaseScanRepository : ScanRepository {
    private val client = SupabaseClientProvider.client

    override fun observeScans() : Flow<List<ScanResult>> = flow {
        val rows = client
            .from("scan_history_view")
            .select()
            .decodeList<ScanHistoryDto>()

        emit(rows.map { it.toDomain() })
    }

    override suspend fun getScanById(id: String): ScanResult? {
        return client
            .from("scan_history_view")
            .select {
                filter {
                    eq("id",id)
                }
            }
            .decodeList<ScanHistoryDto>()
            .firstOrNull()
            ?.toDomain()
    }

    override suspend fun saveScan(scanResult: ScanResult){
        TODO("Saving to supaabse")
    }
}