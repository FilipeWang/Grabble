package com.filipewang.grabble;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

public class SettingsScreen extends AppCompatActivity {

    private SharedPreferences pref;
    private boolean letterSetting;
    private boolean markerSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        pref = getSharedPreferences("PREFS", 0);
        final SharedPreferences.Editor edit = pref.edit();

        Button resetInventoryButton = (Button) findViewById(R.id.buttonSettingsResetInventory);
        resetInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsScreen.this);
                builder.setMessage("Are you sure you want to clear the inventory (including the number of letters collected until now)?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FileManager fm = new FileManager();
                                int [] letterCount = fm.retrieveLetters();
                                for(int j = 1; j<letterCount.length; j++){
                                    letterCount[j] = 0;
                                }
                                fm.setLetterCount(letterCount);
                                fm.storeLetters();
                                Toast.makeText(SettingsScreen.this,
                                        "Successfully cleared the Inventory!", Toast.LENGTH_SHORT).show();
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

        Button resetScoreButton = (Button) findViewById(R.id.buttonSettingsResetScore);
        resetScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsScreen.this);
                builder.setMessage("Are you sure you want to reset your score?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                edit.putInt("currentScore", 0);
                                edit.commit();
                                Toast.makeText(SettingsScreen.this,
                                        "Successfully set score to 0!", Toast.LENGTH_SHORT).show();
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

        letterSetting = pref.getBoolean("bonusLetter",true);
        markerSetting = pref.getBoolean("markerColor",false);

        SwitchCompat bonusLetter = (SwitchCompat) findViewById(R.id.settingsLetterSwitch);
        bonusLetter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                edit.putBoolean("bonusLetter",b);
                edit.commit();
            }
        });

        SwitchCompat markerColor = (SwitchCompat) findViewById(R.id.settingsMarkerSwitch);
        markerColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                edit.putBoolean("markerColor",b);
                edit.commit();
            }
        });

        bonusLetter.setChecked(letterSetting);
        markerColor.setChecked(markerSetting);
    }
}
