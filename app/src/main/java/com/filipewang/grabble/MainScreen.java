package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the used for the MainScreen Activity.
 * This is the class where data is downloaded from the server to later use in Capture Mode.
 */
public class MainScreen extends AppCompatActivity implements View.OnClickListener{

    // Fields
    private ProgressDialog progressDialog;
    private SharedPreferences pref;
    private CalendarManager calendarManager;
    private final static int[] BUTTONS = {
            R.id.settingsButton, R.id.infoButton,
            R.id.mainCapture, R.id.mainWord,
            R.id.mainTitle
    };
    private String [] alphabet = {"A","B","C","D","E",
            "F","G","H","I","J",
            "K","L","M","N","O",
            "P","Q","R","S","T",
            "U","V","W","X","Y","Z"};
    private static int easterEgg = 0; // Counter for how many clicks were done to the title
    private String letterOfDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up letter of the day
        letterOfDaySetup();

        setContentView(R.layout.activity_main_screen);

        // Set the fields to the current instance
        calendarManager = new CalendarManager();


        // Set up listeners for the different buttons
        for (int id : BUTTONS) {
            findViewById(id).setOnClickListener(this);
        }
    }

    /**
     * Setup for the letter of the day.
     * It uses the current day as the ID stored in the SharedPreferences.
     * It first checks if the current letter of the day is a zero (meaning none has been assigned).
     * Then if it is zero it'll delete any previous entries (from previous days) from the SharedPreferences.
     * Then it'll assign a new letter and commit it to memory.
     */
    private void letterOfDaySetup(){
        pref = getSharedPreferences("PREFS", 0);
        String currDay = calendarManager.getCurrentDay();
        letterOfDay = pref.getString(currDay,"0");
        if(letterOfDay.equals("0")){
            String patternString = "^(\\d)+$";
            Pattern pattern = Pattern.compile(patternString);
            SharedPreferences.Editor edit = pref.edit();
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Matcher matcher = pattern.matcher(entry.getKey());
                if(matcher.find()){
                    edit.remove(entry.getKey());
                }
            }
            int indexLetter = ThreadLocalRandom.current().nextInt(0, 25 + 1);
            edit.putString(currDay, alphabet[indexLetter]);
            edit.commit();
        }
    }

    // Deals with any results of the action listener of the buttons.
    @Override
    public void onClick(View view) {
        switch(view.getId()){

            // Easter egg, if the title is clicked twice the bonus letter is shown
            case R.id.mainTitle:
                easterEgg++;
                if(easterEgg == 5){
                    easterEgg = 0;
                    String message = "Secret bonus letter: " + letterOfDay;
                    Toast.makeText(MainScreen.this,
                            message, Toast.LENGTH_SHORT).show();
                }
                break;

            // Takes the user to the settings screen
            case R.id.settingsButton:
                startActivity(new Intent(MainScreen.this, SettingsScreen.class));
                break;

            // Takes the user to the information screen
            case R.id.infoButton:
                startActivity(new Intent(MainScreen.this, InfoScreen.class));
                break;

            // Takes the user to capture mode if there location/internet are on.
            case R.id.mainCapture:

                //Check for the different connections
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean gpsLocation = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkLocation = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                // Act depending on what is connected on what is not
                if (!isConnected) {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Connect to the internet!", Snackbar.LENGTH_LONG)
                            .show();
                } else if (!networkLocation && !gpsLocation) {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Turn on your GPS!", Snackbar.LENGTH_LONG)
                            .show();
                } else {

                    // Getting here means all the checks passed and the data will now be downloaded
                    CalendarManager test = new CalendarManager();
                    String currDayWeek = test.getDayOfWeek();
                    String currDay = test.getCurrentDay();
                    new DownloadMapData().execute(currDayWeek, currDay);
                }
                break;

            // Take the user to Word mode
            case R.id.mainWord:
                    startActivity(new Intent(MainScreen.this,WordScreen.class));
                break;
        }
    }


    // AsyncTask used to download data from the server
    class DownloadMapData extends AsyncTask<String, Void, Boolean> {
        private String root = Environment.getExternalStorageDirectory().toString();
        private String TAG = "DownloadMapData";
        private String dayOfWeek;
        private String currDay;

        // Set up a progress dialog
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainScreen.this, "Downloading",
                    "Downloading markers for the map, please wait...", false, false);
            progressDialog.show();
        }

        // Download the data
        @Override
        protected Boolean doInBackground(String... data) {
            int count;
            dayOfWeek = data[0]; // Day of the week to put in url
            currDay = data[1]; // Current day to use as ID for the files

            // Create the URL needed
            String urlBase = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/";
            String urlString = urlBase + dayOfWeek + ".kml";

            // Create the KML file (that'll be parsed into the actual file)
            File kmlFile = new File(root + "/" + currDay + ".kml");
            File actualFile = new File(root + "/" + currDay + ".tmp");
            try {
                // If the file exists that means data has been downloaded already so don't do it again
                if (actualFile.exists() && !actualFile.isDirectory()) {
                    Log.d(TAG, "Exists!");
                } else {
                    // Download the data
                    URL url = new URL(urlString);
                    URLConnection urlCon = url.openConnection();
                    urlCon.connect();
                    kmlFile.createNewFile();
                    InputStream inStream = urlCon.getInputStream();
                    OutputStream outStream = new FileOutputStream(kmlFile);
                    byte[] textData = new byte[1024];
                    while ((count = inStream.read(textData)) != -1) {
                        outStream.write(textData, 0, count);
                    }
                    outStream.flush();
                    outStream.close();
                    inStream.close();

                    // Parse the KML file and save the result
                    FileManager fm = new FileManager();
                    ArrayList<MarkerData> markerList = fm.parseKmlFile(currDay + ".kml");
                    fm.setMarkerList(markerList);
                    fm.storeMarkerList();
                }
            } catch (Exception e1) {
                Log.d(TAG, "Issues");
                return false;
            }
            return true;
        }

        // If everything went fine move to the capture screen
        @Override
        protected void onPostExecute(Boolean flag) {
            if (!flag) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Some error downloading the files occurred!", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                cleanUp(); // Delete data from previous days
                progressDialog.dismiss();
                Toast.makeText(MainScreen.this,
                        "Moving to capture mode...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainScreen.this, CaptureScreen.class));
            }
        }

        // Method that looks for any file that has a different day as an ID than the current one
        private void cleanUp() {
            File rootFolder = new File(root);
            File fileList[] = rootFolder.listFiles();
            for (File f : fileList) {
                if (f.getName().endsWith(".kml")
                        || (f.getName().endsWith(".tmp") && !(f.getName().equals(currDay + ".tmp")))) {
                    f.delete();
                }
            }
        }
    }

}
