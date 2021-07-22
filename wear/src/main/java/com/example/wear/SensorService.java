package com.example.wear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import edu.txstate.reu.ble.*;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class SensorService extends Service implements SensorEventListener, BleClient.OnEventReceivedListener  {

    private final String TAG = "Sensor Service";
    private SensorManager mmSensorManager;
    private Sensor mmSensor;
    private float[] mmSensorValues;
    private BleServer mmBleServer;
    JSONObject object;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "ForegroundServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Foreground Service")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        mmSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mmSensor = mmSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mmSensorManager.registerListener(this,mmSensor,32000);
        BluetoothLe.getBleClient(getApplicationContext()).addEventListener(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mmSensor != null) {
            mmSensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mmSensorValues = event.values;
        BluetoothLe.getBleServer(getApplicationContext()).send(FloatArray2ByteArray(mmSensorValues));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static byte[] FloatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }

    @Override
    public void onEventReceived(Event event) {
        Log.d(TAG, "onEventReceived: " + event.getData());
    }
}
