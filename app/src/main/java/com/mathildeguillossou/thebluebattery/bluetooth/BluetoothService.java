package com.mathildeguillossou.thebluebattery.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

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
    boolean alreadyCalled = false;


    public void connect(final String address) {
//        bindDevice()

//            if(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address).createBond()) {
                Log.d(TAG, "connecting to ... " + address);
                gatt = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address).connectGatt(this.context, true, new BleGattCallback());
//            }
        alreadyCalled = true;
//        if(gatt != null) getBattery();
    }

    /*public void getBattery() {
        Log.d("GATT", gatt.getServices() != null ? gatt.getServices().size() + "" : "0");

        BluetoothGattService batteryService = this.gatt.getService(Battery_Service_UUID);
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
    }*/
}
