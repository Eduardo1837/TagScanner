package com.example.tagscanner.data.repository

import com.example.tagscanner.data.remote.SupabaseClientProvider
import com.example.tagscanner.data.remote.SupabaseClientProvider.client
import com.example.tagscanner.data.remote.dto.BatchDto
import com.example.tagscanner.data.remote.dto.BatchInsertDto
import com.example.tagscanner.data.remote.dto.ProductDto
import com.example.tagscanner.data.remote.dto.ProductInsertDto
import com.example.tagscanner.data.remote.dto.ProviderDto
import com.example.tagscanner.data.remote.dto.ProviderInsertDto
import com.example.tagscanner.data.remote.dto.ScanHistoryDto
import com.example.tagscanner.data.remote.mapper.toDomain
import com.example.tagscanner.data.remote.mapper.toScanInsertDto
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
        val details = scanResult.details ?: error("Scan details are required before saving")

        val provider = getOrCreateprovider(details.provider.trim())
        val product = getOrCreateProduct(
            providerId = provider.id,
            name = details.product.trim(),
            category = details.category?.trim()?.takeIf { it.isNotBlank() }
        )
        val batch = getOrCreateBatch(
            productId = product.id,
            code = details.batch.trim()
        )
        
        client
            .from("scans")
            .insert(scanResult.toScanInsertDto(batch.id))
    }
}

private suspend fun getOrCreateprovider(name: String): ProviderDto {
    val existing = client
        .from("providers")
        .select {
            filter {
                eq("name",name)
            }
            limit(1)
        }
        .decodeList<ProviderDto>()
        .firstOrNull()

    if(existing != null) return existing

    return client
        .from("providers")
        .insert(ProviderInsertDto(name = name)){
            select()
        }
        .decodeSingle<ProviderDto>()
}

private suspend fun getOrCreateProduct(
    providerId: String,
    name: String,
    category: String?
): ProductDto {
    val existing = client
        .from("products")
        .select { 
            filter { 
                eq("provider_id", providerId)
                eq("name", name)
            }
            limit(1)
        }
        .decodeList< ProductDto>()
        .firstOrNull()
    
    if(existing != null) return existing
    
    return client
        .from("products")
        .insert(
            ProductInsertDto(
                providerId = providerId,
                name = name,
                category = category
            )
        ) {
            select()
        }
        .decodeSingle<ProductDto>()
}

private suspend fun getOrCreateBatch(
    productId: String,
    code: String
): BatchDto {
    val existing = client
        .from("batches")
        .select { 
            filter { 
                eq("product_id", productId)
                eq("code", code)
            }
            limit(1)
        }
        .decodeList<BatchDto>()
        .firstOrNull()
    
    if(existing != null) return existing
    
    return client
        .from("batches")
        .insert(
            BatchInsertDto(
                productId = productId,
                code = code
            )
        ) {
            select()
        }
        .decodeSingle<BatchDto>()
}