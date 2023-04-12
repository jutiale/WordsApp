package com.example.wordsapp;

/**
 * Represents the unlearned word essence.
 * Has the same fields as table "wordsToLearn"
 */
public class UnlearnedWord {
        public int id;
        public String english;
        public String russian;
        public int sourceId;

    /**
     * Class constructor.
     * @param id id of the word in "wordsToLearn" table
     * @param english english variant of the word
     * @param russian russian variant of the word
     * @param sourceId reference to the word in the "words" table
     */
        public UnlearnedWord(int id, String english, String russian, int sourceId) {
            this.id = id;
            this.english = english;
            this.russian = russian;
            this.sourceId = sourceId;
        }
}
