package com.oweshie.womenprotector.womenprotector.common;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oweshie.womenprotector.womenprotector.RunTimeDataStore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CommonTask {


    public static void showDailog(Context context, String title, String message, final IDialogClick iDialogClick, String positiveString, String negativeString){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        iDialogClick.onPositive();
                    }
                })
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        iDialogClick.onNegative();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void savePreference(Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveBluetoothObject(Context context, String key, BluetoothDevice  bluetoothDevice){
        SharedPreferences sharedPref = context.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(bluetoothDevice);

        editor.putString(key, json);
        editor.commit();
    }

    public static BluetoothDevice getBluetoothObject(Context context, String key ){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        String json = sharedPref.getString(key, "");
        if (json==null || json=="" ){
            return null;
        }
        BluetoothDevice obj = gson.fromJson(json, BluetoothDevice.class);
        return obj;

    }



    public static String getPreference(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE);
        String result = sharedPref.getString(key, "");
        return result;
    }

    public static void sendSMS(String phoneNo, String msg) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }





}
