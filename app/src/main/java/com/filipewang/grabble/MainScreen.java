package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreen.this, SettingsScreen.class));
            }
        });

        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreen.this, InfoScreen.class));
            }
        });

        Button captureButton = (Button) findViewById(R.id.mainCapture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean gpsLocation = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkLocation = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Connect to the internet!", Snackbar.LENGTH_LONG)
                            .show();
                } else if (!networkLocation && !gpsLocation) {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Turn on your GPS!", Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    CalendarManager test = new CalendarManager();
                    String currDayWeek = test.getDayOfWeek();
                    String currDay = test.getCurrentDay();
                    new DownloadMapData().execute(currDayWeek, currDay);
                }
            }
        });

        Button wordButton = (Button) findViewById(R.id.mainWord);
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }


    class DownloadMapData extends AsyncTask<String, Void, Boolean> {
        private String root = Environment.getExternalStorageDirectory().toString();
        private String TAG = "DownloadMapData";
        private String dayOfWeek;
        private String currDay;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainScreen.this, "Downloading",
                    "Downloading markers for the map, please wait...", false, false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... data) {
            int count;
            dayOfWeek = data[0];
            currDay = data[1];
            String urlBase = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/";
            String urlString = urlBase + dayOfWeek + ".kml";
            File kmlFile = new File(root + "/" + currDay + ".kml");
            File actualFile = new File(root + "/" + currDay + ".tmp");
            try {
                if (actualFile.exists() && !actualFile.isDirectory()) {
                    Log.d(TAG, "Exists!");
                } else {
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
                }
            } catch (Exception e1) {
                Log.d(TAG, "Issues");
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean flag) {
            File actualFile = new File(root + "/" + currDay + ".tmp");
            if (!(actualFile.exists() && !actualFile.isDirectory() && flag)) {
                try {
                    if (!flag) {
                        Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Some error downloading the files occurred!", Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        KMLParser kmlParser = new KMLParser(currDay + ".kml");
                        ArrayList<MarkerData> markerList = kmlParser.parseFile();
                        Log.d(TAG, String.valueOf(markerList.size()));
                        FileOutputStream fos = new FileOutputStream(root + "/" + currDay + ".tmp");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(markerList);
                        oos.close();
                    }
                } catch (Exception e) {
                    Snackbar.make(findViewById(R.id.coordinatorLayoutMain), "Some error storing files occurred!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
            cleanUp();
            progressDialog.dismiss();
            startActivity(new Intent(MainScreen.this, CaptureScreen.class));
        }

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
