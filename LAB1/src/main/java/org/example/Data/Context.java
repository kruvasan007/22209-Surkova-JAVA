package org.example.Data;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.TreeMap;

public class Context {
    private final TreeMap<String, Double> params = new TreeMap<>();
    private final Stack<Double> stack = new Stack<>();

    public boolean checkParams(String val) {
        return params.containsKey(val);
    }

    public void push(Double val) {
        stack.push(val);
    }

    public void pushArg(String val) {
        stack.push(params.get(val));
    }

    public Double peek() {
        return stack.peek();
    }

    public Double pop() throws Exception {
        Double value;
        try {
            value = stack.pop();
            return value;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void setParams(String name, Double val) {
        params.put(name, val);
    }

    public Double getParams(String name) {
        return params.get(name);
    }

}
