package com.utem.ftmk.ws2.arsconsumer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.utem.ftmk.ws2.arsconsumer.R;
import com.utem.ftmk.ws2.arsconsumer.model.Client;
import com.utem.ftmk.ws2.arsconsumer.ui.MainActivity;

import java.io.IOException;
import java.util.Locale;

public class LocationUtil {

    private static final double DEFAULT_LATITUDE = 2.3137898;
    private static final double DEFAULT_LONGITUDE = 102.3189279;

    public static double getDistanceBetween(Client.Location location1, Client.Location location2) {
        float[] results = new float[1];
        Location.distanceBetween(location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude(), results);
        return results[0] / 1000;
    }

    public static void getLastKnownLocation(Activity activity, LocationSuccessListener listener) {

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        LocationServices.getFusedLocationProviderClient(activity).getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        location = new Location("");
                        location.setLatitude(DEFAULT_LATITUDE);
                        location.setLongitude(DEFAULT_LONGITUDE);
                    }
                    Client.Location currentLocation = new Client.Location();
                    currentLocation.setName(activity.getString(R.string.text_current_location));
                    currentLocation.setAddress(getLocationAddress(activity, location));
                    currentLocation.setLatitude(location.getLatitude());
                    currentLocation.setLongitude(location.getLongitude());
                    listener.onSuccess(currentLocation);
                });
    }

    private static String getLocationAddress(Context context, Location location) {
        StringBuilder addressStringBuilder = new StringBuilder("");
        try {
            Address addresses = new Geocoder(context, Locale.getDefault()).getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1).get(0);
            for (int index = 0; index <= addresses.getMaxAddressLineIndex(); index++) {
                addressStringBuilder.append(addresses.getAddressLine(index)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressStringBuilder.toString();
    }

    public interface LocationSuccessListener {
        void onSuccess(Client.Location location);
    }

}
