package com.example.bluetoothclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.txstate.reu.ble.BleClient;
import edu.txstate.reu.ble.BluetoothLe;
import edu.txstate.reu.ble.Event;

public class MainActivity extends AppCompatActivity implements BleClient.OnEventReceivedListener{

    private TextView mTextView;
    private final String TAG = "BluetoothLe Example";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothLe.getBleClient(getApplicationContext()).addEventListener(this);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float [] values = new float[1];
                values[0] = 1.f;
                BluetoothLe.getBleClient(getApplicationContext()).send(FloatArray2ByteArray(values));
            }
        });
    }



    @Override
    public void onEventReceived(Event event) {
        float [] data = event.getData();
        Timestamp timestamp = event.getTimestamp();
        Log.d(TAG, "onEventReceived: x: " + data[0] + " y: " + data[1] + " z: " + data[2] + " time: " + timestamp);

    }

    public static byte[] FloatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}
