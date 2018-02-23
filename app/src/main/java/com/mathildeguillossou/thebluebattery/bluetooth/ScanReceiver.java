package com.mathildeguillossou.thebluebattery.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScanReceiver extends BroadcastReceiver {

    public interface ScanRequestListener {
        void onScanFinish();
        void onDeviceFound(BluetoothDevice device);
    }

    private final Context context;
    private final BluetoothAdapter adapter;
    private final ScanRequestListener listener;

    public ScanReceiver(Context context, BluetoothAdapter adapter, ScanRequestListener listener) {
        this.context = context;
        this.adapter = adapter;
        this.listener = listener;

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.context.registerReceiver(this, filter);
        this.adapter.startDiscovery();
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d("DEVICE: ", "device bluetooth " + device);
            this.listener.onDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            //FIXME
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //FIXME
            this.listener.onScanFinish();
            this.stop();
        }
    }

    public void stop() {
        try {
            this.adapter.cancelDiscovery();
            this.context.unregisterReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
