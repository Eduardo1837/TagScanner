package com.example.tagscanner.domain.repository

import com.example.tagscanner.domain.model.ScanDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActiveScanDetailsRepository {

    private val activeDetails = MutableStateFlow<ScanDetails?>(null)

    fun observeActiveDetails(): Flow<ScanDetails?> {
        return activeDetails.asStateFlow()
    }

    fun setActiveDetails(details: ScanDetails) {
        activeDetails.value = details
    }

    fun clearActiveDetails() {
        activeDetails.value = null
    }
}