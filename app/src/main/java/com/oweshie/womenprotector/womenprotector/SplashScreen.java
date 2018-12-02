package com.oweshie.womenprotector.womenprotector;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.CommonTask;
import com.oweshie.womenprotector.womenprotector.common.IDialogClick;
import com.oweshie.womenprotector.womenprotector.location.MyLocationManager;
import com.oweshie.womenprotector.womenprotector.location.MyService;


public class SplashScreen extends AppCompatActivity {

    private static final int BLUETOOTH_ENABLE_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initDataStore();
        requestSmsPermission();
    }

    private void gotoMainActivity() {
        MyLocationManager myLocationManager = new MyLocationManager(this);
        //myLocationManager.startLocationUpdate();
        Intent intent= new Intent(this, MyService.class);
        startService(intent);
        Intent gotoMain= new Intent(this,MainActivity.class);
        startActivity(gotoMain);
        finish();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CommonContant.LOCATION_REQUEST);


        }else{
            checkBluetooth();
        }

    }


    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    CommonContant.SEND_SMS_REQUEST);
        } else {
            checkLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CommonContant.LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBluetooth();
                } else {
                    System.exit(0);
                }
                return;
            }
            case CommonContant.SEND_SMS_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                } else {
                    System.exit(0);
                }
                return;
            }

        }
    }

    private void checkBluetooth() {
        if(!RunTimeDataStore.getInstance().getBluetoothAdapter().isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, BLUETOOTH_ENABLE_REQUEST);
        }else{
            gotoMainActivity();
        }

    }

    private void initDataStore() {

        if (BluetoothAdapter.getDefaultAdapter()!=null){
            RunTimeDataStore.getInstance().setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        }else{
            IDialogClick iDialogClick= new IDialogClick() {
                @Override
                public void onPositive() {
                    System.exit(0);
                }

                @Override
                public void onNegative() {

                }
            };
            CommonTask.showDailog(this,"Alert","Your Phone does not support Bluetoth",iDialogClick, "Ok", null);
        }
        RunTimeDataStore.getInstance().setContext(getApplicationContext());
        RunTimeDataStore.getInstance().setDatabase(FirebaseDatabase.getInstance());
        RunTimeDataStore.getInstance().setMyReadReference(RunTimeDataStore.getInstance().getDatabase().getReference());


       // TODO all data store
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        RunTimeDataStore.getInstance().getBluetooth().onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode ==BLUETOOTH_ENABLE_REQUEST ) {
            if(resultCode == Activity.RESULT_OK){
                gotoMainActivity();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                CommonTask.showDailog(this, "Alert", "You can not use this app without enabling bluetooth", new IDialogClick() {
                    @Override
                    public void onPositive() {
                        System.exit(0);
                    }

                    @Override
                    public void onNegative() {

                    }
                },"Ok",null);
            }
        }
    }

}
