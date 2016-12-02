package com.filipewang.grabble;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingsScreen extends AppCompatActivity {

    private SharedPreferences pref;
    private CalendarManager calendarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        pref = getSharedPreferences("PREFS", 0);
        calendarManager = new CalendarManager();

        Button resetButton = (Button) findViewById(R.id.buttonSettingsReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsScreen.this);
                builder.setMessage("Are you sure you want to clear the inventory?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FileManager fm = new FileManager();
                                int [] letterCount = fm.retrieveLetters();
                                for(int j = 1; j<letterCount.length; j++){
                                    letterCount[j]++;
                                }
                                fm.setLetterCount(letterCount);
                                fm.storeLetters();
                                String currDay = calendarManager.getCurrentDay();
                                String letterOfDay = pref.getString(currDay,"0");
                                Toast.makeText(SettingsScreen.this,
                                        letterOfDay, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
