package com.oweshie.womenprotector.womenprotector.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.location.Location;
import android.widget.Toast;

import com.oweshie.womenprotector.womenprotector.RunTimeDataStore;
import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.DangerMessageUtil;
import com.oweshie.womenprotector.womenprotector.common.DataBaseData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class BluetoothReader implements Runnable {
    private UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket bluetoothSocket ;
    private InputStream dataInputStream;
    private OutputStream dataOutputStream;
    private int state=DISCONNECTED;

    public static final int CONNECTED=1;
    public static final int READY_TO_READ=2;
    public static final int DISCONNECTED=3;

    public boolean isBluetoothConnected(){
        if(state != DISCONNECTED){
            return true;
        }
        return false;
    }


    public BluetoothReader(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public boolean connectDevice(BluetoothDevice bluetoothDevice){
        try {
            this.bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();
            state=CONNECTED;
            if (bluetoothSocket.isConnected()) {
                RunTimeDataStore.getInstance().setBluetoothSocket(bluetoothSocket);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();

        }
            return false;
    }

    @Override
    public void run() {
        while(true){
            if(state==CONNECTED){
                try {
                    dataInputStream = bluetoothSocket.getInputStream();
                    dataOutputStream =bluetoothSocket.getOutputStream();
                    state=READY_TO_READ;
                } catch (IOException e) {
                    e.printStackTrace();
                    state=DISCONNECTED;
                }
            }else if(state== READY_TO_READ){
                try {
                    dataOutputStream.write("ping".getBytes());
                    dataOutputStream.flush();
                    if(dataInputStream.available()>0) {
                        byte[] bytes= new byte[100];
                        dataInputStream.read(bytes);
                        String data = new String(bytes);

                        if( data.trim().startsWith("danger")){
                            RunTimeDataStore.getInstance().onBluetoothDataReceive(data);
                            DangerMessageUtil.sendMessageToEmergencyNumber(RunTimeDataStore.getInstance().getContext());
                            DangerMessageUtil.SendToDataBase(RunTimeDataStore.getInstance().getContext());

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    state=DISCONNECTED;
                }

            }else if(state==DISCONNECTED){
                RunTimeDataStore.getInstance().onBluetoothDisconnected();

                break;
            }
        }

    }
}
