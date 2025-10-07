package com.blisscakes.app.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BatteryMonitor(private val context: Context) {

    fun getBatteryStatus(): BatteryStatus {
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val chargingMethod = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Adapter"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Not Charging"
        }

        val temperature = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val temperatureCelsius = temperature / 10.0f

        return BatteryStatus(
            percentage = batteryPct.toInt(),
            isCharging = isCharging,
            chargingMethod = chargingMethod,
            temperature = temperatureCelsius
        )
    }

    fun observeBatteryStatus(): Flow<BatteryStatus> = callbackFlow {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(getBatteryStatus())
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Send initial value
        trySend(getBatteryStatus())

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
}

data class BatteryStatus(
    val percentage: Int,
    val isCharging: Boolean,
    val chargingMethod: String,
    val temperature: Float
)