package com.huawei.huaweitoolkitexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LocationActivity extends AppCompatActivity implements LocationListener {
    private TextView locationText;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationText = findViewById(R.id.textview_location);
        Button getLocationButton = findViewById(R.id.start_button_location);
        Button stopLocationButton = findViewById(R.id.stop_button_location);


        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View it) {
                LocationActivity.this.createLocationRequest();
            }
        });
        stopLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View it) {
                LocationActivity.this.removeLocationUpdatesWithCallback();
            }
        });

        FloatingActionButton floatingActionButton = this.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View it) {
                Intent mainActivity = new Intent(LocationActivity.this, MainActivity.class);
                LocationActivity.this.startActivity(mainActivity);
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull @NotNull Location location) {
        locationText.append("LocationResult: " + "latitude: " + location.getLatitude() + " " + "longitude: " + location.getLongitude() + " " + "accuracy: " + location.getAccuracy() + "\n\n");
    }

    private void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000L);
        mLocationRequest.setFastestInterval(1000L);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 111);
            return;
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                LocationActivity.this.onLocationChanged(locationResult.getLastLocation());
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, this.mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        createLocationRequest();
    }

    private void removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(@NonNull Void empty) {
                    locationText.append("removeLocationUpdatesWithCallback onSuccess\n\n");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public final void onFailure(@NotNull Exception e) {
                    locationText.append("removeLocationUpdatesWithCallback onFailure:${e.message}\n\n");
                }
            });
        } catch (Exception e) {
            locationText.append("removeLocationUpdatesWithCallback exception:${e.message}\n\n");
        }
    }


}