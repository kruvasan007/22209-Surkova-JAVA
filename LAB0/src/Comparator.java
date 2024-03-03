import CSVParser.WordItem;

import java.util.TreeMap;

public class Comparator implements java.util.Comparator<String> {

    private final TreeMap<String, WordItem> map;

    public Comparator(final TreeMap<String, WordItem> map) {
        this.map = map;
    }

    @Override
    public int compare(String o1, String o2) {
        boolean comp = map.get(o1).getCount() >= map.get(o2).getCount();
        if (comp)
            return -1;
        else
            return 1;
    }
}

