package org.example.Data;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.TreeMap;

public class Context {
    private final TreeMap<String, Double> params = new TreeMap<>();
    private final Stack<String> stack = new Stack<>();

    public void push(String val) {
        stack.push(val);
    }

    public String peek() {
        return stack.peek();
    }

    public String pop() throws Exception{
        String value;
        try {
            value = stack.pop();
            if (params.get(value) != null) {
                value = params.get(value).toString();
            }
            return value;
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void setParams(String name, Double val) {
        params.put(name, val);
    }

    public double getParams(String name) {
        return params.get(name);
    }

}
