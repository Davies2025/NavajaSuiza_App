package com.example.navajasuiza.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class SensorController(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)


    private val accelerometerChannel = Channel<FloatArray>(Channel.UNLIMITED)
    private val magnetometerChannel = Channel<FloatArray>(Channel.UNLIMITED)
    private val lightSensorChannel = Channel<Float>(Channel.UNLIMITED)
    private val ambientTemperatureChannel = Channel<Float>(Channel.UNLIMITED)

    private val pressureChannel = Channel<Float>(Channel.UNLIMITED)



    val accelerometerFlow: Flow<FloatArray> = accelerometerChannel.receiveAsFlow()
    val magnetometerFlow: Flow<FloatArray> = magnetometerChannel.receiveAsFlow()
    val lightSensorFlow: Flow<Float> = lightSensorChannel.receiveAsFlow()
    val ambientTemperatureFlow: Flow<Float> = ambientTemperatureChannel.receiveAsFlow()

    val pressureFlow: Flow<Float> = pressureChannel.receiveAsFlow()


    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_UI)

        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListening() {

        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario, pero es una practica
    }


    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerChannel.trySend(event.values.clone())
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerChannel.trySend(event.values.clone())
            Sensor.TYPE_LIGHT -> lightSensorChannel.trySend(event.values[0])
            Sensor.TYPE_AMBIENT_TEMPERATURE -> ambientTemperatureChannel.trySend(event.values[0])

            Sensor.TYPE_PRESSURE -> pressureChannel.trySend(event.values[0])
        }
    }
}







