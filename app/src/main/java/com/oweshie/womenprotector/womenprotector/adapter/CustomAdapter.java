package com.oweshie.womenprotector.womenprotector.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oweshie.womenprotector.womenprotector.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private ArrayList<String> mNames;
    private ArrayList<String> mDevices;


    public CustomAdapter(Activity context, ArrayList<String> values) {
        super(context, R.layout.bluetooth_list_adapter);
        //Set the value of variables

    }

    //Here the ListView will be displayed
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View layoutView = mContext.getLayoutInflater().inflate(R.layout.bluetooth_list_adapter, null, true);
        TextView tvName= (TextView) layoutView.findViewById(R.id.deviceName);
        TextView tvId= (TextView) layoutView.findViewById(R.id.deviceId);
        tvName.setText("Hi");
        tvId.setText("Hello");
        return layoutView;
    }
}