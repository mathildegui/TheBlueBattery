package com.mathildeguillossou.thebluebattery.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.util.*

/**
 * @author mathildeguillossou on 07/03/2018
 */
class BleGattCallback(var context: Context, var position: Int) : BluetoothGattCallback() {

    private val TAG = BleGattCallback::class.simpleName

    private val Battery_Service_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    private val Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")

    companion object {
        val ACTION_BATTERY = "com.mathildeguillossou.battery"
        val EXTRA_PARAM_BATTERY = "EXTRA_PARAM_BATTERY"
        val EXTRA_PARAM_POSITION = "EXTRA_PARAM_POSITION"
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                         newState: Int) {
        Log.d(TAG, "onConnectionStateChange $gatt and status $status and newstate $newState")

        when (newState) {
            BluetoothGatt.STATE_CONNECTED -> {
                Handler(Looper.getMainLooper()).post {
                    gatt.discoverServices()
                }
                Log.d(TAG, "onConnectionStateChange STATE_CONNECTED")
            }
            BluetoothGatt.STATE_DISCONNECTED -> {
                Log.d(TAG, "onConnectionStateChange STATE_DISCONNECTED")
            }
            BluetoothGatt.STATE_CONNECTING -> {
                Log.d(TAG, "onConnectionStateChange STATE_CONNECTING")
            }
            BluetoothGatt.STATE_DISCONNECTING -> {
                Log.d(TAG, "onConnectionStateChange STATE_DISCONNECTING")
            }
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        Log.d("onCharacteristicRead", "status: " + status)
        try {
            val value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)!!
            Log.d("onCharacteristicRead", "value battery: $value")
            //FIXME - Update the battery - Send a broadcast
             broadcastActionBattery(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.d("onServicesDiscovered", "status: " + status)

        for(service in gatt.services) {
            Log.d("SERVICE:", service.uuid.toString())
        }

        val batteryService = gatt.getService(Battery_Service_UUID)
        if (batteryService == null) {
            Log.d("TAG", "Battery service not found!")
            return
        }

        val batteryLevel = batteryService.getCharacteristic(Battery_Level_UUID)
        if (batteryLevel == null) {
            Log.d("TAG", "Battery level not found!")
            return
        }
        gatt.readCharacteristic(batteryLevel)
    }

    fun broadcastActionBattery(value : Int) {
        val intent = Intent(ACTION_BATTERY)
        intent.putExtra(EXTRA_PARAM_BATTERY, value)
        intent.putExtra(EXTRA_PARAM_POSITION, position)
        LocalBroadcastManager.getInstance(context.applicationContext).sendBroadcast(intent)
    }
}