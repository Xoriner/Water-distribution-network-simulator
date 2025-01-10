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
        setLayout(new GridLayout(8, 2));

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

        // Start button
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
        add(startButton);

        // Filling Percentage label
        add(new JLabel("Filling Percentage:"));
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel);

        // Water Discharge label
        add(new JLabel("Water Discharge:"));
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Parameters");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParameters();
            }
        });
        add(refreshButton);

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