package com.example.languageApp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.example.languageApp.R;

import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private TextView clue;
    private TextView quess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button checkButton = (Button) findViewById(R.id.Button);
        TextView clue = (TextView) findViewById(R.id.clue);
        clue.setTextSize(20);
        clue.setText("Aloita enterillä");


        //drop menu for mode
        Spinner mode = (Spinner) findViewById(R.id.mode);
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.mode));
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mode.setAdapter(modeAdapter);

        //drop menu for class (sanaluokka)
        final Spinner wordType = (Spinner) findViewById(R.id.wordType);
        ArrayAdapter<String> wordTypeAdapter = new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.classes));
        wordTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        wordType.setAdapter(wordTypeAdapter);
        checkButton.setOnClickListener(new View.OnClickListener(){
        int pasi = 8;
        String wordTypeString = "kaikki";
        String modeString = "suomi-espanja";



            public void onClick(View view){

                EditText quess = (EditText) findViewById(R.id.quess);
                TextView clue = (TextView) findViewById(R.id.clue);

                Spinner mode = (Spinner) findViewById(R.id.mode);
                Spinner wordType = (Spinner) findViewById(R.id.wordType);

                //start of the game
                if (clue.getText().toString().matches("Aloita enterillä")){
                    wordTypeString = wordType.getSelectedItem().toString();
                    modeString = mode.getSelectedItem().toString();
                    if(modeString.matches("suomi-espanja")){
                        clue.setText(randomWordPair(wordTypeString, mode.getSelectedItem().toString())[0].split(",")[0]);
                    }
                    else {
                        clue.setText(randomWordPair(wordTypeString, mode.getSelectedItem().toString())[1].split(",")[0]);
                    }
                    quess.setText("");
                }

                //correct answer
                else if (checkAnswer(quess.getText().toString().trim(), clue.getText().toString().trim(), wordTypeString, modeString)){
                    wordTypeString = wordType.getSelectedItem().toString();
                    modeString = mode.getSelectedItem().toString();
                    if(modeString.matches("suomi-espanja")){
                        clue.setText(randomWordPair(wordTypeString, mode.getSelectedItem().toString())[0].split(",")[0]);
                    }
                    else{
                        clue.setText(randomWordPair(wordTypeString, mode.getSelectedItem().toString())[1].split(",")[0]);
                    }
                    quess.setText("");

                }
                //no answer
                else if (quess.getText().toString().trim().matches("")){
                    quess.setText(findWord(clue.getText().toString(), wordTypeString, modeString)[0].trim());
                }
                else
                    quess.setText("");
            }

            //returns the translation of the word
            public String [] findWord(String word, String type, String mode){

                String fileName = type.toLowerCase();

                int resID = getResources().getIdentifier(fileName, "raw", getPackageName());
                InputStream input = getResources().openRawResource(resID);

                Scanner scan = new Scanner(input);

                while (scan.hasNext()){

                    String line = scan.nextLine();

                    String[] pieces = line.split("=");

                    if(mode.matches("suomi-espanja")){
                        if (pieces[0].split(",")[0].trim().matches(word.split(",")[0].trim())){
                            scan.close();
                            return pieces[1].split(",");
                        }
                    }
                    else {
                        if (pieces[1].split(",")[0].trim().matches(word.trim())){
                            scan.close();
                            return pieces[0].split(",");}
                    }
                }
                Log.d("findWord pieces null", "null");
                return null;

            }

            public int dictLength(int dictId){

                InputStream input = getResources().openRawResource(dictId);

                Scanner scan = new Scanner(input);

                int length = 0;

                while (scan.hasNext()){

                    length = length + 1;
                    String line = scan.nextLine();
                }
                return length-1;
            }

            public boolean checkAnswer (String quess, String clue, String type, String mode){

                String [] answers = findWord(clue, type, mode);
                for (int i = 0; i < answers.length; ++i){
                    if(answers[i].toLowerCase().trim().matches(quess.toLowerCase().trim())){
                        return true;
                    }
                }
                return false;

            }


            public String[] randomWordPair(String type, String mode) {

                String classes[] = new String[4];
                classes[0] = "verbi";
                classes[1] = "substantiivi";
                classes[2] = "adjektiivi";
                classes[3] = "muut";

                //get correct text file
                String fileName = type.toLowerCase().trim();

                if (type.toLowerCase().trim().matches("kaikki")){
                    int randomClassInt = (int)Math.floor(Math.random() * 4);
                    fileName = classes[randomClassInt];
                    wordTypeString = classes[randomClassInt];
                }

                int resID = getResources().getIdentifier(fileName, "raw", getPackageName());
                InputStream input = getResources().openRawResource(resID);

                //random number within range of dictionary length
                int wantedNumber = (int)Math.floor(Math.random() * (dictLength(resID)+2));
                if (wantedNumber < 1){
                    wantedNumber = 1;
                }

                int comparisonNumber = 0;

                Scanner scan = new Scanner(input);

                while (scan.hasNext()){
                    comparisonNumber = comparisonNumber + 1;
                    String line = scan.nextLine();

                    if (comparisonNumber == wantedNumber){
                        String[] pieces = line.split("=");
                        return pieces;
                    }
                }
                Log.e("end of loop", "text file");
                return null;
            }
        });
    }



}

