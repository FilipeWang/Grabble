package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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
                startActivity(new Intent(MainScreen.this,SettingsScreen.class));
            }
        });

        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreen.this,InfoScreen.class));
            }
        });

        Button captureButton = (Button) findViewById(R.id.mainCapture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(findViewById(R.id.coordinatorLayout),"No internet!", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        Button wordButton = (Button) findViewById(R.id.mainWord);
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarManager test = new CalendarManager();
                String currDayWeek = test.getDayOfWeek();
                String currDay = test.getCurrentDay();
                new DownloadMapData().execute(currDayWeek,currDay);
            }
        });

    }


    class DownloadMapData extends AsyncTask<String,Void,Boolean> {
        private String root = Environment.getExternalStorageDirectory().toString();
        private String TAG = "DOWNLOADMAPDATA";
        private String dayOfWeek;
        private String currDay;

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(MainScreen.this,"Downloading",
                    "Downloading markers for the map, please wait...", false, false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... data) {
            int count;
            dayOfWeek = data[0];
            currDay = data[1];
            //Context context = getApplicationContext();
            //int duration = Toast.LENGTH_SHORT;
            String urlBase = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/";
            String urlString = urlBase + dayOfWeek + ".kml";
            File kmlFile = new File(root+"/" + currDay + ".kml");
            try {
                if(kmlFile.exists() && !kmlFile.isDirectory()){
                    Log.d(TAG,"Exists!");
                }
                else {
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
                Log.d(TAG,"Issues");
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean flag){
            File rootFolder = new File(root);
            File fileList[] = rootFolder.listFiles();
            for(File f: fileList){
                if(f.getName().endsWith(".kml") && !(f.getName().equals(currDay + ".kml"))){
                    Log.d(TAG,f.getName());
                    f.delete();
                }
            }
            progressDialog.dismiss();
            if(!flag){
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "An error has ocurred!", duration);
                toast.show();
            }

        }
    }
}
