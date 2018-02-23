package com.mathildeguillossou.thebluebattery.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.reflect.Method;

public class BindRequest extends BroadcastReceiver {

    public interface BindRequestListener {
        void onBindingSuccess();
        void onBindingFailed();
    }

    private final Context context;
    private final BluetoothDevice device;
    private final BindRequestListener listener;

    public BindRequest(Context context, BluetoothDevice device, BindRequestListener listener) {
        this.context = context;
        this.listener = listener;
        this.device = device;

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            this.context.registerReceiver(this, filter);
            this.device.createBond();
        } else {
            listener.onBindingSuccess();
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                this.listener.onBindingSuccess();
                this.context.unregisterReceiver(this);
            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                this.listener.onBindingFailed();
                this.context.unregisterReceiver(this);
            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING) {
                this.listener.onBindingFailed();
                this.context.unregisterReceiver(this);
            } else if (state == BluetoothDevice.BOND_BONDING && prevState == BluetoothDevice.BOND_NONE) {
                //fIXME
            }
        }
    }

    public void stop() {
        try {
            this.context.unregisterReceiver(this);
            Method m = this.device.getClass().getMethod("cancelBondProcess", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
