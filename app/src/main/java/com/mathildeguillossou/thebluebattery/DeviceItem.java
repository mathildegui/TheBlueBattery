package com.mathildeguillossou.thebluebattery;

/**
 * @author mathildeguillossou on 01/11/2017
 */

public class DeviceItem {

    public String deviceName;
    public String address;
    public boolean connected;

    public void deviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceItem(String name, String address, boolean connected) {
        this.deviceName = name;
        this.address = address;
        this.connected = connected;
    }
}
