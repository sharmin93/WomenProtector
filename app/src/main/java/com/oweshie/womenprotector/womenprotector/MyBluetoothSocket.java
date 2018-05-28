package com.oweshie.womenprotector.womenprotector;

import android.bluetooth.BluetoothSocket;

/**
 * Created by Sourav on 23-05-18.
 */

public class MyBluetoothSocket implements Runnable {
    private BluetoothSocket myBluetoothSocket;

    public MyBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.myBluetoothSocket = bluetoothSocket;
    }

    private void connect(){

    }

    @Override
    public void run() {

    }
}
