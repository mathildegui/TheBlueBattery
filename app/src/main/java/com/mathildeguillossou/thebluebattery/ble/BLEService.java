package com.mathildeguillossou.thebluebattery.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.UUID;

import javax.security.auth.callback.Callback;

/**
 * @author mathildeguillossou on 02/11/2017
 */

public class BLEService extends Service {

    public static final String TAG = BLEService.class.getSimpleName();
    public static final String CONNECTION_STATE_CHANGE = "CONNECTION_STATE_CHANGE";
    public static final String BATTERY = "BATTERY";
    public static final String EXTRA_PARAM_STATUS = "EXTRA_PARAM_STATUS";
    public static final String EXTRA_PARAM_BATTERY = "EXTRA_PARAM_BATTERY";
    public static final String EXTRA_PARAM_BATTERY_POSITION = "EXTRA_PARAM_BATTERY_POSITION";

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
    private UUID Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
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
    private Integer position;
    private BluetoothGatt gatt;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("startService", "startService");


        address = intent.getStringExtra("address");
        position = intent.getIntExtra("position", -1);
        if(initialize() && !address.isEmpty())
            connect(address);

        return super.onStartCommand(intent, flags, startId);
    }

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

    public void broadcastActionBaz(int status) {
        Intent intent = new Intent(CONNECTION_STATE_CHANGE);
        intent.putExtra(EXTRA_PARAM_STATUS, status);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(intent);
    }

    public void broadcastActionBattery(int batterylevel) {
        Intent intent = new Intent(BATTERY);
        intent.putExtra(EXTRA_PARAM_BATTERY, batterylevel);
        intent.putExtra(EXTRA_PARAM_BATTERY_POSITION, position);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(intent);
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

            broadcastActionBaz(status);


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
            try {
                int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.d("onCharacteristicRead", "value: " + value);
                broadcastActionBattery(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
