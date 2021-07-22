package edu.txstate.reu.ble;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class BluetoothLe {

    private static BleServer bleServer;
    private static BleClient bleClient;
    private BluetoothLe () {}

    public static BleServer getBleServer(Context context) {
        if (bleServer == null) {
            bleServer = new BleServer(context);
        }
        return bleServer;
    }

    public static BleClient getBleClient(Context context) {
       if (bleClient == null) {
           bleClient = new BleClient(context);
       }
       return bleClient;
    }

}
