package com.example.tagscanner.processing.image

import android.graphics.Bitmap
import android.graphics.Color
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.RgbColor
import kotlin.math.roundToInt
import androidx.core.graphics.get

class RoiColorSampler {

    fun sampleCenter(
        bitmap: Bitmap,
        sampleFraction: Float = 0.20f
    ): RgbColor {
        require(sampleFraction in 0.01f..1.0f){
            "sampleFraction needed between 0.01..1.0"
        }

        val sampleWidth = (bitmap.width * sampleFraction).roundToInt()
            .coerceAtLeast(1)
        val sampleHeight = (bitmap.height * sampleFraction).roundToInt()
            .coerceAtLeast(1)
        val startX = ((bitmap.width - sampleWidth) / 2)
            .coerceAtLeast(0)

        val startY = ((bitmap.height - sampleHeight) / 2)
            .coerceAtLeast(0)

        return sampleRegion(
            bitmap = bitmap,
            region = RegionOfInterest(
                x = startX.toFloat(),
                y = startY.toFloat(),
                width = sampleWidth.toFloat(),
                height = sampleHeight.toFloat()
            )
        )
    }

    fun sampleRegion(
        bitmap: Bitmap,
        region: RegionOfInterest
    ): RgbColor {
        val startX = region.x.roundToInt()
            .coerceIn(0, bitmap.width - 1)

        val startY = region.y.roundToInt()
            .coerceIn(0, bitmap.height - 1)

        val endX = (region.x + region.width).roundToInt()
            .coerceIn(startX + 1, bitmap.width)

        val endY = (region.y + region.height).roundToInt()
            .coerceIn(startY + 1, bitmap.height)

        var redSum = 0L
        var greenSum = 0L
        var blueSum = 0L
        var pixelCount = 0L

        for (y in startY until endY) {
            for (x in startX until endX) {
                val pixel = bitmap[x, y]

                redSum += Color.red(pixel)
                greenSum += Color.green(pixel)
                blueSum += Color.blue(pixel)
                pixelCount++
            }
        }

        if (pixelCount == 0L) {
            return RgbColor(
                red = 0,
                green = 0,
                blue = 0
            )
        }

        return RgbColor(
            red = (redSum / pixelCount).toInt(),
            green = (greenSum / pixelCount).toInt(),
            blue = (blueSum / pixelCount).toInt()
        )
    }
}