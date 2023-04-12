package com.example.wordsapp;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for working with database
 * @author Julia Tikki
 */
public class DataBaseHelper {
    private static final String DB_NAME = "w.db";
    private Context mContext;
    private SQLiteDatabase database;

    /**
     * Class constructor
     * @param context Context of the activity
     */
    public DataBaseHelper(Context context) {
        mContext = context;
    }

    /**
     * Creates database or opens it (if it already exists)
     */
    public void openOrCreate() {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        if (!dbFile.exists())
            copyDatabase(dbFile);
        database = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Copies words from table "words" to "wordsToLearn" if the table "wordsToLearn" is empty
     */
    public void downloadWordsToLearn() {
        if (DatabaseUtils.queryNumEntries(database, "wordsToLearn") == 0) {
            database.execSQL("INSERT INTO wordsToLearn (english, russian, sourceId) SELECT english, russian, id FROM words");
        }
    }

    /**
     * Adds two rows into "wordsToTest" table: where the word is english and the translation is russian and vice versa.
     * Deletes the row with the word from "wordsToLearn" table.
     * @param word word essence that was learned
     */
    public void setWordLearned(UnlearnedWord word) {
        int sourceId = word.sourceId;
        addWordToTest(sourceId, 0);
        addWordToTest(sourceId, 1);
        database.execSQL("DELETE FROM wordsToLearn WHERE wordsToLearn.sourceId = ?", new String[]{Integer.toString(sourceId)});
    }

    /**
     * Adds row into the "wordsToTest" table. Sets dateRepeat to current time.
     * @param sourceId the id of word in "words" table
     * @param orderFlag if orderFlag == 0 then the word is english and the translation is russian
     *                  if orderFlag == 1 then the word is russian and the translation is english
     */
    private void addWordToTest(int sourceId, int orderFlag) {
        String insertQuery;
        long currentTime = System.currentTimeMillis() / 1000;
        if (orderFlag == 0) {
            insertQuery = "INSERT INTO wordsToTest (word, translation) " +
                    "SELECT english, russian FROM words WHERE words.id = ?";
        }
        else {
            insertQuery = "INSERT INTO wordsToTest (word, translation) " +
                    "SELECT russian, english FROM words WHERE words.id = ?";
        }
            database.execSQL(insertQuery, new String[] {Integer.toString(sourceId)});
            Cursor c = database.rawQuery("SELECT last_insert_rowid() FROM wordsToTest", null);
            c.moveToFirst();
            int maxId = c.getInt(0);
            database.execSQL("UPDATE wordsToTest SET sourceId = ?, dateRepeat = ? WHERE id = ?",
                    new String[]{Integer.toString(sourceId), Long.toString(currentTime), Integer.toString(maxId)});
    }

    /**
     * gets the cursor for all the words from "wordsToLearn" table.
     * @return the cursor for the unlearned words
     */
    public Cursor getUnlearnedWordsCursor() {
        return database.rawQuery("SELECT * FROM 'wordsToLearn';", null);
    }

    /**
     * gets the cursor for those words from "wordsToTest" table which date for revising is more or equals current time in random order.
     * @return the cursor for the matching words
     */
    public Cursor getLearnedWordsCursor() {
        long currentTime = System.currentTimeMillis() / 1000;
        return database.rawQuery("SELECT * FROM 'wordsToTest' where dateRepeat<? ORDER BY RANDOM()", new String[]{Long.toString(currentTime)});
    }

    /**
     * Deletes rows from "wordsToTest" table that refers to word that was not repeated
     * @param word word essence that was not repeated
     */
    public void setWordUnlearned(LearnedWord word) {
        int sourceId = word.sourceId;
        database.execSQL("DELETE FROM wordsToTest WHERE wordsToTest.sourceId = ?", new String[]{Integer.toString(sourceId)});
        database.execSQL("INSERT INTO wordsToLearn (english, russian, sourceId) SELECT english, russian, id FROM words WHERE words.id=?", new String[]{Integer.toString(sourceId)});
    }

    /**
     * Sets repeatNumber increased by one and date for the next revision accordingly.
     * @see LearnedWord#getRepeatDate() Returns the revision time based on repeat number.
     * @param word word essence that was repeated
     */
    public void changeRepeatInfo(LearnedWord word) {
        word.repeatNumber++;
        int repeatNumber = word.repeatNumber;
        int id = word.id;
        long date = word.getRepeatDate();
        database.execSQL("UPDATE wordsToTest SET repeatNumber = ?, dateRepeat = ? where id = ?", new String[]{Integer.toString(repeatNumber), Long.toString(date), Integer.toString(id)});
    }


    /**
     * Closes database.
     */
    public void closeDatabase() {
        database.close();
    }

    /**
     * Copies the database.
     * @param dbFile path to database
     */
    private void copyDatabase(File dbFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            is = mContext.getAssets().open(DB_NAME);
            os = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            while (is.read(buffer) > 0) {
                os.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
                if (is != null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
