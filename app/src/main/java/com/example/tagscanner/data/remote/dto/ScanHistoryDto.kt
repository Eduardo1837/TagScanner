package com.example.tagscanner.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanHistoryDto(
    val id: String,

    @SerialName("scanned_at")
    val scannedAt: String,

    val source: String,

    @SerialName("provider_name")
    val providerName: String,

    @SerialName("product_name")
    val productName: String,

    val category: String?,

    @SerialName("batch_code")
    val batchCode: String,

    val red: Int,
    val green: Int,
    val blue: Int,

    val hue: Float?,
    val saturation: Float?,
    val value: Float?,
    val confidence: Float?,

    @SerialName("interpretation_label")
    val interpretationLabel: String,

    @SerialName("interpretation_description")
    val interpretationDescription: String,

    @SerialName("interpretation_severity")
    val interpretationSeverity: String,

    @SerialName("roi_x")
    val roiX: Float?,

    @SerialName("roi_y")
    val roiY: Float?,

    @SerialName("roi_width")
    val roiWidth: Float?,

    @SerialName("roi_height")
    val roiHeight: Float?,

    @SerialName("quality_score")
    val qualityScore: Int?,

    val note: String?,

    @SerialName("image_path")
    val imagePath: String?

)