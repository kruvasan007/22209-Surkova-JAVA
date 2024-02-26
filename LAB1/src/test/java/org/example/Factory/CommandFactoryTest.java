package org.example.Factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    @Test
    void createCommandTest() {
        var comFactory = new CommandFactory("C:\\Users\\Anastasia\\IdeaProjects\\Calculator\\22209-Surkova-JAVA\\LAB1\\src\\main\\resources\\config.config");
        comFactory.createCommand("org.example.Factory.Command.Pop");
    }

    @Test
    void createCommandTestException() {
        var comFactory = new CommandFactory("C:\\Users\\Anastasia\\IdeaProjects\\Calculator\\22209-Surkova-JAVA\\LAB1\\src\\main\\resources\\config.config");
        comFactory.createCommand("org.example.Factory.Command");
    }
}