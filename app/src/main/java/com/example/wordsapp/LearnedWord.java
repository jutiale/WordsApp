package com.example.wordsapp;

/**
 * Represents the learned word essence.
 * Has the same fields as table "wordsToTest" and the function for calculating repeat date.
 */
public class LearnedWord {
    public int id;
    public String word;
    public String translation;
    public int sourceId;
    public int repeatNumber;
    public long repeatDate;

    /**
     * Class constructor.
     * @param id id of the word in "wordsToTest" table.
     * @param word word that will be suggested to translate
     * @param translation the translation of the word
     * @param sourceId reference to the word in the "words" table
     * @param repeatNumber number of times the word was translated right
     * @param repeatDate the date (in seconds) when the word should be shown. Calculates in {@link #getRepeatDate()}
     */
    public LearnedWord(int id, String word, String translation, int sourceId, int repeatNumber, long repeatDate) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.sourceId = sourceId;
        this.repeatNumber = repeatNumber;
        this.repeatDate = repeatDate;
    }

    /**
     * Calculates the date (in seconds) when the word should be shown for revision based on repeat number.
     * @see <a href="https://en.wikipedia.org/wiki/Spaced_repetition">Spaced Repetition</a>
     * @return repeat date in seconds
     */
    public long getRepeatDate() {
        long currentTime = System.currentTimeMillis() / 1000;
        if (repeatNumber == 1) {
            return currentTime + 1200;
        }
        if (repeatNumber == 2) {
            return currentTime + 86400;
        }
        if (repeatNumber == 3) {
            return currentTime + 1209600;
        }
        else {
            return currentTime + 5097600;
        }
    }
}
