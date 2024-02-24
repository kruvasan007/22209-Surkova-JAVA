package org.example.Factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    @Test
    void createCommandTest() {
        var comFactory = new CommandFactory();
        comFactory.createCommand("org.example.Factory.Command.Pop");
    }

    @Test
    void createCommandTestException() {
        var comFactory = new CommandFactory();
        comFactory.createCommand("org.example.Factory.Command");
    }
}