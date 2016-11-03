package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
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


public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

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

    /*@Override
    protected void onStop(){
        storeMarkerList(markerList);
        super.onStop();
    }*/


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
        mMap.setOnInfoWindowClickListener(this);

        //Set max zoom out
        //mMap.setMinZoomPreference(17);

        for(MarkerData curr: markerList){
            mMap.addMarker(new MarkerOptions()
                    .position(curr.getCoordinates())
                    .title(curr.letter)
                    .snippet(curr.name));
        }
        progressDialog.dismiss();
    }

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
        storeMarkerList(markerList);
        markerList = retrieveMarkerList();
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
