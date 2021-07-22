package edu.txstate.reu.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * BleClient
 *  - Ble GattClient used to receive events from connect GattServer
 *
 **/
public class BleClient {

    private final String TAG = "BleClient";
    private Context mContext;
    private BluetoothAdapter bleAdapter;
    private BluetoothGatt bluetoothGatt;
    private OnEventReceivedListener listener = null;


    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.d(TAG, "onConnectionStateChange: Connecting to GATT server.");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "onConnectionStateChange: Connected to GATT server.");
                    /*check if the service is available on the device*/

                    bluetoothGatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.d(TAG, "onConnectionStateChange: Disconnecting from GATT server");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d(TAG, "onConnectionStateChange: Disconnected from GATT server.");
                    gatt.close();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            for(BluetoothGattService service:services) {
                if(service.getUuid().equals(BleProfile.SERVER_SERVICE)){
                    gatt.readCharacteristic(service.getCharacteristic(BleProfile.SERVER_OUT));
                }
                Log.d(TAG, "onServicesDiscovered: " + service.getUuid());

            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            gatt.setCharacteristicNotification(characteristic,true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BleProfile.SERVER_OUT);
            gatt.readDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Event event = new Event(characteristic.getValue(), new Timestamp(System.currentTimeMillis()));
            dispatchEvent(event);
        }


    };

    public void send(byte[] value) {
        try {
            BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(BleProfile.SERVER_SERVICE).getCharacteristic(BleProfile.SERVER_IN);
            characteristic.setValue(value);
            bluetoothGatt.writeCharacteristic(characteristic);
        }
        catch (Exception e) {

        }
    }

    public BleClient(Context context) {

        mContext = context;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "NO BLE SUPPORT" );

        }
        bleAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();

        for(BluetoothDevice device: pairedDevices) {
            bluetoothGatt = device.connectGatt(context,false,bluetoothGattCallback,2);
        }
    }

    /**
     * Event Functionality
     */
    public interface OnEventReceivedListener {
        void onEventReceived(Event event);
    }

    public void dispatchEvent(Event event) {
        if(this.listener != null) {
            this.listener.onEventReceived(event);
        }
    }

    public void addEventListener(BleClient.OnEventReceivedListener listener) {
        this.listener = listener;
    }

    public void removeAllListeners() {
        this.listener = null;
    }


}
