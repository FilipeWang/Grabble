package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private CalendarManager calendarManager;
    private String TAG = "CaptureScreen";
    private String root = Environment.getExternalStorageDirectory().toString();
    private String currDay;
    private ArrayList<MarkerData> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        calendarManager = new CalendarManager();
        currDay = calendarManager.getCurrentDay();
        markerList = retrieveMarkerList();
        storeMarkerList(markerList);

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

    @Override
    protected void onPause(){
        progressDialog = ProgressDialog.show(CaptureScreen.this,"Saving Data",
                "Saving markers, please wait...", false, false);
        progressDialog.show();
        storeMarkerList(markerList);
        markerList = retrieveMarkerList();
        progressDialog.dismiss();
        super.onPause();
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
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String point = marker.getSnippet();
                marker.remove();
                int index = -1;
                for(int i = 0; i<markerList.size(); i++){
                    if(markerList.get(i).name.equals(point)){
                        index = i;
                        i = markerList.size();
                    }
                }
                markerList.remove(index);
            }
        });

        // Since we are consuming the event this is necessary to
        // manage closing openned markers before openning new ones
        // Based on:
        // http://stackoverflow.com/questions/14497734/dont-snap-to-marker-after-click-in-android-map-v2
        final Marker[] lastOpenned = {null};

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned[0] != null) {
                    // Close the info window
                    lastOpenned[0].hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned[0].equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned[0] = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned[0] = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

        //Set max zoom out
        //mMap.setMinZoomPreference(17);

        progressDialog = ProgressDialog.show(CaptureScreen.this,"Loading markers",
                "Placing markers, please wait...", false, false);
        progressDialog.show();

        for(MarkerData curr: markerList){
            mMap.addMarker(new MarkerOptions()
                    .position(curr.getCoordinates())
                    .title(curr.letter)
                    .snippet(curr.name));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(0).getCoordinates(),20));
        progressDialog.dismiss();
    }

    public void storeMarkerList(ArrayList<MarkerData> markerList){
        try{
            File del = new File(root + "/" + currDay + ".tmp");
            del.delete();
            FileOutputStream fos = new FileOutputStream(root + "/" + currDay + ".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(markerList);
            oos.close();
        } catch(Exception e){
            Log.d(TAG, "File creation error!");
        }
    }

    public ArrayList<MarkerData> retrieveMarkerList(){
        try{
            FileInputStream fis = new FileInputStream(root + "/" + currDay + ".tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<MarkerData> markers = (ArrayList<MarkerData>) ois.readObject();
            ois.close();
            return markers;
        } catch(Exception e){
            Log.d(TAG, "File retrieval error!");
            ArrayList<MarkerData> markers = new ArrayList<>();
            return markers;
        }
    }
}
