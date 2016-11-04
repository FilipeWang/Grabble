package com.filipewang.grabble;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;


public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FileManager fm;
    private String TAG = "CaptureScreen";
    private ArrayList<MarkerData> markerList;
    private int [] letterCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        fm = new FileManager();
        letterCount = fm.retrieveLetters();
        if(letterCount == null)
            letterCount = new int[26];
        markerList = fm.retrieveMarkerList();
        fm.setMarkerList(markerList);
        fm.storeMarkerList();
        fm.storeLetters(letterCount);

        mapFragment.getMapAsync(this);

        FloatingActionButton b1 = (FloatingActionButton) findViewById(R.id.floating1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureScreen.this);
                String currentInventory = getLetterCount();
                builder.setMessage(currentInventory)
                        .setTitle("Letter Inventory");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume(){
        markerList = fm.retrieveMarkerList();
        letterCount = fm.retrieveLetters();
        super.onResume();
    }

    @Override
    protected void onRestart(){
        markerList = fm.retrieveMarkerList();
        letterCount = fm.retrieveLetters();
        super.onRestart();
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


        //Test styling
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setBuildingsEnabled(false);

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
                char c = marker.getTitle().charAt(0);
                int numValue = (int) c;
                int indexLetter = numValue - 65;
                letterCount[indexLetter]++;
                new StoreDataMarker().execute(markerList);
                new StoreDataLetters().execute(letterCount);
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

        try{
            for(MarkerData curr: markerList){
                mMap.addMarker(new MarkerOptions()
                        .position(curr.getCoordinates())
                        .title(curr.letter)
                        .snippet(curr.name));
            }
        } catch (Exception e){
            Snackbar.make(findViewById(R.id.coordinatorLayoutCapture),"No more letters for today!", Snackbar.LENGTH_LONG)
                    .show();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(0).getCoordinates(),20));
    }

    public String getLetterCount(){
        String text = "";
        for(int i = 0; i < 26; i++){
            if(i > 0 && i % 5 == 0)
                text = text + "\n";
            text = text + Character.toString((char) (i + 65)) + ": " + letterCount[i] + "     ";
        }
        return text;
    }

    class StoreDataMarker extends AsyncTask<ArrayList<MarkerData>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<MarkerData>... arrayLists) {
            try{
                FileManager fm = new FileManager();
                fm.setMarkerList(arrayLists[0]);
                fm.storeMarkerList();
                return true;
            } catch(Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if(!flag)
                Log.d(TAG, "Error in storing data!");
        }
    }

    class StoreDataLetters extends AsyncTask<int [], Void, Boolean> {

        @Override
        protected Boolean doInBackground(int []... arrays) {
            try{
                FileManager fm = new FileManager();
                fm.storeLetters(arrays[0]);
                return true;
            } catch(Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if(!flag)
                Log.d(TAG, "Error in storing data!");
        }
    }
}
