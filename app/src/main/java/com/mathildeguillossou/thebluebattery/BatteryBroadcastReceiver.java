package com.mathildeguillossou.thebluebattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author mathilde
 * @version 29/03/2018
 */

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //catch android.bluetooth.device.extra.BATTERY_LEVEL
        //catch android.bluetooth.device.extra.EXTRA_DEVICE

        Log.d(BatteryBroadcastReceiver.class.getSimpleName(), "battery");
    }
}
