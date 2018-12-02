package com.oweshie.womenprotector.womenprotector.common;

import android.os.AsyncTask;

import com.oweshie.womenprotector.womenprotector.RunTimeDataStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;



public class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        try {
            String url= String.format("https://maps.google.com/maps/api/geocode/json?address=%s%%2C%s&sensor=false", params[0], params[1]);
            response = getLocationName(url);
            return new String[]{response};
        } catch (Exception e) {
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        try {
            JSONObject jsonObject = new JSONObject(result[0]);
            String address ="jj"; //((JSONArray)jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
            CommonTask.savePreference(RunTimeDataStore.getInstance().getContext(),CommonContant.CURRENT_LOCATION_NAME,address);
            CommonTask.savePreference(RunTimeDataStore.getInstance().getContext(),CommonContant.CURRENT_TIME, String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getLocationName(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}

