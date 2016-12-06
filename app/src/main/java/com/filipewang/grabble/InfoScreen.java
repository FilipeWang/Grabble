package com.filipewang.grabble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class InfoScreen extends AppCompatActivity {

    final String text = "This application was developed for the course Software Engineering Large Practical." +
            "\n\nThere are many references that were used in the production in this application." +
            "\n\nMost of the ideas and information were taken from the Android Developer Guide and Stackoverflow." +
            "\n\nAll icons used were taken from the Google Material Open Source Icons." +
            "\n\nFinally the Floating Button was taken from Clans (Dmytro Tarianyk) in his GitHub repository FloatingActionButton";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);

        TextView infoText = (TextView) findViewById(R.id.informationText);
        infoText.setText(text);
    }
}
