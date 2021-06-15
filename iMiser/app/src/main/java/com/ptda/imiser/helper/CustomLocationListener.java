package com.ptda.imiser.helper;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomLocationListener implements LocationListener{
    public static double latitude;
    public static double longitude;
    public static String address;
    public static String city;
    public static String state;
    public static String country;
    public static String postalCode;
    public static String knownName;
    public static Context context;
    private static Geocoder geocoder;

    public CustomLocationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.latitude  = location.getLatitude();
        this.longitude = location.getLongitude();
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                this.address = addresses.get(0).getAddressLine(0);
                this.city = addresses.get(0).getLocality();
                this.state = addresses.get(0).getAdminArea();
                this.country = addresses.get(0).getCountryName();
                this.postalCode = addresses.get(0).getPostalCode();
                this.knownName = addresses.get(0).getFeatureName();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}