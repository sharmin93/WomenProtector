package com.oweshie.womenprotector.womenprotector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnector extends AppCompatActivity {
    private ListView bluetoothListView;
    private Set<BluetoothDevice> pairedDevices;
    private UUID myUUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connector);
        initView();
        showPairedDevice();
    }

    private void initView() {
        bluetoothListView = findViewById(R.id.bluetooth_listview);
    }

    private void showPairedDevice() {
        pairedDevices = RunTimeDataStore.getInstance().getBluetoothAdapter().getBondedDevices();
        ArrayList<BluetoothDevice> list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices) {
            list.add(bt);
        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        bluetoothListView.setAdapter(adapter);
        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = (BluetoothDevice) adapterView.getItemAtPosition(i);
                connectDevice(device);
            }
        });

    }

    private void connectDevice(BluetoothDevice bluetoothDevice) {
        for (BluetoothDevice device : pairedDevices){
            if (bluetoothDevice.equals(bluetoothDevice)){
                Toast.makeText(this,bluetoothDevice.getName(),Toast.LENGTH_LONG).show();
                try {
                    BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                    bluetoothSocket.connect();
                    if (bluetoothSocket.isConnected()){
                        Toast.makeText(this,"Connected",Toast.LENGTH_LONG).show();
                    }
                    MyBluetoothSocket myBluetoothSocket = new MyBluetoothSocket(bluetoothSocket);

                } catch (IOException e) {
                    e.printStackTrace(); // TODO: 23-05-18 handle this case
                    Toast.makeText(this,"Not connected",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
