package com.example.tagscanner.data.repository

import com.example.tagscanner.domain.model.ColorInterpretation
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.model.ScanSource

object FakeScanData {

    val scans = listOf(
        ScanResult(
            id = 1,
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 5,
            source = ScanSource.GALLERY_IMAGE,
            colorMeasurement = ColorMeasurement(
                red = 42,
                green = 180,
                blue = 85,
                hue = 140f,
                saturation = 0.76f,
                value = 0.71f,
                confidence = 0.92f
            ),
            interpretation = ColorInterpretation(
                label = "Green",
                description = "Tag indicates normal state",
                severity = InterpretationSeverity.NORMAL
            ),
            regionOfInterest = null,
            details = ScanDetails(
                provider = "FreshFarm Co.",
                product = "Yogurt Pack A",
                batch = "B-1024",
                category = "Food"
            ),
            qualityScore = 96,
            note = "Sample gallery scan"
        ),
        ScanResult(
            id = 2,
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60,
            source = ScanSource.LIVE_CAMERA,
            colorMeasurement = ColorMeasurement(
                red = 230,
                green = 190,
                blue = 40,
                hue = 48f,
                saturation = 0.83f,
                value = 0.90f,
                confidence = 0.86f
            ),
            interpretation = ColorInterpretation(
                label = "Yellow",
                description = "Tag indicates warning state",
                severity = InterpretationSeverity.WARNING
            ),
            regionOfInterest = null,
            details = ScanDetails(
                provider = "MediSupply Ltd.",
                product = "Vaccine Box B",
                batch = "LOT-88A",
                category = "Medical"
            ),
            qualityScore = 62,
            note = "Sample live scan"
        ),
        ScanResult(
            id = 3,
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60 * 3,
            source = ScanSource.LIVE_CAMERA,
            colorMeasurement = ColorMeasurement(
                red = 210,
                green = 40,
                blue = 35,
                hue = 2f,
                saturation = 0.83f,
                value = 0.82f,
                confidence = 0.88f
            ),
            interpretation = ColorInterpretation(
                label = "Red",
                description = "Tag indicates critical state",
                severity = InterpretationSeverity.CRITICAL
            ),
            regionOfInterest = null,
            details = ScanDetails(
                provider = "SafePack Distribution",
                product = "Fresh Meat Tray",
                batch = "M-2045",
                category = "Food"
            ),
            qualityScore = 24,
            note = "Sample critical scan"
        ),
        ScanResult(
            id = 4,
            timestampMillis = System.currentTimeMillis() - 1000L * 60 * 60 * 5,
            source = ScanSource.LIVE_CAMERA,
            colorMeasurement = ColorMeasurement(
                red = 159,
                green = 43,
                blue = 104,
                hue = 2f,
                saturation = 0.83f,
                value = 0.82f,
                confidence = 0.88f
            ),
            interpretation = ColorInterpretation(
                label = "Purple",
                description = "Tag indicates unknown state",
                severity = InterpretationSeverity.UNKNOWN
            ),
            regionOfInterest = null,
            details = ScanDetails(
                provider = "SafePack Distribution",
                product = "Fresh Meat Tray",
                batch = "M-2045",
                category = "Food"
            ),
            qualityScore = 24,
            note = "Sample unknown scan"
        )
    )
}