package com.mathildeguillossou.thebluebattery.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mathildeguillossou.thebluebattery.ble.BLEService;

import java.util.UUID;

/**
 * @author mathilde
 * @version 23/02/2018
 */

public class BluetoothService {
    public static final String TAG = BluetoothService.class.getSimpleName();

    private UUID Battery_Service_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private UUID Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    private final Context context;

    private String address;
    private BluetoothGatt gatt;

    public BluetoothService(Context context) {
        this.context = context;
    }

    private BluetoothAdapter bt() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new Exception();
        } else if (!adapter.isEnabled() && !adapter.enable()) {
            throw new Exception();
        }
        return adapter;
    }

    public ScanReceiver devices(ScanReceiver.ScanRequestListener listener) throws Exception {
        return new ScanReceiver(this.context, bt(), listener);
    }

    public BindRequest bindDevice(String mac, BindRequest.BindRequestListener listener) throws Exception {
        BluetoothDevice device = bt().getRemoteDevice(mac);
        if (device != null) {
            return new BindRequest(this.context, device, listener);
        } else {
            listener.onBindingFailed();
            return null;
        }
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("startService", "startService");


        address = intent.getStringExtra("address");
        if(initialize() && !address.isEmpty())
            connect(address);

        return super.onStartCommand(intent, flags, startId);
    }

    public void connect(final String address) {
        *//** FIXME
         * 1. The device has Public address
         2. The device with given address has been scanned at least once before the connectGatt method is called.
         *//*

        gatt = mBluetoothAdapter.getRemoteDevice(address)
                .connectGatt(BLEService.this, false, callback);
        *//*for(BluetoothDevice de : mBluetoothAdapter.getBondedDevices()) {
            Log.d("de", de.toString() + " - Name: " + de.getName());
            if(de.getAddress().equals(address)) de.connectGatt(BLEService.this, false, callback);
        }*//*
    }*/

    private void getBattery(BluetoothGatt gatt) {
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
}
