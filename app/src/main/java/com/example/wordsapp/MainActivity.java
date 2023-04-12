package com.example.wordsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Represents the menu.
 */
public class MainActivity extends AppCompatActivity  {

    Button learnButton;
    Button testButton;
    Button exitButton;
    static DataBaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        learnButton = findViewById(R.id.learn_btn);
        testButton = findViewById(R.id.test_btn);
        exitButton = findViewById(R.id.exit_btn);

        databaseHelper = new DataBaseHelper(this);

        databaseHelper.openOrCreate();
        databaseHelper.downloadWordsToLearn();

        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivityToLearn();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivityToTest();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });
    }


    public void switchActivityToLearn() {
        Intent switchActivityIntent = new Intent(this, LearnActivity.class);
        startActivity(switchActivityIntent);
    }

    public void switchActivityToTest() {
        Intent switchActivityIntent = new Intent(this, TestActivity.class);
        startActivity(switchActivityIntent);
    }

    protected void onDestroy(){
        super.onDestroy();
        databaseHelper.closeDatabase();
    }
}