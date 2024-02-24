package org.example.Data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    @Test
    void argumentGetCommand() {
        var arg = new Argument("PUSH");
        Assertions.assertEquals("PUSH", arg.getCommand());
    }

    @Test
    void argumentSetArgs() {
        var arg = new Argument("PUSH");
        arg.setArgs("0");
        Assertions.assertEquals("0", arg.getArgs(0));
    }

    @Test
    void argumentSize() {
        var arg = new Argument("PUSH");
        arg.setArgs("0");
        Assertions.assertEquals(1, arg.getSize());
    }

    @Test
    void contextGetterSetter() {
        var context = new Context();
        context.setParams("a", 4.0);
        Assertions.assertEquals(4.0, context.getParams("a"));
    }

    @Test
    void contextPushPop() throws Exception {
        var context = new Context();
        context.push("4");
        Assertions.assertEquals("4", context.pop());
    }

    @Test
    void contextPeek() {
        var context = new Context();
        context.push("4");
        Assertions.assertEquals("4", context.peek());
    }

    @Test
    void contextPushPopParams() throws Exception {
        var context = new Context();
        context.setParams("a", 4.0);
        context.push("a");
        Assertions.assertEquals("4.0", context.pop());
    }

    @Test
    void contextPushPopWithException() {
        var context = new Context();
        Assertions.assertThrows(Exception.class, () -> context.pop());
    }

}
