package com.mathildeguillossou.thebluebattery;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * @author mathilde
 * @version 29/03/2018
 */

public class BleBatteryLevelHelper {

    private static final String TAG = "BTLeBatteryLevelClient";

    public static final UUID BATTERY_SERVICE_UUID =
            UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_LEVEL_CHARACTER_UUID =
            UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_PRESENTATION_FORMAT_DESCRIPTOR_UUID =
            UUID.fromString("00002904-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID REPORT_REFERENCE_DESCRIPTOR_UUID =
            UUID.fromString("00002908-0000-1000-8000-00805f9b34fb");

    public class BatteryLevelData {
        private int mBatteryLevel;

        public int getBatteryLevel() {
            return mBatteryLevel;
        }

        public void setBatteryLevel(int batteryLevel) {
            mBatteryLevel = batteryLevel;
        }

        private int mNamespace;

        public int getNamespace() {
            return mNamespace;
        }

        public void setNamespace(int namespace) {
            mNamespace = namespace;
        }

        private int mDescription;

        public int getDescription() {
            return mDescription;
        }

        public void setDescription(int description) {
            mDescription = description;
        }
    }

    /**
     * Enable or disable notification for battery level measurement.
     *
     * <p>Once notification is enabled, a
     * {@link BluetoothGattCallback#onCharacteristicChanged} callback will be
     * triggered if the ble battery service indicates that the device battery
     * level has changed. Then use {@link BleBatteryLevelHelper#readBatteryLevel
     * (BluetoothGatt,BluetoothGattCharacteristic)} to read the battery data.
     *
     * @param gatt Bluetooth GATT.
     * @param enable Set to true to enable notification.
     * @return true, if the notification status was set successfully.
     */
    public boolean setNotification(BluetoothGatt gatt, boolean enable) {
        if (null == gatt) {
            return false;
        }

        BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE_UUID);
        if (null == batteryService) {
            return false;
        }

        BluetoothGattCharacteristic batteryLevelCharacter =
                batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTER_UUID);
        if (null == batteryLevelCharacter) {
            return false;
        }

        if (!gatt.setCharacteristicNotification(batteryLevelCharacter, enable)) {
            return false;
        }

        BluetoothGattDescriptor cccDescriptor =
                batteryLevelCharacter.getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID);
        if (null == cccDescriptor)
            return false;

        if (enable) {
            cccDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            cccDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }

        return gatt.writeDescriptor(cccDescriptor);
    }

    /**
     * Read battery data directly.
     *
     * @param gatt Bluetooth GATT.
     * @return battery level data.
     */
    public BatteryLevelData readBatteryLevel(BluetoothGatt gatt) {

        BluetoothGattService batteryService = gatt.getService(BATTERY_SERVICE_UUID);
        if (null == batteryService) {
            return null;
        }

        BluetoothGattCharacteristic battLevelCharacter =
                batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTER_UUID);
        if (null == battLevelCharacter) {
            return null;
        }

        return parseBatteryLevelData(gatt, battLevelCharacter);
    }

    /**
     * Read battery data with in {@link BluetoothGattCallback#onCharacteristicChanged}
     * callback
     *
     * @param gatt Bluetooth GATT.
     * @param characteristic Set to true to enable notification.
     * @return battery level data.
     */
    public BatteryLevelData readBatteryLevel(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic) {

        if(characteristic.getUuid().equals(BATTERY_LEVEL_CHARACTER_UUID)) {
            return parseBatteryLevelData(gatt, characteristic);
        }

        return null;
    }

    private BatteryLevelData parseBatteryLevelData(BluetoothGatt gatt,
                                                   BluetoothGattCharacteristic battLevelCharacter) {

        if (null == gatt) {
            return null;
        }

        if (null == battLevelCharacter) {
            return null;
        }

        if (gatt.readCharacteristic(battLevelCharacter)) {
            BatteryLevelData levelData = new BatteryLevelData();

            levelData.setBatteryLevel(battLevelCharacter.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 0));

            BluetoothGattDescriptor cpfDescriptor =
                    battLevelCharacter.getDescriptor(CHARACTERISTIC_PRESENTATION_FORMAT_DESCRIPTOR_UUID);
            if (cpfDescriptor != null && gatt.readDescriptor(cpfDescriptor)) {
                byte[] cpfVal = cpfDescriptor.getValue();
                if (null != cpfVal && cpfVal.length == 7) {
                    levelData.setNamespace(cpfVal[4] & 0xFF);
                    levelData.setDescription(((cpfVal[5] & 0xFF) + (cpfVal[6] & 0xFF) << 8));
                }
            }
            return levelData;
        } else {
            return null;
        }
    }
}
