package com.example.wordsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Represents the learning mode.
 */
public class LearnActivity extends AppCompatActivity {

    Cursor cursor;
    TextView englishWordView;
    TextView russianWordView;
    Button nextButton;
    Button menuButton;
    UnlearnedWord word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        cursor = MainActivity.databaseHelper.getUnlearnedWordsCursor();

        englishWordView = findViewById(R.id.english_word);
        russianWordView = findViewById(R.id.russian_word);
        nextButton = findViewById(R.id.next_btn);
        menuButton = findViewById(R.id.menu_btn);

        setNextWord();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.databaseHelper.setWordLearned(word);
                setNextWord();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Gets the next word in cursor and shows english and russian variants on the screen.
     * If the words are over, the function showDialog is called.
     * @see #showDialog() Shows the window saying that the words are over.
     */
    private void setNextWord() {
        if (cursor.moveToNext()) {
            word = new UnlearnedWord(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3));
            String english = word.english;
            String russian = word.russian;
            englishWordView.setText(english);
            russianWordView.setText(russian);
        } else {
            showDialog();
        }
    }

    /**
     * Shows the window saying that the words are over.
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Слова закончились!");
        builder.setMessage("Вы выучили все доступные слова. Попробуйте проверить свои знания!");
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            cursor.close();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
    }

}