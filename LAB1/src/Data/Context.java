package Data;

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

    public String pop() {
        var value = stack.pop();
        if (params.get(value) != null) {
            return params.get(value).toString();
        } else
            return value;
    }

    public void setParams(String name, Double val) {
        params.put(name, val);
    }

    public double getParams(String name) {
        return params.get(name);
    }

}
