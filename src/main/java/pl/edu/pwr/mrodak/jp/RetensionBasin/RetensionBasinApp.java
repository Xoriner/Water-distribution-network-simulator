package pl.edu.pwr.mrodak.jp.RetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class RetensionBasinApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;
    private JTextField maxVolumeInput;
    private JTextField controlCenterHostInput;
    private JTextField controlCenterPortInput;
    private JTextField riverSectionHostInput;
    private JTextField riverSectionPortInput;
    private JLabel fillingPercentageLabel;
    private JLabel waterDischargeLabel;
    private IRetensionBasin retensionBasin;

    public RetensionBasinApp() {
        setTitle("Retension Basin Configuration");
        setSize(400, 500);
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

        // Control Center Host input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Control Center Host:"), gbc);
        gbc.gridx = 1;
        controlCenterHostInput = new JTextField("localhost");
        add(controlCenterHostInput, gbc);

        // Control Center Port input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Control Center Port:"), gbc);
        gbc.gridx = 1;
        controlCenterPortInput = new JTextField("8080");
        add(controlCenterPortInput, gbc);

        // River Section Host input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("River Section Host:"), gbc);
        gbc.gridx = 1;
        riverSectionHostInput = new JTextField("localhost");
        add(riverSectionHostInput, gbc);

        // River Section Port input
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("River Section Port:"), gbc);
        gbc.gridx = 1;
        riverSectionPortInput = new JTextField("8082");
        add(riverSectionPortInput, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Retension Basin");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = hostInput.getText();
                int port = Integer.parseInt(portInput.getText());
                int maxVolume = Integer.parseInt(maxVolumeInput.getText());
                String controlCenterHost = controlCenterHostInput.getText();
                int controlCenterPort = Integer.parseInt(controlCenterPortInput.getText());
                String riverSectionHost = riverSectionHostInput.getText();
                int riverSectionPort = Integer.parseInt(riverSectionPortInput.getText());

                Map<Integer, String> incomingRiverSections = new HashMap<>();
                incomingRiverSections.put(riverSectionPort, riverSectionHost);

                retensionBasin = new RetensionBasin(maxVolume, host, port, controlCenterHost, controlCenterPort, incomingRiverSections);
                updateParameters();
            }
        });
        add(startButton, gbc);

        // Filling Percentage label
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        add(new JLabel("Filling Percentage:"), gbc);
        gbc.gridx = 1;
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel, gbc);

        // Water Discharge label
        gbc.gridx = 0;
        gbc.gridy = 9;
        add(new JLabel("Water Discharge:"), gbc);
        gbc.gridx = 1;
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel, gbc);

        // Refresh button
        gbc.gridx = 0;
        gbc.gridy = 10;
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