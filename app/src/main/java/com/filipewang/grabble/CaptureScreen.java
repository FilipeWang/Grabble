package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private CalendarManager calendarManager;
    private KMLParser kmlParser;
    private String TAG = "CaptureScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        calendarManager = new CalendarManager();
        String currDay = calendarManager.getCurrentDay();
        kmlParser = new KMLParser(currDay + ".kml");

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
        //LatLng edinburgh = new LatLng(55.9533, 3.1883);
        //mMap.addMarker(new MarkerOptions().position(edinburgh).title("Marker in Edinburgh"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(edinburgh));

        //Test styling
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        mMap.getUiSettings().setMapToolbarEnabled(false);

        //Set max zoom out
        //mMap.setMinZoomPreference(17);

        ArrayList<MarkerData> markerList = kmlParser.parseFile();
        mMap.addMarker(new MarkerOptions().position(markerList.get(0).coordinates).title(markerList.get(0).letter));
        Log.d(TAG,markerList.get(0).coordinates.toString());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(markerList.get(0).coordinates));
        progressDialog.dismiss();
    }
}
