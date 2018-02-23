package com.mathildeguillossou.thebluebattery.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * @author mathilde
 * @version 23/02/2018
 */

public class BluetoothService {

    private final Context context;

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
}
