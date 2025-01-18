package com.example.myapp.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHandler(private val context: Context) {

    private lateinit var imageCapture: ImageCapture
    private lateinit var preview: Preview
    private lateinit var cameraProvider: ProcessCameraProvider
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun startCamera(previewView: PreviewView, onCameraReady: () -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                onCameraReady()
            } catch (e: Exception) {
                Log.e("CameraHandler", "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun captureImage(onImageCaptured: (String) -> Unit) {
        if (!this::imageCapture.isInitialized) {
            Log.e("CameraHandler", "ImageCapture is not initialized.")
            return
        }

        val outputDirectory = File(
            context.getExternalFilesDir(null), "Pictures/myAppImages"
        )
        if (!outputDirectory.exists()) outputDirectory.mkdirs()

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss", Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("CameraX", "Photo capture succeeded: $savedUri")
                    onImageCaptured(photoFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    fun stopCamera() {
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
    }
}
