package com.huawei.huaweitoolkitexample;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;


public final class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    int REQUEST_LOCATION_PERMISSION = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();

        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(it -> {
            Intent locationIntent = new Intent(MainActivity.this, LocationActivity.class);
            MainActivity.this.startActivity(locationIntent);
        });
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        CameraPosition build = (new CameraPosition.Builder()).target(new LatLng(38.423733D, 27.142826D)).zoom(15.0F).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(build);
        googleMap.animateCamera(cameraUpdate);
        GroundOverlayOptions homeOverlay = (new GroundOverlayOptions()).image(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_camera)).position(new LatLng(38.423733D, 27.142826D), 500.0F);
        googleMap.addGroundOverlay(homeOverlay);

        googleMap.addCircle(new CircleOptions().center(new LatLng(38.423733, 27.142826)).radius(500.0)
                .fillColor(Color.RED)
        );
        setMapLongClick(googleMap);
        setPoiClick(googleMap);
        enableMyLocation(googleMap);

    }

    private void setMapLongClick(final GoogleMap map) {

        map.setOnMapLongClickListener(latLng -> {
            String snippet = String.format(
                    Locale.getDefault(),
                    getString(R.string.lat_long_snippet),
                    latLng.latitude,
                    latLng.longitude
            );

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).snippet(snippet).title("Pinned Place");
            map.addMarker(markerOptions);

        });
    }

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(poi -> {

            MarkerOptions markerOptions = new MarkerOptions().position(poi.latLng).title(poi.name).snippet(poi.latLng.toString());
            Marker poiMarker = map.addMarker(markerOptions);
            if (poiMarker != null)
                poiMarker.setTag("poi");
        });
    }

    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED
        ) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {

            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_LOCATION_PERMISSION);

        }
    }


}