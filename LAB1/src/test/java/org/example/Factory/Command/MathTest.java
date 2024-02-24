package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Factory.Command.*;
import org.example.Factory.CommandFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EmptyStackException;

class MathTest {
    static private Context context;

    @Test
    void min() throws Exception {
        System.out.println("======TEST MIN EXECUTED=======");
        context = new Context();
        var comm = new Min();
        var a = new Argument("MIN");
        context.push("5");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("1.0", context.peek());
    }

    @Test
    void plus() {
        System.out.println("======TEST PLUS EXECUTED=======");
        context = new Context();
        var comm = new Plus();
        var a = new Argument("PLUS");
        context.push("5");
        Assertions.assertThrows(Exception.class, ()->comm.doOperation(context, a));
    }

    @Test
    void plusWithException() throws Exception {
        System.out.println("======TEST PLUS EXECUTED=======");
        context = new Context();
        var comm = new Plus();
        var a = new Argument("PLUS");
        context.push("5");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("9.0", context.peek());
    }

    @Test
    void minWithException() {
        System.out.println("======TEST MIN v2 EXECUTED=======");
        context = new Context();
        var comm = new Min();
        var a = new Argument("MIN");
        context.push("5");
        Assertions.assertThrows(Exception.class, () ->
                comm.doOperation(context, a));
    }

    @Test
    void div() throws Exception {
        System.out.println("======TEST DIV EXECUTED=======");
        context = new Context();
        var comm = new Div();
        var a = new Argument("DIV");
        context.push("8");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("2.0", context.peek());
    }



    @Test
    void mult() throws Exception {
        System.out.println("======TEST MULT EXECUTED=======");
        context = new Context();
        var comm = new Mult();
        var a = new Argument("MULT");
        context.push("5");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("20.0", context.peek());
    }

    @Test
    void sqrt() throws Exception {
        System.out.println("======TEST SQRT EXECUTED=======");
        context = new Context();
        var comm = new Sqrt();
        var a = new Argument("SQRT");
        context.push("4");
        comm.doOperation(context, a);
        Assertions.assertEquals("2.0", context.peek());
    }

    @Test
    void sqrtWIthException() {
        System.out.println("======TEST SQRT v2 EXECUTED=======");
        context = new Context();
        var comm = new Sqrt();
        var a = new Argument("SQRT");
        Assertions.assertThrows(Exception.class, () ->
                comm.doOperation(context, a));
    }
}