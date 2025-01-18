package com.example.myapp.tensorflow

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageProcessing {
    fun processImage(imagePath: String, inputSize: Int): ByteBuffer {
        val imgFile = File(imagePath)
        if (!imgFile.exists()) {
            throw IllegalArgumentException("Image file does not exist at $imagePath")
        }
        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            ?: throw IllegalArgumentException("Failed to decode image at $imagePath")

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val buffer = ByteBuffer.allocateDirect(4 * resizedBitmap.width * resizedBitmap.height * 3)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            buffer.putFloat((value shr 16 and 0xFF) / 255.0f)
            buffer.putFloat((value shr 8 and 0xFF) / 255.0f)
            buffer.putFloat((value and 0xFF) / 255.0f)
        }

        println("Number of pixels: ${buffer.capacity() / (4 * 3)}")
        return buffer
    }
}
