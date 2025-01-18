    package com.example.myapp.sensors

    import android.content.Context
    import android.hardware.Sensor
    import android.hardware.SensorEvent
    import android.hardware.SensorEventListener
    import android.hardware.SensorManager

    class AccelerometerHandler(context: Context) : SensorEventListener {

        private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        private var onAccelerometerChangedCallback: ((Triple<Float, Float, Float>) -> Unit)? = null

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]
                onAccelerometerChangedCallback?.invoke(Triple(x, y, z))
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        fun startListening(onSensorChanged: (Triple<Float, Float, Float>) -> Unit) {
            onAccelerometerChangedCallback = onSensorChanged
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        fun stopListening() {
            sensorManager.unregisterListener(this)
        }
    }
