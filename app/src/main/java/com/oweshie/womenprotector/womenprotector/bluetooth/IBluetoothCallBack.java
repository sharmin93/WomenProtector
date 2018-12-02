package com.oweshie.womenprotector.womenprotector.bluetooth;

public interface IBluetoothCallBack {
    void onConnectionFailed();
    void onConnected();
    void onDataReceive(String data);
    void onDisconnected();
}
