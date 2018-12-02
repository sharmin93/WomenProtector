package com.oweshie.womenprotector.womenprotector;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oweshie.womenprotector.womenprotector.bluetooth.IBluetoothCallBack;
import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.CommonTask;
import com.oweshie.womenprotector.womenprotector.common.DangerMessageUtil;
import com.oweshie.womenprotector.womenprotector.common.DataBaseData;
import com.oweshie.womenprotector.womenprotector.common.IDialogClick;
import com.oweshie.womenprotector.womenprotector.location.MyLocationObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Stack;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, IBluetoothCallBack {
    private Button connectButton;
    private Button inDangerButton;
    private TextView selectedDevice;
    private TextView userNameMain;
    private ListView lvListOfPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        init();
        initFireBase();

    }

    private void initFireBase() {
        RunTimeDataStore.getInstance().getMyReadReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> dataToLoad = new ArrayList<>();
                Iterable<DataSnapshot> snapshots =dataSnapshot.getChildren();

                Stack<DataBaseData> dataBaseDataArrayList = new Stack<>();
                for (DataSnapshot snap: snapshots) {
                    DataBaseData temp = snap.getValue(DataBaseData.class);
                    dataBaseDataArrayList.push(temp);
                }


                while(!dataBaseDataArrayList.empty()){
                    DataBaseData temp = dataBaseDataArrayList.pop();
                    temp.getName();
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                    format.setTimeZone(TimeZone.getDefault());
                    MyLocationObject myLocationObject = new MyLocationObject();
                    myLocationObject.setLatitude(temp.getLatitude());
                    myLocationObject.setLongitude(temp.getLongitude());
                    String text =temp.getName()+" is in danger.\nTime: "+format.format( temp.getDate())+"\n"+ "LOCATION: "+ new Gson().toJson(myLocationObject);
                    dataToLoad.add(text);
                }


                final ArrayList<String> finalDataToLoad = dataToLoad;

                final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.danger_list_item, R.id.titleTextView, finalDataToLoad);

                lvListOfPeople.setAdapter(adapter);
                lvListOfPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String text = adapterView.getItemAtPosition(i).toString();
                        int index = text.indexOf("{");
                        String json = text.substring(index);
                        MyLocationObject myLocationObject = new Gson().fromJson(json,MyLocationObject.class);
                        String uri = String.format(Locale.ENGLISH, "geo:%s,%s?q=%s,%s(Help)", myLocationObject.getLatitude(), myLocationObject.getLongitude(),myLocationObject.getLatitude(), myLocationObject.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        mapIntent.setPackage("com.google.android.apps.maps");

                        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(mapIntent);

//                        saveDevice(deviceName);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void init(){
//        userNameMain = findViewById(R.id.).findViewById(R.id.userNameMain);
////        userNameMain.setText(CommonTask.getPreference(this,CommonContant.USER_NAME));
        connectButton  = findViewById(R.id.connectButton);
        selectedDevice = findViewById(R.id.selectedDevice);
        inDangerButton = findViewById(R.id.inDangerButton);
        lvListOfPeople = findViewById(R.id.rvListOfPeople);
        connectButton.setOnClickListener(this);
        inDangerButton.setOnClickListener(this);
        String isDeviceSelected = CommonTask.getPreference(this,CommonContant.IS_DEVICE_SELECTED);
        if(isDeviceSelected != null && isDeviceSelected.equals(CommonContant.YES)){
            String message = "Selected Device: "+ CommonTask.getPreference(this,CommonContant.BLUETOOTH_DEVICE_NAME);
            selectedDevice.setText(message);
        }

        if(RunTimeDataStore.getInstance().isBluetoothConnected()){
            setButtonStatus(false, getString(R.string.connected), Color.GREEN);
        }else{
            setButtonStatus(true, getString(R.string.connect), Color.RED);
        }

    }

    private void setButtonStatus(boolean clickable,String message,int color){
        connectButton.setClickable(clickable);
        connectButton.setText(message);
        connectButton.setBackgroundColor(color);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.select_bluetooth) {
             Intent pairdListIntent = new Intent(this, BluetoothConnector.class);
             startActivity(pairdListIntent);
             finish();

        } else if (id == R.id.know_my_location) {
             String lat= CommonTask.getPreference(this, CommonContant.CURRENT_LATITUDE);
             String lon= CommonTask.getPreference(this, CommonContant.CURRENT_LONGITUDE);
             String message= "Your current Latitude is "+ lat + "and longitude is "+ lon+".";
             CommonTask.showDailog(this, "Location", message, new IDialogClick() {
                 @Override
                 public void onPositive() {

                 }

                 @Override
                 public void onNegative() {

                 }
             },"OK",null);
        } else if (id == R.id.settings) {
             Intent intent= new Intent(this, Settings.class);
             startActivity(intent);
             finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.connectButton){
            BluetoothDevice bluetoothDevice = CommonTask.getBluetoothObject(this, CommonContant.BLUETOOTH_DEVICE);
            if(bluetoothDevice!=null) {
                RunTimeDataStore.getInstance().setBluetoothDevice(bluetoothDevice);
                RunTimeDataStore.getInstance().connectToBluetooth(this);
            }else {
                // TODO: 7/28/18 select bluetooth device
            }
        } else if(view.getId()== R.id.inDangerButton){
            try {
//                DangerMessageUtil.sendMessageToEmergencyNumber(this);
                onDataReceive("danger");
                DangerMessageUtil.SendToDataBase(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed() {
        Thread thread=new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        setButtonStatus(true, getString(R.string.connect), Color.RED);
                    }

                });


            }
        };
        thread.start();
    }

    @Override
    public void onConnected() {
        Thread thread=new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        setButtonStatus(false, getString(R.string.connected), Color.GREEN);
                    }

                });


            }
        };
        thread.start();

    }

    @Override
    public void onDataReceive(String data) {
        String recvNumber = CommonTask.getPreference(this, CommonContant.EMERGENCY_NUMBER);
        if(recvNumber== null || recvNumber.length()==0) {
            Toast.makeText(this, "Please Set Emergency number in setting first", Toast.LENGTH_LONG).show();
            return;
        }
        String lat = CommonTask.getPreference(this, CommonContant.CURRENT_LATITUDE);
        String lon = CommonTask.getPreference(this, CommonContant.CURRENT_LONGITUDE);

        String message = "I am in danger. My location http://maps.google.com/?q=" +
                lat +
                "," +
                lon;

        sendSMS(recvNumber,message);
    }

    @Override
    public void onDisconnected() {
        Thread thread=new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        setButtonStatus(true, getString(R.string.connect), Color.RED);
                    }

                });


            }
        };
        thread.start();

    }

    private void sendSMS(final String phoneNumber, final String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();
                        values.put("address", phoneNumber);// txtPhoneNo.getText().toString());
                        values.put("body", message);
//                        for (int i = 0; i < MobNumber.size() - 1; i++) {
//                            values.put("address", MobNumber.get(i).toString());// txtPhoneNo.getText().toString());
//                            values.put("body", MessageText.getText().toString());
//                        }
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}
