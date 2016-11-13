package com.filipewang.grabble;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordScreen extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private final static int[] PICKERS = {
            R.id.letterPicker1, R.id.letterPicker2,
            R.id.letterPicker3, R.id.letterPicker4,
            R.id.letterPicker5, R.id.letterPicker6,
            R.id.letterPicker7
    };
    private FileManager fm;
    private SharedPreferences pref;
    private String TAG = "WordScreen";
    private TextView currWord;
    private TextView currValue;
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

        Button createWord = (Button) findViewById(R.id.createWordButton);
        createWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curr = getCurrentWord();
                int wordScore = getValue(curr);
                if(dictionary.contains(curr.toLowerCase())){
                    int score = pref.getInt("currentScore",0);
                    int newScore = score + wordScore;
                    //CHECK FOR OVERFLOW
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putInt("currentScore", newScore);
                    edit.commit();
                    String message = "Valid word! Added " + wordScore + " points to current score!";
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), message, Snackbar.LENGTH_LONG)
                            .show();
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayoutWord), "Word Does Not Exist!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        FloatingActionButton floatingAchievements = (FloatingActionButton) findViewById(R.id.floatingAchievementsWord);
        floatingAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int score = pref.getInt("currentScore",0);
                String message = "Current score: " + score;
                Snackbar.make(findViewById(R.id.coordinatorLayoutWord), message, Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        currWord = (TextView) findViewById(R.id.currentWord);
        currValue = (TextView) findViewById(R.id.currentValue);

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

        fm = new FileManager();
        loadDictionary();
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
}
