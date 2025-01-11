package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;

public class ControlCenterApp extends JFrame implements Observer {
    private JTextField controlCenterPortField;
    private JTextField basinPortField;
    private JTextField waterDischargeField;
    private DefaultListModel<String> listModel;
    private IControlCenter controlCenter;

    public ControlCenterApp() {
        setTitle("Control Center");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Control Center Port input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Control Center Port:"), gbc);
        gbc.gridx = 1;
        controlCenterPortField = new JTextField("8080");
        add(controlCenterPortField, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Control Center");
        startButton.addActionListener(e -> startControlCenter());
        add(startButton, gbc);

        // List of assigned basins
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Registered Retension Basins:"), gbc);

        JList<String> basinList = new JList<>();
        listModel = new DefaultListModel<>();
        basinList.setModel(listModel);
        JScrollPane listScrollPane = new JScrollPane(basinList);
        listScrollPane.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(listScrollPane, gbc);

        // Basin Port input
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("Basin Port:"), gbc);
        gbc.gridx = 1;
        basinPortField = new JTextField();
        add(basinPortField, gbc);

        // Water Discharge input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Water Discharge (L/s):"), gbc);
        gbc.gridx = 1;
        waterDischargeField = new JTextField();
        add(waterDischargeField, gbc);

        // Set Water Discharge button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton setWaterDischargeButton = new JButton("Set Water Discharge");
        setWaterDischargeButton.addActionListener(e -> setWaterDischarge());
        add(setWaterDischargeButton, gbc);
    }

    private void startControlCenter() {
        int port;
        try {
            port = Integer.parseInt(controlCenterPortField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controlCenter = new ControlCenter("localhost", port);
        controlCenter.addObserver(this);
        controlCenter.start();
    }

    private void setWaterDischarge() {
        int port;
        int waterDischarge;
        try {
            port = Integer.parseInt(basinPortField.getText());
            waterDischarge = Integer.parseInt(waterDischargeField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controlCenter.setWaterDischarge(port, waterDischarge);
    }

    @Override
    public void update(String host, int port, String fillStatus, int waterDischarge) {
        SwingUtilities.invokeLater(() -> {
            String displayText = host + ":" + port + " - Fill: " + fillStatus + "%, Discharge: " + waterDischarge + "L";
            boolean updated = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).startsWith(host + ":" + port)) {
                    listModel.set(i, displayText);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                listModel.addElement(displayText);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ControlCenterApp::new);
    }
}