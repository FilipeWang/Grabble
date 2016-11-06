package com.filipewang.grabble;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;


public class CaptureScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Circle currCircle;

    private FileManager fm;
    private String TAG = "CaptureScreen";
    private ArrayList<MarkerData> markerList;
    private int[] letterCount;
    private final static int[] BUTTONS = {
            R.id.floatingInventory, R.id.floatingLeaderboards,
            R.id.floatingAchievements
    };

    private static int RC_SIGN_IN = 9001;
    private static int LEADERBOARD = 1000;
    private static int ACHIEVEMENTS = 1001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        fm = new FileManager();
        letterCount = fm.retrieveLetters();
        if (letterCount == null)
            letterCount = new int[27];
        markerList = fm.retrieveMarkerList();
        fm.setMarkerList(markerList);
        fm.setLetterCount(letterCount);
        fm.storeMarkerList();
        fm.storeLetters();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .setViewForPopups(findViewById(android.R.id.content))
                    .build();
        }

        mapFragment.getMapAsync(this);

        for (int id : BUTTONS) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        markerList = fm.retrieveMarkerList();
        letterCount = fm.retrieveLetters();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        markerList = fm.retrieveMarkerList();
        letterCount = fm.retrieveLetters();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "Error")) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureScreen.this);
                builder.setMessage("Player not signed in, proceed without access to leaderboard or achievement screens?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(CaptureScreen.this,MainScreen.class));
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else if (requestCode == LEADERBOARD) {
            if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureScreen.this);
                builder.setMessage("Player not signed in, proceed without access to leaderboard or achievement screens?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(CaptureScreen.this,MainScreen.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                if (intent != null)
                    startActivity(intent);
            }
        } else if(requestCode == ACHIEVEMENTS){
            if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureScreen.this);
                builder.setMessage("Player not signed in, proceed without access to leaderboard or achievement screens?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(CaptureScreen.this,MainScreen.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                if (intent != null)
                    startActivity(intent);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingInventory:
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureScreen.this);
                fm.setLetterCount(letterCount);
                String currentInventory = fm.getLetterCount();
                builder.setMessage(currentInventory)
                .setTitle("Letter Inventory");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.floatingLeaderboards:
                try {
                    Log.d(TAG,"Letters submitted: " + letterCount[0]);
                    Games.Leaderboards.submitScore(mGoogleApiClient, getApplicationContext().getResources().getString(R.string.leaderboard_grabble), letterCount[0]);
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getApplicationContext().getResources().getString(R.string.leaderboard_grabble)), LEADERBOARD);
                } catch (Exception e) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(CaptureScreen.this);
                    builder2.setMessage("Player not signed in, cannot access leaderboards. \nDo you want to restart capture mode?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    startActivity(new Intent(CaptureScreen.this,CaptureScreen.class));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                }
                break;
            case R.id.floatingAchievements:
                try {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                            ACHIEVEMENTS);
                } catch (Exception e) {
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(CaptureScreen.this);
                    builder3.setMessage("Player not signed in, cannot access achievements. \nDo you want to restart capture mode?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    startActivity(new Intent(CaptureScreen.this,CaptureScreen.class));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    AlertDialog dialog3 = builder3.create();
                    dialog3.show();
                }
                break;
        }
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
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                boolean close = checkDistance(marker.getPosition());
                if (close) {
                    String point = marker.getSnippet();
                    marker.remove();
                    int index = -1;
                    for (int i = 0; i < markerList.size(); i++) {
                        if (markerList.get(i).name.equals(point)) {
                            index = i;
                            i = markerList.size();
                        }
                    }
                    markerList.remove(index);
                    char c = marker.getTitle().charAt(0);
                    int numValue = (int) c;
                    int indexLetter = numValue - 64;
                    letterCount[indexLetter]++;
                    letterCount[0]++;
                    new StoreDataMarker().execute(markerList);
                    new StoreDataLetters().execute(letterCount);
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutCapture), "Too far from the marker!", Snackbar.LENGTH_LONG)
                            .show();
                }
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

        try {
            for (MarkerData curr : markerList) {
                mMap.addMarker(new MarkerOptions()
                        .position(curr.getCoordinates())
                        .title(curr.letter)
                        .snippet(curr.name));
            }
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.coordinatorLayoutCapture), "No more letters for today!", Snackbar.LENGTH_LONG)
                    .show();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.94400, -3.192473), 19));

    }

    private boolean checkDistance(LatLng position) {
        Location currentLocation = getCurrentLocation();
        Location markerLocation = new Location("Current");
        drawBoundary(currentLocation);
        if (currentLocation != null) {
            markerLocation.setLatitude(position.latitude);
            markerLocation.setLongitude(position.longitude);

            float diff = currentLocation.distanceTo(markerLocation);
            int difference = Math.round(diff);
            return difference < 20;
        }
        return false;
    }

    private Location getCurrentLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"Changed");
        drawBoundary(location);
    }

    private void drawBoundary(Location location){
        Log.d(TAG,"Draw");
        if(location != null){
            if(currCircle != null)
                currCircle.remove();
            CircleOptions co = new CircleOptions();
            co.center(new LatLng(location.getLatitude(),location.getLongitude()));
            co.radius(20);
            co.strokeColor(Color.RED);
            co.strokeWidth(4.0f);
            currCircle = mMap.addCircle(co);
        }
    }


    class StoreDataMarker extends AsyncTask<ArrayList<MarkerData>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<MarkerData>... arrayLists) {
            try {
                FileManager fm = new FileManager();
                fm.setMarkerList(arrayLists[0]);
                fm.storeMarkerList();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (!flag)
                Log.d(TAG, "Error in storing data!");
        }
    }

    class StoreDataLetters extends AsyncTask<int[], Void, Boolean> {

        @Override
        protected Boolean doInBackground(int[]... arrays) {
            try {
                FileManager fm = new FileManager();
                fm.setLetterCount(arrays[0]);
                fm.storeLetters();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (!flag)
                Log.d(TAG, "Error in storing data!");
        }
    }
}
