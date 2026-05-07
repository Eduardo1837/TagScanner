package com.example.tagscanner.data.remote.mapper

import com.example.tagscanner.data.remote.dto.ScanHistoryDto
import com.example.tagscanner.domain.model.ColorInterpretation
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.model.ScanSource
import java.time.Instant

fun ScanHistoryDto.toDomain(): ScanResult {
    return ScanResult(
        id = id,
        timestampMillis = Instant.parse(scannedAt).toEpochMilli(),
        source = when (source) {
            "live_camera" -> ScanSource.LIVE_CAMERA
            else -> ScanSource.GALLERY_IMAGE
        },
        colorMeasurement = ColorMeasurement(
            red = red,
            green = green,
            blue = blue,
            hue = hue ?: 0f,
            saturation = saturation ?: 0f,
            value = value ?: 0f,
            confidence = confidence ?: 0f
        ),
        interpretation = ColorInterpretation(
            label = interpretationLabel,
            description = interpretationDescription ?: "",
            severity = when (interpretationSeverity) {
                "normal" -> InterpretationSeverity.NORMAL
                "warning" -> InterpretationSeverity.WARNING
                "critical" -> InterpretationSeverity.CRITICAL
                else -> InterpretationSeverity.UNKNOWN
            }
        ),
        regionOfInterest = if (
            roiX != null &&
            roiY != null &&
            roiWidth != null &&
            roiHeight != null
        ) {
            RegionOfInterest(
                x = roiX,
                y = roiY,
                width = roiWidth,
                height = roiHeight
            )
        } else {
            null
        },
        details = ScanDetails(
            provider = providerName,
            product = productName,
            batch = batchCode,
            category = category
        ),
        qualityScore = qualityScore,
        note = note
    )
}