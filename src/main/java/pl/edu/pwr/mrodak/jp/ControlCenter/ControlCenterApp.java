package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;

public class ControlCenterApp extends JFrame implements Observer {
    private JTextField controlCenterPortField;
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