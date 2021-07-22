package edu.txstate.reu.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.UUID;

public class BleProfile {

    public static UUID SERVER_SERVICE = UUID.fromString("0000fe81-0000-1000-8000-00805f9b34fb");
    public static UUID SERVER_OUT = UUID.fromString("0000fe82-0000-1000-8000-00805f9b34fb");
    public static UUID SERVER_IN  = UUID.fromString("0000fe83-0000-1000-8000-00805f9b34fb");


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothGattService createService() {
        BluetoothGattService service = new BluetoothGattService(SERVER_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic inputCharacteristic = new BluetoothGattCharacteristic(SERVER_IN,
                //Read-only characteristic
                BluetoothGattCharacteristic.PROPERTY_WRITE ,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattCharacteristic outputCharacteristic = new BluetoothGattCharacteristic(SERVER_OUT,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ );

        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(SERVER_OUT,
                BluetoothGattDescriptor.PERMISSION_READ| BluetoothGattDescriptor.PERMISSION_WRITE);
        outputCharacteristic.addDescriptor(descriptor);

        service.addCharacteristic(inputCharacteristic);
        service.addCharacteristic(outputCharacteristic);

        return service;
    }
}
