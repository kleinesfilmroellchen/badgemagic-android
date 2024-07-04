package org.fossasia.badgemagic.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.core.graphics.scale
import androidx.core.graphics.set
import org.fossasia.badgemagic.core.android.log.Timber
import org.fossasia.badgemagic.ui.custom.badgeHeight

object ImageUtils {
    fun trim(source: Bitmap, toDimen: Int): Bitmap {
        var firstX = 0
        var firstY = 0
        var lastX = source.width
        var lastY = source.height
        val pixels = IntArray(source.width * source.height)
        source.getPixels(pixels, 0, source.width, 0, 0, source.width, source.height)
        loop@ for (x in 0 until source.width) {
            for (y in 0 until source.height) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    firstX = when {
                        x > 1 -> x - 1
                        else -> x
                    }
                    break@loop
                }
            }
        }
        loop@ for (y in 0 until source.height) {
            for (x in firstX until source.width) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    firstY = when {
                        y > 1 -> y - 1
                        else -> y
                    }
                    break@loop
                }
            }
        }
        loop@ for (x in source.width - 1 downTo firstX) {
            for (y in source.height - 1 downTo firstY) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    lastX = when {
                        x < source.width - 2 -> x + 2
                        else -> x + 1
                    }
                    break@loop
                }
            }
        }
        loop@ for (y in source.height - 1 downTo firstY) {
            for (x in source.width - 1 downTo firstX) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    lastY = when {
                        y < source.height - 2 -> y + 2
                        else -> y + 1
                    }
                    break@loop
                }
            }
        }

        val trimmedBitmap = Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY)
        return scaleBitmap(trimmedBitmap, toDimen)
    }

    fun scaleBitmap(trimmedBitmap: Bitmap, toDimen: Int): Bitmap {
        val outWidth: Int
        val outHeight: Int
        val inWidth = trimmedBitmap.width
        val inHeight = trimmedBitmap.height
        if (inWidth > inHeight) {
            outWidth = toDimen
            outHeight = inHeight * toDimen / inWidth
        } else {
            outHeight = toDimen
            outWidth = inWidth * toDimen / inHeight
        }
        return Bitmap.createScaledBitmap(trimmedBitmap, outWidth, outHeight, false)
    }

    fun convertToInternalFormat(originalBitmap: Bitmap): Bitmap {
        val aspectRatio = originalBitmap.width.toDouble() / originalBitmap.height.toDouble()
        // Rescale to fit height (image can scoll)
        var scaledBitmap = if (originalBitmap.height > badgeHeight) {
            val newWidth = aspectRatio * badgeHeight
            Timber.tag(javaClass.simpleName)
                .i("Resizing incoming image from ${originalBitmap.width}x${originalBitmap.height} to ${newWidth.toInt()}x$badgeHeight (old aspect: $aspectRatio)")
            originalBitmap.scale(width = newWidth.toInt(), height = badgeHeight, true)
        } else {
            originalBitmap
        }
        scaledBitmap = scaledBitmap.copy(Config.ARGB_8888, true)

        // Convert normal image into two-tone format used internally
        for (x in 0 until scaledBitmap.width) {
            for (y in 0 until scaledBitmap.height) {
                val originalValue = scaledBitmap[x, y]
                scaledBitmap[x, y] =
                    if ((Color.luminance(originalValue) < 0.5) or (Color.alpha(originalValue) < 128)) {
                        Color.TRANSPARENT
                    } else {
                        Color.BLACK
                    }
            }
        }
        return scaledBitmap
    }

    fun vectorToBitmap(drawable: VectorDrawable): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(220, 55, Bitmap.Config.ARGB_8888)
        return try {
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            Timber.e {
                "Error Converting Bitmap: $e"
            }
            bitmap
        }
    }

    fun convertToBitmap(drawable: Drawable?): Bitmap {
        return drawable?.toBitmap() ?: Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }
}
