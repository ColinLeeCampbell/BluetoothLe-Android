package com.example.wear;

import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.sql.Timestamp;

import edu.txstate.reu.ble.BleClient;
import edu.txstate.reu.ble.BleServer;
import edu.txstate.reu.ble.BluetoothLe;
import edu.txstate.reu.ble.Event;

public class MainActivity extends Activity implements BleServer.OnEventReceivedListener {

    private final String TAG = "BluetoothLe Example";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent sensorIntent = new Intent(this, SensorService.class );
        ContextCompat.startForegroundService(this, sensorIntent);
        BluetoothLe.getBleServer(getApplicationContext()).addEventListener(this);
    }

    @Override
    public void onEventReceived(Event event) {
        float [] data = event.getData();
        Timestamp timestamp = event.getTimestamp();
        Log.d(TAG, "onEventReceived:  " + data[0] + " time: "+ timestamp);
    }
}