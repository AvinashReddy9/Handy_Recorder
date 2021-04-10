package com.example.viewrecorder.Util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;

import java.util.Set;


public class Bluetooth_Util {
    BluetoothAdapter adapter;
    private Activity context;
    private static final String TAG = "BLUETOOTH_UTILS";

    public Bluetooth_Util(Activity context) {
        this.context = context;
    }

    public boolean isBluetoothAdapterAvailable(BluetoothAdapter bAdapter) {
        if (bAdapter == null) {
            // Device won't support Bluetooth
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled(BluetoothAdapter bAdapter) {
        if (!bAdapter.isEnabled()) {
            return false;
        }
       return true;
    }

    public void enableBluetooth(BluetoothAdapter bAdapter) {
        Intent eintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivityForResult(eintent, 1);
    }
    public Set<BluetoothDevice> getdeviceList(BluetoothAdapter bAdapter){
        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.e(TAG, "DEVICE NAME " + deviceName);
                Log.e(TAG," DEVICE HARDWARE ADDRESS "+ deviceHardwareAddress);
            }
        }
        return pairedDevices;
    }
}
