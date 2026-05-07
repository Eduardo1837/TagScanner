package com.example.tagscanner.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProviderInsertDto(
    val name: String
)
@Serializable
data class ProviderDto(
    val id: String,
    val name: String
)

@Serializable
data class ProductInsertDto(
    @SerialName("provider_id")
    val providerId: String,

    val name: String,

    val category: String?
)

@Serializable
data class ProductDto(
    val id: String,

    @SerialName("provider_id")
    val providerId: String,

    val name: String,

    val category: String?
)

@Serializable
data class BatchInsertDto(
    @SerialName("product_id")
    val productId: String,

    val code: String
)

@Serializable
data class BatchDto(
    val id: String,

    @SerialName("product_id")
    val productId: String,

    val code: String
)

@Serializable
data class ScanInsertDto(
    @SerialName("batch_id")
    val batchId: String,

    val source: String,

    val red: Int,
    val green: Int,
    val blue: Int,

    val hue: Float,
    val saturation: Float,
    val value:  Float,
    val confidence: Float,

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