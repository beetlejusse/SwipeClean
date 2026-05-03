package com.app.swipeclean.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.app.swipeclean.data.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri

class BlurDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Threshold: variance below this = blurry
    // Tune by testing on your own blurry photos — 100 is a reasonable start
    private val BLUR_THRESHOLD = 100.0
    // Returns list of photos that are likely blurry
    // Runs on Default dispatcher — CPU-bound computation
    suspend fun flagBlurry(photos: List<Photo>): List<Photo> = withContext(Dispatchers.Default) {
        photos.filter { isBlurry(it.uri.toUri()) }
    }

    private fun isBlurry(uri: Uri): Boolean {
        val bitmap = loadScaledBitmap(uri) ?: return false
        val variance = laplacianVariance(bitmap)
        bitmap.recycle()
        return variance < BLUR_THRESHOLD
    }

    // Load a small (256px) version for speed — full-res not needed for blur detection
    private fun loadScaledBitmap(uri: Uri): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply { inSampleSize = 4 }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
        } catch (e: Exception) { null }
    }

    // Laplacian variance: sharp images have high variance, blurry ones have low
    private fun laplacianVariance(bitmap: Bitmap): Double {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        //Convert to grayscale and apply 3x3 Laplacian Kernel
        var sum = 0.0
        var sumSq = 0.0
        var count = 0
        for(y in 1 until h - 1) {
            for (x in 1 until w - 1) {
                val lap = -4 * gray(pixels[y*w+x]) + gray(pixels[(y-1)*w+x]) + gray(pixels[(y+1)*w+x]) +
                        gray(pixels[y*w+x-1]) + gray(pixels[y*w+x+1])

                sum += lap.toDouble()
                sumSq += lap.toDouble() * lap
                count++
            }
        }
        val mean = sum / count
        return (sumSq / count) - (mean * mean)
    }

    // Extract luminance (grayscale) from ARGB pixel
    private fun gray(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }
}