package com.example.tagscanner.domain.model

data class ScanDetails(
    val provider: String,
    val product: String,
    val batch: String,
    val category: String? = null
)
