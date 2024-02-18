package CSVParser;

import org.example.LAB0.Comparator;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class CSVParser {
    private final TreeMap<String, WordItem> data = new TreeMap<>();
    private int commonCounter = 0;

    public void parseFile(String inputFileName, String outputFileName) {
        InputStreamReader inputStream = null;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            File file = new File(classLoader.getResource(inputFileName).getFile());
            inputStream = new InputStreamReader(new FileInputStream(file));
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
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFileName));
            Comparator comparator = new Comparator(data);
            Map<String, WordItem> sorted = new TreeMap<>(comparator);
            sorted.putAll(data);
            for (WordItem wordItem : sorted.values()) {
                float percent = ((float) wordItem.getCount() / commonCounter);
                writer.write(wordItem.getWord() + "," + wordItem.getCount() + "," + String.format("%.2f", (percent * 100)) + "%\n");
            }
        } catch (Exception e) {
            System.err.println("Error while writing file: " + e.getLocalizedMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error while close file: " + e.getLocalizedMessage());
                }
            }
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
