package org.example.LAB0.CSVParser;

import org.example.LAB0.Comparator;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class CSVParser {
    private final TreeMap<String, WordItem> data = new TreeMap<>();
    private int commonCounter = 0;

    public void parseFile(String inputFileName, String outputFileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStreamReader inputStream = new InputStreamReader(new FileInputStream(classLoader.getResource(inputFileName).getFile()))) {
            int c;
            StringBuilder curWord = new StringBuilder();
            while ((c = inputStream.read()) > 0) {
                if (Character.isLetterOrDigit(c)) curWord.append((char) c);
                else {
                    commonCounter++;
                    changeData(curWord.toString());
                    curWord.delete(0, curWord.length());
                }
            }
            if (!curWord.isEmpty()) {
                changeData(curWord.toString());
            }
        } catch (Exception e) {
            System.err.println("Error while reading file: " + e.getLocalizedMessage());
        }

        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            Comparator comparator = new Comparator(data);
            Map<String, WordItem> sorted = new TreeMap<>(comparator);
            sorted.putAll(data);
            for (WordItem wordItem : sorted.values()) {
                float percent = ((float) wordItem.getCount() / commonCounter);
                writer.println(wordItem.getWord() + "," + wordItem.getCount() + "," + String.format("%.2f", (percent * 100)) + "%");
            }
        } catch (Exception e) {
            System.err.println("Error while writing file: " + e.getLocalizedMessage());
        }
    }

    private void changeData(String word) {
        if (data.containsKey(word)) {
            data.get(word).addOneItem();
        } else {
            data.put(word, new WordItem(word));
        }
    }
}
