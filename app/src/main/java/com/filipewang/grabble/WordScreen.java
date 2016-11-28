package com.filipewang.grabble;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.location.LocationServices;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordScreen extends AppCompatActivity implements NumberPicker.OnValueChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;
    private static int LEADERBOARD = 1000;
    private static int ACHIEVEMENTS = 1001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    private final static int[] PICKERS = {
            R.id.letterPicker1, R.id.letterPicker2,
            R.id.letterPicker3, R.id.letterPicker4,
            R.id.letterPicker5, R.id.letterPicker6,
            R.id.letterPicker7
    };

    private final static int[] BUTTONS = {
            R.id.floatingAchievementsWord, R.id.floatingLeaderboardsWord,
            R.id.createWordButton, R.id.floatingInventoryWord
    };

    private FileManager fm;
    private SharedPreferences pref;
    private String TAG = "WordScreen";
    private TextView currWord;
    private TextView currValue;
    private TextView currScore;
    private String [] alphabet = {"A","B","C","D","E",
                                    "F","G","H","I","J",
                                    "K","L","M","N","O",
                                    "P","Q","R","S","T",
                                    "U","V","W","X","Y","Z"};
    private int [] letterValue = {3,20,13,10,1,15,18,9,5,25,22,11,14,
                                    6,4,19,24,8,7,2,12,21,17,23,16,26};
    private ArrayList<String> dictionary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_screen);

        pref = getSharedPreferences("PREFS", 0);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .setViewForPopups(findViewById(android.R.id.content))
                    .build();
        }

        for (int id : BUTTONS) {
            findViewById(id).setOnClickListener(this);
        }

        currWord = (TextView) findViewById(R.id.currentWord);
        currValue = (TextView) findViewById(R.id.currentValue);
        currScore = (TextView) findViewById(R.id.currentScore);

        for (int id : PICKERS) {
            NumberPicker np = (NumberPicker) findViewById(id);
            np.setMaxValue(0);
            np.setMaxValue(25);
            np.setDisplayedValues(alphabet);
            np.setOnValueChangedListener(this);
            np.setWrapSelectorWheel(false);
        }
        String curr = getCurrentWord();
        currWord.setText("Word: " + curr);
        currValue.setText("Value: " + String.valueOf(getValue(curr)));

        int score = pref.getInt("currentScore",0);
        currScore.setText(String.valueOf(score));

        fm = new FileManager();
        loadDictionary();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createWordButton:
                String curr = getCurrentWord();
                int wordScore = getValue(curr);
                if(dictionary.contains(curr.toLowerCase())){
                    int score = pref.getInt("currentScore",0);
                    int newScore = score + wordScore;
                    //CHECK FOR OVERFLOW
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putInt("currentScore", newScore);
                    edit.commit();
                    String message = "Valid word! Added " + wordScore + " points!";
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), message, Snackbar.LENGTH_LONG)
                            .show();
                    currScore.setText(String.valueOf(newScore));
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), "Word does not exist!", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case R.id.floatingInventoryWord:
                AlertDialog.Builder builder = new AlertDialog.Builder(WordScreen.this);
                fm.setLetterCount(fm.retrieveLetters());
                String currentInventory = fm.getLetterCount();
                builder.setMessage(currentInventory)
                        .setTitle("Letter Inventory");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.floatingAchievementsWord:
                boolean internet = checkInternetLocation(1);
                if(internet){
                    try {
                        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                                ACHIEVEMENTS);
                    } catch (Exception e) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(WordScreen.this);
                        builder2.setMessage("Player not signed in, cannot access achievements. \nDo you want to restart word mode?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        startActivity(new Intent(WordScreen.this,WordScreen.class));
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
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), "No internet!", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case R.id.floatingLeaderboardsWord:
                boolean internet2 = checkInternetLocation(1);
                if(internet2){
                    try {
                        //Submit score NOW
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                                getApplicationContext().getResources().getString(R.string.leaderboard_word)), LEADERBOARD);
                    } catch (Exception e) {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(WordScreen.this);
                        builder3.setMessage("Player not signed in, cannot access leaderboards. \nDo you want to restart word mode?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        startActivity(new Intent(WordScreen.this,WordScreen.class));
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
                } else{
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), "No internet!", Snackbar.LENGTH_LONG)
                            .show();
                }
                break;

        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        String curr = getCurrentWord();
        currWord.setText("Word: " + curr);
        currValue.setText("Value: " + String.valueOf(getValue(curr)));
    }

    private String getCurrentWord(){
        String word = "";
        for(int id: PICKERS){
            NumberPicker np = (NumberPicker) findViewById(id);
            word = word + alphabet[np.getValue()];
        }
        return word;
    }

    private int getValue(String word){
        int value = 0;
        for(int i=0; i<7; i++){
            int numValue = (int) word.charAt(i);
            int indexLetter = numValue - 65;
            value = value + letterValue[indexLetter];
        }
        return value;
    }

    private void loadDictionary(){
        ArrayList<String> dict = new ArrayList<>();
        try {
            InputStreamReader in = new InputStreamReader(getAssets().open("dictionary.txt"));
            BufferedReader reader = new BufferedReader(in);
            String word;
            while((word = reader.readLine()) != null ){
                String wordFinal = word.toLowerCase();
                dict.add(wordFinal);
            }
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.coordinatorLayoutWord), "Error openening dictionary!", Snackbar.LENGTH_LONG)
                    .show();
        }
        dictionary = dict;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(WordScreen.this);
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
                                startActivity(new Intent(WordScreen.this,MainScreen.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else if (requestCode == LEADERBOARD) {
            if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WordScreen.this);
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
                                startActivity(new Intent(WordScreen.this,MainScreen.class));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(WordScreen.this);
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
                                startActivity(new Intent(WordScreen.this,MainScreen.class));
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

    private boolean checkInternetLocation(int caseNum){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean gpsLocation = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkLocation = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(caseNum == 0)
            return (gpsLocation && networkLocation && isConnected);
        else
            return isConnected;
    }
}
