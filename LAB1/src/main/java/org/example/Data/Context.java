package org.example.Data;

import org.example.Debug.ColorLogger;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.TreeMap;

public class Context {
    private ColorLogger log = new ColorLogger();
    private final TreeMap<String, Double> params = new TreeMap<>();
    private final Stack<Double> stack = new Stack<>();

    public boolean checkParams(String val) {
        return params.containsKey(val);
    }

    public void push(Double val) {
        stack.push(val);
    }

    public void pushArg(String val) {
        if (params.get(val) != null)
            stack.push(params.get(val));
        else {
            log.logError("No such argument. Push 0");
            stack.push(0.0);
        }
    }

    public Double peek() {
        return stack.peek();
    }

    public Double pop() throws EmptyStackException {
        Double value;
        try {
            value = stack.pop();
            return value;
        } catch (EmptyStackException e) {
            throw new EmptyStackException();
        }
    }

    public void setParams(String name, Double val) {
        params.put(name, val);
    }

    public Double getParams(String name) {
        return params.get(name);
    }

}
