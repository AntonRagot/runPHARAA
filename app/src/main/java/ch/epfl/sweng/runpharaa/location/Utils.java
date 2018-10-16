package ch.epfl.sweng.runpharaa.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class Utils {

    /*private Activity activity = null;
    private LatLng lastLocation = null;

    @SuppressLint("MissingPermission")
    public LatLng getCurrLocation(final Activity a){
        FusedLocationProviderClient mFusedLocationClient;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(a);

        activity = a;

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(a, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location == null) {
                            Toast.makeText(activity, "No last location, setting location to SAT", Toast.LENGTH_SHORT).show();
                        } else {
                            lastLocation =  new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                });

        return lastLocation;
    }*/



    @SuppressLint("MissingPermission")
    public static Location getCurrLocation(Activity a){
        LocationManager locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}