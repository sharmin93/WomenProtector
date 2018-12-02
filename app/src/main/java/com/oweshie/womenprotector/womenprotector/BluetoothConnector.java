package com.oweshie.womenprotector.womenprotector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.CommonTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnector extends AppCompatActivity {
    private ListView bluetoothListView;
    private Map<String, BluetoothDevice> pairedDevices= new HashMap<>();

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
        Set<BluetoothDevice> bluetoothDeviceList = RunTimeDataStore.getInstance().getBluetoothAdapter().getBondedDevices();
        ArrayList<String> list = new ArrayList<>();
        for (BluetoothDevice bt :
                bluetoothDeviceList) {
            pairedDevices.put(bt.getName(), bt);
            list.add(bt.getName());
        }

        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        bluetoothListView.setAdapter(adapter);
        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String deviceName = adapterView.getItemAtPosition(i).toString();
                saveDevice(deviceName);
            }
        });

    }

    private void saveDevice(String bluetoothDeviceName) {

        BluetoothDevice device = pairedDevices.get(bluetoothDeviceName);
//        RunTimeDataStore.getInstance().setBluetoothDevice(device);
        CommonTask.saveBluetoothObject(this, CommonContant.BLUETOOTH_DEVICE,device);
        CommonTask.savePreference(this, CommonContant.IS_DEVICE_SELECTED, CommonContant.YES);
        CommonTask.savePreference(this, CommonContant.BLUETOOTH_DEVICE_NAME, bluetoothDeviceName);
        Intent newIntent = new Intent(this, MainActivity.class);
        startActivity(newIntent);
        finish();

    }
}
