package com.example.wordsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Represents the testing mode.
 */
public class TestActivity extends AppCompatActivity {

    Cursor cursor;
    TextView toTranslateView;
    EditText usersAnswer;
    Button checkButton;
    Button dontRememberButton;
    Button menuButton;
    String toTranslate;
    String rightAnswer;
    LearnedWord word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        toTranslateView = findViewById(R.id.word_to_translate);
        usersAnswer = findViewById(R.id.users_answer);
        checkButton = findViewById(R.id.check_btn);
        dontRememberButton = findViewById(R.id.dont_remember_btn);
        menuButton = findViewById(R.id.menu_btn);


        cursor = MainActivity.databaseHelper.getLearnedWordsCursor();


        setNextWord();

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerGot = usersAnswer.getText().toString();
                rightAnswer = word.translation;
                if (answerGot.equals(rightAnswer)) {
                    MainActivity.databaseHelper.changeRepeatInfo(word);
                    showDialog("Верно!", "Вы дали правильный ответ", 1);
                } else {
                    showDialog("Неверно!", "Вы дали неправильный ответ", 0);
                }
            }
        });


        dontRememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.databaseHelper.setWordUnlearned(word);
                cursor = MainActivity.databaseHelper.getLearnedWordsCursor();
                showDialog("Правильный ответ:", word.translation, 1);
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
     * Gets the next word in cursor and shows the "word" field.
     * If the words are over, the function showDialog with flag = 0 is called.
     * @see #showDialog(String, String, int) shows the window with title and message.
     */
    private void setNextWord() {
        if (cursor.moveToNext()) {
            word = new LearnedWord(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getLong(5));
            toTranslate = word.word;
            toTranslateView.setText(toTranslate);
        } else {
            showDialog("Слова закончились!",
                    "Вы повторили все выученные слова. Попробуйте запомнить новые!", 0);
        }
    }

    /**
     * Shows the window with title and message.
     * @param title the title of window
     * @param message the message of window
     * @param flag if flag == 0 then after clicking the positive button the word stays the same
     *             if flag == 1 then after clicking the positive button function setNextWord is called
     */
    private void showDialog(String title, String message, int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
            usersAnswer.setText("");
            if (flag == 1)
                setNextWord();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
    }
}