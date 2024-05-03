package org.example;

import org.example.core.Controller;
import org.example.view.View;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var controller = new Controller();
        SwingUtilities.invokeLater(() -> {
            JFrame view = new View(controller);
            view.setSize(860,
                    520);
            view.setResizable(false);
            view.setVisible(true);
        });
    }
}