package com.oweshie.womenprotector.womenprotector;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by Sourav on 25-04-18.
 */

public class RunTimeDataStore {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private static RunTimeDataStore runTimeDataStore = new RunTimeDataStore();

    private RunTimeDataStore() {

    }

    public static RunTimeDataStore getInstance() {
        return runTimeDataStore;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }
}
