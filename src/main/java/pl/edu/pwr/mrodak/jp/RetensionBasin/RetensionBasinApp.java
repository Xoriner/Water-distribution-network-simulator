package pl.edu.pwr.mrodak.jp.RetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RetensionBasinApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;
    private JTextField maxVolumeInput;
    private JLabel fillingPercentageLabel;
    private JLabel waterDischargeLabel;
    private IRetensionBasin retensionBasin;

    public RetensionBasinApp() {
        setTitle("Retension Basin Configuration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Host input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        hostInput = new JTextField("localhost");
        add(hostInput, gbc);

        // Port input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portInput = new JTextField("8081");
        add(portInput, gbc);

        // Max Volume input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Max Volume:"), gbc);
        gbc.gridx = 1;
        maxVolumeInput = new JTextField("1000");
        add(maxVolumeInput, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Retension Basin");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostInput.getText();
                int port = Integer.parseInt(portInput.getText());
                int maxVolume = Integer.parseInt(maxVolumeInput.getText());

                retensionBasin = new RetensionBasin(maxVolume, port);
                updateParameters();
            }
        });
        add(startButton, gbc);


        // Filling Percentage label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(new JLabel("Filling Percentage:"), gbc);
        gbc.gridx = 1;
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel, gbc);

        // Water Discharge label
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Water Discharge:"), gbc);
        gbc.gridx = 1;
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel, gbc);

        // Refresh button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton refreshButton = new JButton("Refresh Parameters");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParameters();
            }
        });
        add(refreshButton, gbc);

        setVisible(true);
    }

    private void updateParameters() {
        if (retensionBasin != null) {
            fillingPercentageLabel.setText(retensionBasin.getFillingPercentage() + "%");
            waterDischargeLabel.setText(retensionBasin.getWaterDischarge() + " L/s");
        }
    }

    public static void main(String[] args) {
        new RetensionBasinApp();
    }
}