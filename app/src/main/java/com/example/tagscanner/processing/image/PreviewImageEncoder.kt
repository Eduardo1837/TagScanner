package com.example.tagscanner.processing.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.UiContext
import androidx.camera.video.Quality
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class PreviewImageEncoder {

    fun uriToPreviewJpegBytes(
        context: Context,
        uri: Uri,
        maxSize: Int = 1024,
        quality: Int = 82
    ) : ByteArray {
        val bytes = context.contentResolver.openInputStream(uri)
            ?.use { input -> input.readBytes()}
            ?: error("Could not read selected image")

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: error("Could not decode selected image")

        return bitmapToPreviewJpegBytes(bitmap, maxSize, quality)
    }

    fun bitmapToPreviewJpegBytes(
        bitmap: Bitmap,
        maxSize: Int = 1024,
        quality: Int = 82
    ) : ByteArray {
        val scaled = bitmap.scaleDown(maxSize)

        return ByteArrayOutputStream().use {output ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
            output.toByteArray()
        }
    }

    private fun Bitmap.scaleDown(maxSize: Int) : Bitmap {
        val largestSide = maxOf(width, height)
        if (largestSide <= maxSize) return this

        val ratio = maxSize.toFloat() / largestSide.toFloat()
        val targetWidth = (width * ratio).roundToInt()
        val targetHeight = (height * ratio).roundToInt()

        return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
    }
}