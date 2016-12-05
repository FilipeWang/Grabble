package com.filipewang.grabble;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoScreen extends AppCompatActivity {

    private SharedPreferences pref;
    private char letterOfDay;
    private CalendarManager calendarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);

        pref = getSharedPreferences("PREFS", 0);
        calendarManager = new CalendarManager();

        letterOfDay = pref.getString(calendarManager.getCurrentDay(),"0").charAt(0);
        TextView text = (TextView) findViewById(R.id.informationText);
        text.setText("The bonus letter is " + letterOfDay);
    }
}
