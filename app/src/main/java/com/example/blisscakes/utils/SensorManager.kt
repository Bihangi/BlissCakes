package com.blisscakes.app.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager as AndroidSensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// SENSOR MANAGER

class DeviceSensorManager(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as AndroidSensorManager

    // Accelerometer
    fun observeAccelerometer(): Flow<AccelerometerData> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val data = AccelerometerData(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                    timestamp = System.currentTimeMillis()
                )
                trySend(data)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Gyroscope
    fun observeGyroscope(): Flow<GyroscopeData> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val data = GyroscopeData(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                    timestamp = System.currentTimeMillis()
                )
                trySend(data)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Light Sensor
    fun observeLightSensor(): Flow<Float> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values[0])
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Proximity Sensor
    fun observeProximitySensor(): Flow<Float> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values[0])
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                AndroidSensorManager.SENSOR_DELAY_NORMAL
            )
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    fun hasSensor(sensorType: Int): Boolean {
        return sensorManager.getDefaultSensor(sensorType) != null
    }

    fun getAvailableSensors(): List<String> {
        val sensors = mutableListOf<String>()

        if (hasSensor(Sensor.TYPE_ACCELEROMETER)) sensors.add("Accelerometer")
        if (hasSensor(Sensor.TYPE_GYROSCOPE)) sensors.add("Gyroscope")
        if (hasSensor(Sensor.TYPE_LIGHT)) sensors.add("Light Sensor")
        if (hasSensor(Sensor.TYPE_PROXIMITY)) sensors.add("Proximity Sensor")
        if (hasSensor(Sensor.TYPE_MAGNETIC_FIELD)) sensors.add("Magnetometer")
        if (hasSensor(Sensor.TYPE_PRESSURE)) sensors.add("Barometer")

        return sensors
    }
}

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class GyroscopeData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)