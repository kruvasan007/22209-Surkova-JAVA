package org.example.view;

import org.example.core.Controller;
import org.example.util.Observer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowEvent;

public class View extends JFrame implements Observer {
    private final Controller controller;
    private final ImageIcon icon;
    private int commonWidthProgressBar;
    private int currentWidthProgressBar = 0;
    private String alivePeersText;

    public View(Controller ctrl) {
        controller = ctrl;
        icon = new ImageIcon("src/main/resources/bkg.jpg");
        createWelcomeView();
    }

    private void createWelcomeView() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                super.paintComponent(g);
            }
        };
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setPreferredSize(new Dimension(500, 500));
        JButton getFileButton = setFileButton(panel);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 100));
        panel.add(getFileButton);
        this.add(panel);
    }

    private JButton setFileButton(JPanel panel) {
        JButton getFileButton = new JButton("Выбрать файл");
        getFileButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "torrent", "torrent");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                controller.startTorrentProcess(chooser.getSelectedFile().getAbsolutePath());
                createMainView();
                panel.setVisible(false);
            }
        });
        return getFileButton;
    }

    private void createMainView() {
        //controller.startTorrentProcess("C:\\Users\\Anastasia\\Downloads\\Торгашева Юлия - Учимся создавать игры на Scratch. Программирование для детей [2018, PDF, RUS] [rutracker-5690209].torrent");
        JLabel torrentName = new JLabel(controller.getTorrentFileName());
        JLabel alivePeers = new JLabel("Alive peers now: " + alivePeersText);
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            controller.stop();
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        controller.setObserver(this);
        JPanel panel = createMainPanel(alivePeers);
        panel.add(torrentName);
        panel.add(alivePeers);
        panel.add(stopButton);
        this.add(panel, BorderLayout.PAGE_START);
    }

    private JPanel createMainPanel(JLabel alivePeers) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawProgressBar(g);
                alivePeers.setText("Alive peers now: " + alivePeersText);
            }
        };
        panel.setSize(getWidth(), getHeight());
        GridLayout gridLayout = new GridLayout(2, 1, 10, 10);
        panel.setLayout(gridLayout);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 100, 30));
        return panel;
    }

    private void drawProgressBar(Graphics g) {
        int commonHeightProgressBar = 60;
        commonWidthProgressBar = getWidth() - 100;
        g.drawRect(30, 100, commonWidthProgressBar, commonHeightProgressBar);
        g.setColor(Color.green);
        g.fillRect(30, 100, currentWidthProgressBar, commonHeightProgressBar);
    }

    @Override
    public void onNotified() {
        currentWidthProgressBar = (int) (commonWidthProgressBar * controller.getCurrentProgress());
        alivePeersText = controller.getAlivePeers();
        repaint();
    }
}
