package pl.edu.pwr.mrodak.jp.RetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

public class RetensionBasinApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;
    private JTextField maxVolumeInput;
    private JTextField controlCenterHostInput;
    private JTextField controlCenterPortInput;
    private JLabel fillingPercentageLabel;
    private JLabel waterDischargeLabel;
    private RetensionBasin retensionBasin;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RetensionBasinApp() {
        setTitle("Retension Basin Configuration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Host Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        hostInput = new JTextField("localhost");
        add(hostInput, gbc);

        // Port Input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portInput = new JTextField("8081");
        add(portInput, gbc);

        // Max Volume Input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Max Volume:"), gbc);
        gbc.gridx = 1;
        maxVolumeInput = new JTextField("1000");
        add(maxVolumeInput, gbc);

        // Control Center Host Input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Control Center Host:"), gbc);
        gbc.gridx = 1;
        controlCenterHostInput = new JTextField("localhost");
        add(controlCenterHostInput, gbc);

        // Control Center Port Input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Control Center Port:"), gbc);
        gbc.gridx = 1;
        controlCenterPortInput = new JTextField("8080");
        add(controlCenterPortInput, gbc);

        // Start Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Retension Basin");
        startButton.addActionListener(e -> startRetensionBasin());
        add(startButton, gbc);

        // Filling Percentage Label
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        add(new JLabel("Filling Percentage:"), gbc);
        gbc.gridx = 1;
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel, gbc);

        // Water Discharge Label
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Water Discharge:"), gbc);
        gbc.gridx = 1;
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel, gbc);
    }

    private void startRetensionBasin() {
        try {
            String host = hostInput.getText();
            int port = Integer.parseInt(portInput.getText());
            int maxVolume = Integer.parseInt(maxVolumeInput.getText());
            String controlCenterHost = controlCenterHostInput.getText();
            int controlCenterPort = Integer.parseInt(controlCenterPortInput.getText());

            retensionBasin = new RetensionBasin(maxVolume, host, port, controlCenterHost, controlCenterPort);
            retensionBasin.start();
            updateLabels();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLabels() {
        scheduler.scheduleAtFixedRate(() -> {
            if (retensionBasin != null) {
                fillingPercentageLabel.setText(retensionBasin.getFillingPercentage() + "%");
                waterDischargeLabel.setText(retensionBasin.getWaterDischarge() + " L/s");
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RetensionBasinApp::new);
    }
}