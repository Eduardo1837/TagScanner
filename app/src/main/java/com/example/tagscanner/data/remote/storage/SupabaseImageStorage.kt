package com.example.tagscanner.data.remote.storage

import com.example.tagscanner.data.remote.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import java.util.UUID
import io.ktor.http.ContentType

class SupabaseImageStorage {
    private val bucket = SupabaseClientProvider.client.storage.from("scan-images")

    suspend fun uploadScanPreview(bytes: ByteArray): String {
        val path = "previews/${UUID.randomUUID()}.jpg"

        bucket.upload(path, bytes) {
            upsert = false
            contentType = ContentType.Image.JPEG
        }

        return path
    }

    suspend fun deleteScanPreview(path: String) {
        bucket.delete(path)
    }

    fun publicUrl(path: String): String{
        return bucket.publicUrl(path)
    }
}