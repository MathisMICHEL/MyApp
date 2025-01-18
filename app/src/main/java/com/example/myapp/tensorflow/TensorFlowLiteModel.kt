package com.example.myapp.tensorflow

import android.content.Context
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class TensorFlowLiteModel(private val context: Context) {

    private var interpreter: Interpreter? = null
    fun loadModel(modelPath: String): Boolean {
        return try {
            val fileDescriptor = context.assets.openFd(modelPath)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            interpreter = Interpreter(modelBuffer)

            true
        } catch (e: IOException) {
            Toast.makeText(context, "Error loading model: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun runInference(inputData: ByteBuffer, outputData: ByteBuffer) {
        interpreter?.run(inputData, outputData)
    }

}

