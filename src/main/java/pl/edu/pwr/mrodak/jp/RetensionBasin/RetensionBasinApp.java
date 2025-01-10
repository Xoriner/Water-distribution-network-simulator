package pl.edu.pwr.mrodak.jp.RetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RetensionBasinApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;
    private JTextField maxVolumeInput;
    private JTextField ccHostInput;
    private JTextField ccPortInput;

    public RetensionBasinApp() {
        setTitle("Retension Basin Configuration");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        // Host input
        add(new JLabel("Host:"));
        hostInput = new JTextField("localhost");
        add(hostInput);

        // Port input
        add(new JLabel("Port:"));
        portInput = new JTextField("8081");
        add(portInput);

        // Max Volume input
        add(new JLabel("Max Volume:"));
        maxVolumeInput = new JTextField("1000");
        add(maxVolumeInput);

        // Control Center Host input
        add(new JLabel("Control Center Host:"));
        ccHostInput = new JTextField("localhost");
        add(ccHostInput);

        // Control Center Port input
        add(new JLabel("Control Center Port:"));
        ccPortInput = new JTextField("9090");
        add(ccPortInput);

        // Start button
        JButton startButton = new JButton("Start Retension Basin");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostInput.getText();
                int port = Integer.parseInt(portInput.getText());
                int maxVolume = Integer.parseInt(maxVolumeInput.getText());
                String ccHost = ccHostInput.getText();
                int ccPort = Integer.parseInt(ccPortInput.getText());

                new RetensionBasin(maxVolume, port, ccHost, ccPort);
            }
        });
        add(startButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new RetensionBasinApp();
    }
}