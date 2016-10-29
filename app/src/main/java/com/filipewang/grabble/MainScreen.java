package com.filipewang.grabble;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
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
                CalendarManager test = new CalendarManager();
                String currDay = test.getDayOfWeek();

                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, currDay, duration);
                toast.show();
            }
        });

        Button wordButton = (Button) findViewById(R.id.mainWord);
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarManager test = new CalendarManager();
                String currDay = test.getDayOfWeek();
                new DownloadMapData().execute(currDay);
            }
        });

    }

    class DownloadMapData extends AsyncTask<String,Void,Void> {

        private String root = Environment.getExternalStorageDirectory().toString();
        private String TAG = "DOWNLOADMAPDATA";

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(MainScreen.this,"Downloading",
                    "Downloading markers for the map, please wait...", false, false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... data) {
            int count;
            File kmlFile = new File(root+"/" + data[0] + ".kml");
            try {
                kmlFile.createNewFile();
                URL url = new URL(data[0]);
                URLConnection urlCon = url.openConnection();
                urlCon.connect();
                InputStream inStream = urlCon.getInputStream();
                OutputStream outStream = new FileOutputStream(kmlFile);
                byte [] textData = new byte[1024];
                while((count = inStream.read(textData)) != -1){
                    outStream.write(textData,0,count);
                }
                outStream.flush();
                outStream.close();
                inStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception in AsyncTask ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void x){
            progressDialog.dismiss();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "HURRAY!", duration);
            toast.show();
        }
    }
}
