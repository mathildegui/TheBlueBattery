package com.mathildeguillossou.thebluebattery;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * @author mathildeguillossou on 02/11/2017
 */

public class BLEService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();





    }


    private UUID Battery_Service_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private UUID Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
;
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler;

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        mHandler = new Handler(getMainLooper());
        Log.i(TAG, "initialize is already called :  " + mBluetoothManager);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    private String address;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //connect("00:11:67:2C:E8:F7");
        //connect("F4:F5:D8:67:61:3F");
        //connect("80:E4:DA:72:08:C4");
        //connect("04:34:12:39:03:81");
        Log.d("startService", "startService");


        address = intent.getStringExtra("address");
        if(initialize() && !address.isEmpty())
            connect(address);

        return super.onStartCommand(intent, flags, startId);
    }

    BluetoothGatt gatt;

    public void connect(final String address) {
        /** FIXME
         * 1. The device has Public address
         2. The device with given address has been scanned at least once before the connectGatt method is called.
         */

        gatt = mBluetoothAdapter.getRemoteDevice(address)
                .connectGatt(BLEService.this, false, callback);
        /*for(BluetoothDevice de : mBluetoothAdapter.getBondedDevices()) {
            Log.d("de", de.toString() + " - Name: " + de.getName());
            if(de.getAddress().equals(address)) de.connectGatt(BLEService.this, false, callback);
        }*/
    }

    void getBAttery(BluetoothGatt gatt) {


        Log.d("GATT", gatt.getServices() != null ? gatt.getServices().size() + "" : "0");

        BluetoothGattService batteryService = gatt.getService(Battery_Service_UUID);
        if (batteryService == null) {
            Log.d(TAG, "Battery service not found!");
            return;
        }

        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(Battery_Level_UUID);
        if (batteryLevel == null) {
            Log.d(TAG, "Battery level not found!");
            return;
        }
        gatt.readCharacteristic(batteryLevel);
    }

    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //BluetoothProfile.

            Log.d("onConnectionStateChange", status == BluetoothGatt.GATT_SUCCESS ? "GATT_SUCCESS" : "GATT_FAIL");
            Log.d("onConnectionStateChange", newState == BluetoothProfile.STATE_CONNECTED ? "STATE_CONNECTED" : "STATE_DISCONNECTED");
            Log.d("onConnectionStateChange", "status: " + status + " - newState: " + newState);
            //if(newState == 0) connect("00:11:67:2C:E8:F7");




            if(status == BluetoothGatt.GATT_SUCCESS && BluetoothProfile.STATE_CONNECTED == newState) gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d("onServicesDiscovered", "status: " + status);
            getBAttery(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("onCharacteristicRead", "status: " + status);
            Log.d("onCharacteristicRead", "value: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("onCharacteristicWrite", "status: " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d("onCharacteristicChanged", "characteristic: " + characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d("onReadRemoteRssi", "status: " + status);
        }
    };
}
