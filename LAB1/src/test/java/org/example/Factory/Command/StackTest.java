package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

class StackTest {
    static private Context context;

    @BeforeAll
    static void setup() {
        context = new Context();
    }

    @Test
    void push() throws Exception {
        System.out.println("======TEST PUSH EXECUTED=======");
        var comm = new Push();
        var a = new Argument("PUSH");
        a.setArgs("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("4", context.peek());
    }

    @Test
    void print() {
        System.out.println("======TEST PRINT EXECUTED=======");
        var comm = new Print();
        var a = new Argument("PRINT");
        context.push("4");
        Assertions.assertDoesNotThrow(() ->
                comm.doOperation(context, a));
    }

    @Test
    void pushException() {
        System.out.println("======TEST PUSH EXECUTED=======");
        var comm = new Push();
        var a = new Argument("PUSH");
        Assertions.assertThrows(Exception.class, () -> comm.doOperation(context, a));
    }

    @Test
    void pop() {
        System.out.println("======TEST POP EXECUTED=======");
        var comm = new Pop();
        var a = new Argument("POP");
        Assertions.assertThrows(EmptyStackException.class, () -> comm.doOperation(context, a));
    }

    @Test
    void popWithArguments() {
        System.out.println("======TEST POP v2 EXECUTED=======");
        var comm = new Pop();
        var a = new Argument("POP");
        a.setArgs("a");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals(4.0, context.getParams("a"));
    }

    @Test
    void define() throws Exception {
        System.out.println("======TEST DEFINE EXECUTED=======");
        var comm = new Define();
        var a = new Argument("DEFINE");
        a.setArgs("a");
        a.setArgs("4");
        comm.doOperation(context, a);
        Assertions.assertEquals(4.0, context.getParams("a"));
    }

    @Test
    void defineWithException() {
        System.out.println("======TEST DEFINE v2 EXECUTED=======");
        var comm = new Define();
        var a = new Argument("DEFINE");
        Assertions.assertThrows(Exception.class, () -> comm.doOperation(context, a));
    }
}
