package com.oweshie.womenprotector.womenprotector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oweshie.womenprotector.womenprotector.bluetooth.BluetoothReader;
import com.oweshie.womenprotector.womenprotector.bluetooth.IBluetoothCallBack;


public class RunTimeDataStore{
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private IBluetoothCallBack iBluetoothCallBack;
    private BluetoothReader bluetoothReader;
    private Thread bluetoothThread;
    private FirebaseDatabase database;
    private DatabaseReference myReadReference;
    private DatabaseReference myWriteReference;

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

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public IBluetoothCallBack getiBluetoothCallBack() {
        return iBluetoothCallBack;
    }

    public void setiBluetoothCallBack(IBluetoothCallBack iBluetoothCallBack) {
        this.iBluetoothCallBack = iBluetoothCallBack;
    }


    public void onBluetoothConnectionFailed() {
        try{
            iBluetoothCallBack.onConnectionFailed();
        }catch(Exception e){
            iBluetoothCallBack=null;
        }

    }

    public void onBluetoothConnected() {
        try{
            iBluetoothCallBack.onConnected();
        }catch(Exception e){
            iBluetoothCallBack=null;
        }
    }

    public void onBluetoothDataReceive(String data) {
        try{
            iBluetoothCallBack.onDataReceive(data);
        }catch(Exception e){
            iBluetoothCallBack=null;
        }
    }

    public void onBluetoothDisconnected() {
        try{
            iBluetoothCallBack.onDisconnected();
        }catch(Exception e){
            iBluetoothCallBack=null;
        }
    }

    public void connectToBluetooth(IBluetoothCallBack iBluetoothCallBack){
        if(isBluetoothConnected()){
            return;
        }
        this.iBluetoothCallBack = iBluetoothCallBack;
        if(bluetoothDevice== null){
            onBluetoothConnectionFailed();
            return;
        }
        if(bluetoothThread!=null  && bluetoothThread.isAlive()){
            bluetoothThread.interrupt();
        }
        bluetoothReader = new BluetoothReader(bluetoothSocket);
        boolean success= bluetoothReader.connectDevice(bluetoothDevice);
        if(success){
            onBluetoothConnected();
        }
        bluetoothThread = new Thread(bluetoothReader);
        bluetoothThread.start();
    }

    public boolean isBluetoothConnected(){
        if(bluetoothReader!= null){
            return bluetoothReader.isBluetoothConnected();
        }
        return false;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getMyReadReference() {
        return myReadReference;
    }

    public void setMyReadReference(DatabaseReference myReadReference) {
        this.myReadReference = myReadReference;
    }

    public DatabaseReference getMyWriteReference() {
        return myWriteReference;
    }

    public void setMyWriteReference(DatabaseReference myWriteReference) {
        this.myWriteReference = myWriteReference;
    }
}
