package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.myapp.camera.CameraHandler
import com.example.myapp.sensors.LightSensorHandler
import com.example.myapp.sensors.AccelerometerHandler
import com.example.myapp.ui.theme.MainUI
import com.example.myapp.tensorflow.ImageProcessing
import com.example.myapp.tensorflow.TensorFlowLiteModel
import com.example.myapp.ui.theme.AuthenticationForm
import com.google.firebase.FirebaseApp
import java.nio.ByteBuffer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    private lateinit var cameraHandler: CameraHandler
    private lateinit var accelerometerHandler: AccelerometerHandler
    private val accelerometerValues = mutableStateOf(Triple(0f, 0f, 0f))
    private lateinit var lightSensorHandler: LightSensorHandler
    private val luminosityValue = mutableFloatStateOf(0f)
    private val imageProcessing = ImageProcessing()
    private val tfLiteModel = TensorFlowLiteModel(this)
    private var isCameraInitialized = false
    private lateinit var previewView: PreviewView

    private var showAuthenticationScreen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        accelerometerHandler = AccelerometerHandler(this)
        cameraHandler = CameraHandler(this)
        lightSensorHandler = LightSensorHandler(this) { lightLevel ->
            luminosityValue.floatValue = lightLevel
        }

        requestPermissions()

        previewView = PreviewView(this)

        setContent {

            androidx.compose.runtime.LaunchedEffect(isCameraInitialized) {
                if (!isCameraInitialized && allPermissionsGranted()) {
                    cameraHandler.startCamera(previewView) {
                        isCameraInitialized = true
                    }
                }
            }
            if (showAuthenticationScreen) {
                AuthenticationForm(
                    onBackClick = {
                        showAuthenticationScreen = false
                    },
                    onAuthenticateUser = { email, password ->
                        authenticateUser(email, password)
                    },
                    onCreateAccount = { email, password ->
                        createUser(email, password)
                    }
                )
            } else {
                MainUI(
                    accelerometerValues = accelerometerValues.value,
                    luminosityValue = luminosityValue.floatValue,
                    cameraPreview = {
                        AndroidView(factory = { previewView })
                    },
                    onCaptureClick = {
                        cameraHandler.captureImage { imagePath ->
                            val byteBuffer = processImageForInference(imagePath)
                            val outputArray = runInference(byteBuffer)
                            if (outputArray != null) {
                                handleResult(outputArray)
                            }
                        }
                    },
                    onAuthenticateClick = {
                        showAuthenticationScreen = true
                    }
                )
            }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showAuthenticationScreen = false
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUser(email: String, password: String) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    showAuthenticationScreen = false
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "Account creation failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePredictionData(maxIndex: Int) {
        val userEmail = Firebase.auth.currentUser?.email
        val timestamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(Date())

        if (userEmail != null) {
            val data = hashMapOf(
                "email" to userEmail,
                "predictedClassIndex" to maxIndex,
                "timestamp" to timestamp
            )

            Firebase.firestore.collection("predictions")
                .add(data)
        } else {
            Toast.makeText(this, "User not authenticated, data not saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageForInference(imagePath: String): ByteBuffer {
        return imageProcessing.processImage(imagePath, 128)
    }

    private fun runInference(byteBuffer: ByteBuffer): FloatArray? {
        return if (tfLiteModel.loadModel("mobilenet_v2.tflite")) {
            val outputData = ByteBuffer.allocateDirect(4 * 1001)
            tfLiteModel.runInference(byteBuffer, outputData)
            byteBuffer.rewind()
            outputData.rewind()
            val outputArray = FloatArray(1001)
            outputData.asFloatBuffer().get(outputArray)
            outputArray
        } else {
            Toast.makeText(this, "Failed to load model", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun handleResult(outputArray: FloatArray) {
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
        val maxConfidence = outputArray.getOrNull(maxIndex) ?: -1f
        println("Predicted class index: $maxIndex, Confidence: $maxConfidence")

        fetchEntityFromFirestore(maxIndex) { entityName ->
            if (entityName != null) {
                Toast.makeText(this, "Entity detected: $entityName", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this,"No entity found",Toast.LENGTH_SHORT).show()
            }
        }

        savePredictionData(maxIndex)
    }

    private fun fetchEntityFromFirestore(maxIndex: Int, callback: (String?) -> Unit) {
        Firebase.firestore.collection("entity")
            .whereEqualTo("id", maxIndex)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val entityName = document.getString("entity")
                    callback(entityName)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching entity: ${e.message}")
                callback(null)
            }
    }





    override fun onResume() {
        super.onResume()
        accelerometerHandler.startListening { values ->
            accelerometerValues.value = values
        }
        lightSensorHandler.startListening()
    }

    override fun onPause() {
        super.onPause()
        accelerometerHandler.stopListening()
        lightSensorHandler.stopListening()
        cameraHandler.stopCamera()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
    }
}
