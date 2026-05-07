package com.example.tagscanner.domain.model

data class PendingScan(
    val result: AnalysisResult,
    val source: ScanSource,
    val previewJpegBytes: ByteArray?
)