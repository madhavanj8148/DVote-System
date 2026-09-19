package com.example.dvotesystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    public interface LocationCallback {
        void onSuccess(UserLocation userLocation);
        void onFailure(String error);
    }

    public static boolean hasLocationPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, requestCode);
    }

    public static void getUserLocation(Context context, LocationCallback callback) {
        if (!hasLocationPermissions(context)) {
            callback.onFailure("Location permission not granted");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        reverseGeocode(context, location, callback);
                    } else {
                        callback.onFailure("Could not retrieve location. Make sure GPS is on.");
                    }
                }
            });
        } catch (SecurityException e) {
            callback.onFailure("Permission error: " + e.getMessage());
        }
    }

    private static void reverseGeocode(Context context, Location location, LocationCallback callback) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                UserLocation userLocation = new UserLocation(
                        address.getCountryName(),
                        address.getAdminArea(), // State
                        address.getSubAdminArea(), // District
                        address.getLocality(), // City
                        address.getPostalCode(),
                        location.getLatitude(),
                        location.getLongitude(),
                        address.getAddressLine(0)
                );
                callback.onSuccess(userLocation);
            } else {
                callback.onFailure("Could not resolve address from coordinates.");
            }
        } catch (IOException e) {
            callback.onFailure("Geocoder error: " + e.getMessage());
        }
    }

    public static void updateLocationInFirebase(UserLocation userLocation) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userRef.child("location").setValue(userLocation);
        userRef.child("lastLocationUpdated").setValue(ServerValue.TIMESTAMP);
    }
}
