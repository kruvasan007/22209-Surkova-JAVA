package org.example.Parser;

import org.example.CalcIterator;
import org.example.Data.Argument;
import org.example.Debug.ColorLogger;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private final Reader reader = new Reader();

    public void create() {
        reader.createInstance();
    }

    public void create(String filePath) {
        reader.createInstance(filePath);
    }


    public Argument getNextArg() {
        String curStr = reader.getNextLine();
        Argument arg = null;
        if (curStr != null) {
            arg = editLine(curStr);
        }
        return arg;
    }

    private Argument editLine(String curStr) {
        if (!curStr.equals("EXIT")) {
            var str = curStr.split(" ");
            var list = new ArrayList<>(Arrays.stream(str).toList());
            var comm = list.getFirst();
            Argument argument = new Argument(comm);
            for (int i = 1; i < list.size(); ++i)
                argument.setArgs(list.get(i));
            return argument;
        } else {
            return null;
        }
    }
}
