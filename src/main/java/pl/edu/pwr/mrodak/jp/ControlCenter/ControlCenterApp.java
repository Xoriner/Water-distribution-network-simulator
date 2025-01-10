package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlCenterApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;

    public ControlCenterApp() {
        setTitle("Control Center Configuration");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Host input
        add(new JLabel("Host:"));
        hostInput = new JTextField("localhost");
        add(hostInput);

        // Port input
        add(new JLabel("Port:"));
        portInput = new JTextField("8081");
        add(portInput);

        // Start button
        JButton startButton = new JButton("Start Control Center");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostInput.getText();
                int port = Integer.parseInt(portInput.getText());

                IControlCenter controlCenter = new ControlCenter();
                controlCenter.assignRetensionBasin(port, host);
                controlCenter.monitorBasins();
            }
        });
        add(startButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ControlCenterApp();
    }
}