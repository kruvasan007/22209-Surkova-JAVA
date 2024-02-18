import CSVParser.CSVParser;

public class Main {
    public static void main(String[] args) {
        CSVParser csvParser = new CSVParser();
        csvParser.parseFile("input.txt", "output.txt");
    }
}