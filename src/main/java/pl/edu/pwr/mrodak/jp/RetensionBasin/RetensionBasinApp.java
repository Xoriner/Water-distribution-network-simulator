package pl.edu.pwr.mrodak.jp.RetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RetensionBasinApp extends JFrame {
    private JTextField hostInput;
    private JTextField portInput;
    private JTextField maxVolumeInput;
    private JTextField controlCenterHostInput;
    private JTextField controlCenterPortInput;
    private JTextField incomingRiverSectionAmountInput;
    private JLabel fillingPercentageLabel;
    private JLabel waterDischargeLabel;
    private IRetensionBasin retensionBasin;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<JTextField> incomingRiverSectionHostInputs = new ArrayList<>();
    private List<JTextField> incomingRiverSectionPortInputs = new ArrayList<>();

    public RetensionBasinApp() {
        setTitle("Retension Basin Configuration");
        setSize(600, 600);
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

        // Incoming River Section Amount Input
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(new JLabel("Incoming River Section Amount:"), gbc);
        gbc.gridx = 3;
        incomingRiverSectionAmountInput = new JTextField("1");
        add(incomingRiverSectionAmountInput, gbc);

        // Add button to generate inputs for incoming river sections
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton generateInputsButton = new JButton("Generate Inputs");
        generateInputsButton.addActionListener(e -> generateIncomingRiverSectionInputs(gbc));
        add(generateInputsButton, gbc);

        // Start Button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Retension Basin");
        startButton.addActionListener(e -> startRetensionBasin());
        add(startButton, gbc);

        // Filling Percentage Label
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        add(new JLabel("Filling Percentage:"), gbc);
        gbc.gridx = 1;
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel, gbc);

        // Water Discharge Label
        gbc.gridx = 0;
        gbc.gridy = 10;
        add(new JLabel("Water Discharge:"), gbc);
        gbc.gridx = 1;
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel, gbc);
    }

    private void generateIncomingRiverSectionInputs(GridBagConstraints gbc) {
        try {
            // Remove existing inputs
            Component[] components = getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JTextField && component != hostInput && component != portInput && component != maxVolumeInput && component != controlCenterHostInput && component != controlCenterPortInput && component != incomingRiverSectionAmountInput) {
                    remove(component);
                }
            }
            incomingRiverSectionHostInputs.clear();
            incomingRiverSectionPortInputs.clear();

            int amount = Integer.parseInt(incomingRiverSectionAmountInput.getText());
            for (int i = 0; i < amount; i++) {
                gbc.gridx = 2;
                gbc.gridy = 2 + i * 2;
                add(new JLabel("Incoming River Section Host " + (i + 1) + ":"), gbc);
                gbc.gridx = 3;
                JTextField hostInput = new JTextField("localhost");
                incomingRiverSectionHostInputs.add(hostInput);
                add(hostInput, gbc);

                gbc.gridx = 2;
                gbc.gridy = 3 + i * 2;
                add(new JLabel("Incoming River Section Port " + (i + 1) + ":"), gbc);
                gbc.gridx = 3;
                JTextField portInput = new JTextField("8082");
                incomingRiverSectionPortInputs.add(portInput);
                add(portInput, gbc);
            }

            revalidate();
            repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readIncomingRiverSectionInputs() {
        try {
            for (int i = 0; i < incomingRiverSectionHostInputs.size(); i++) {
                String riverHost = incomingRiverSectionHostInputs.get(i).getText();
                int riverPort = Integer.parseInt(incomingRiverSectionPortInputs.get(i).getText());
                retensionBasin.addIncomingRiverSection(riverHost, riverPort);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for incoming river sections. Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startRetensionBasin() {
        try {
            String host = hostInput.getText();
            int port = Integer.parseInt(portInput.getText());
            int maxVolume = Integer.parseInt(maxVolumeInput.getText());
            String controlCenterHost = controlCenterHostInput.getText();
            int controlCenterPort = Integer.parseInt(controlCenterPortInput.getText());

            retensionBasin = new RetensionBasin(maxVolume, host, port, controlCenterHost, controlCenterPort);

            // Read inputs for incoming river sections
            readIncomingRiverSectionInputs();

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