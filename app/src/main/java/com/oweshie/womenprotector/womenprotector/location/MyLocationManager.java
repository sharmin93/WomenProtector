package com.oweshie.womenprotector.womenprotector.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;

import com.oweshie.womenprotector.womenprotector.common.CommonContant;
import com.oweshie.womenprotector.womenprotector.common.CommonTask;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by Sourav on 24-05-18.
 */

public class MyLocationManager {
    private Context context;

    public MyLocationManager(Context context) {
        this.context = context;
    }

    public void startLocationUpdate(){
        SmartLocation.with(context).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        CommonTask.savePreference(context, CommonContant.CURRENT_LATITUDE, String.valueOf(location.getLatitude()));
                        CommonTask.savePreference(context, CommonContant.CURRENT_LONGITUDE, String.valueOf(location.getLongitude()));

                        SmartLocation.with(context).geocoding()
                                .reverse(location, new OnReverseGeocodingListener() {

                                    @Override
                                    public void onAddressResolved(Location original, List<Address> results) {
                                        System.out.println(results.get(0).getAdminArea());
                                    }
                                });
                    }});
    }
}
