package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        progressDialog = ProgressDialog.show(CaptureScreen.this,"Preparing the Map",
                "Preparing the map, please wait...", false, false);
        progressDialog.show();
        mapFragment.getMapAsync(this);

        FloatingActionButton b1 = (FloatingActionButton) findViewById(R.id.floating1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.coordinatorLayoutCapture),"Test!", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        progressDialog.dismiss();
    }
}
