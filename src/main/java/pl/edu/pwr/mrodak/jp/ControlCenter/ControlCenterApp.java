package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlCenterApp extends JFrame {
    private JTextField host;
    private JTextField port;
    private JTextField RetensionBasinHost;
    private JTextField RetensionBasinPort;


    public ControlCenterApp() {
        setTitle("Control Center Configuration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Control Center Configuration
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Control Center Configuration"), gbc);

        // Control Center Host input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Control Center Host:"), gbc);
        gbc.gridx = 1;
        host = new JTextField("localhost");
        add(host, gbc);

        // Control Center Port input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Control Center Port:"), gbc);
        gbc.gridx = 1;
        port = new JTextField("8080");
        add(port, gbc);

        // Retension Basin Host input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        RetensionBasinHost = new JTextField("localhost");
        add(RetensionBasinHost, gbc);

        // Retension Basin Port input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        RetensionBasinPort = new JTextField("8081");
        add(RetensionBasinPort, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Control Center");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = RetensionBasinHost.getText();
                int port = Integer.parseInt(RetensionBasinPort.getText());

                IControlCenter controlCenter = new ControlCenter();
                controlCenter.assignRetensionBasin(port, host);
                controlCenter.monitorBasins();
            }
        });
        add(startButton, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ControlCenterApp();
    }
}