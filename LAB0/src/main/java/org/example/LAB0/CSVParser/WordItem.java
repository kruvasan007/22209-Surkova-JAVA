package org.example.LAB0.CSVParser;

public class WordItem {
    private final String word;
    private int count;

    public WordItem(String word) {
        this.word = word;
        this.count = 1;
    }

    public void addOneItem() {
        this.count++;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }
}
