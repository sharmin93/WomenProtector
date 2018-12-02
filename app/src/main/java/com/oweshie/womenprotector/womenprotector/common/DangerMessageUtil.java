package com.oweshie.womenprotector.womenprotector.common;

import android.content.Context;
import android.widget.Toast;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;
import com.oweshie.womenprotector.womenprotector.RunTimeDataStore;

import java.io.IOException;
import java.util.Date;

public class DangerMessageUtil {
    public static void sendMessageToEmergencyNumber(Context context) throws IOException {
        String recvNumber = CommonTask.getPreference(context, CommonContant.EMERGENCY_NUMBER);
        if(recvNumber== null || recvNumber.length()==0) {
            Toast.makeText(context, "Please Set Emergency number in setting first", Toast.LENGTH_LONG).show();
            return;
        }
        String lat = CommonTask.getPreference(context, CommonContant.CURRENT_LATITUDE);
        String lon = CommonTask.getPreference(context, CommonContant.CURRENT_LONGITUDE);

        String message = "I am in danger. My location http://maps.google.com/?q=" +
                lat +
                "," +
                lon;
//        String message= "i am in danger. My Location https://www.google.com/maps/?q=" +
//                lat +
//                "," +
//                lon;
//        Settings sendSettings = new Settings();
//        Transaction sendTransaction = new Transaction(context, sendSettings);
//        Message mMessage = new Message(message, recvNumber);
//        sendTransaction.sendNewMessage(mMessage, Transaction.NO_THREAD_ID);
        CommonTask.sendSMS(recvNumber,message);
    }
    public static void SendToDataBase(Context context) throws IOException {
        String databaseWriteRefernce = CommonTask.getPreference(context, CommonContant.DATABASE_WRITE_REFERENCE);
        String userName = CommonTask.getPreference(context, CommonContant.USER_NAME);
        String lat = CommonTask.getPreference(context, CommonContant.CURRENT_LATITUDE);
        String lon = CommonTask.getPreference(context, CommonContant.CURRENT_LONGITUDE);
        if (databaseWriteRefernce!= null && !databaseWriteRefernce.trim().equals("")) {
            RunTimeDataStore.getInstance().setMyWriteReference(RunTimeDataStore.getInstance().getDatabase().getReference(databaseWriteRefernce));
            RunTimeDataStore.getInstance().getMyWriteReference().removeValue();
        }
        DataBaseData dataBaseData = new DataBaseData();


        dataBaseData.setDate(new Date().getTime());
        dataBaseData.setLatitude(lat);
        dataBaseData.setLongitude(lon);
        dataBaseData.setName(userName);
        String key = RunTimeDataStore.getInstance().getDatabase().getReference().push().getKey();
        CommonTask.savePreference(context,  CommonContant.DATABASE_WRITE_REFERENCE, key);
        RunTimeDataStore.getInstance().setMyWriteReference(RunTimeDataStore.getInstance().getDatabase().getReference(key));
        RunTimeDataStore.getInstance().getMyWriteReference().setValue(dataBaseData);
    }
}
