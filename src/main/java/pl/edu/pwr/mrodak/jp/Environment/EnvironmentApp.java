package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.Observer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnvironmentApp extends JFrame implements Observer {
    private JTextField environmentPortField;
    private JTextField riverPortField;
    private JTextField riverRainFallField;
    private DefaultListModel<String> listModel;
    private IEnvironment environment;

    public EnvironmentApp() {
        setTitle("Environment Monitoring");
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

        // Environment Port input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Environment Port:"), gbc);
        gbc.gridx = 1;
        environmentPortField = new JTextField("8080");
        add(environmentPortField, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Environment");
        startButton.addActionListener(e -> startEnvironment());
        add(startButton, gbc);

        // List of monitored sections
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Monitored River Sections:"), gbc);

        JList<String> sectionList = new JList<>();
        listModel = new DefaultListModel<>();
        sectionList.setModel(listModel);
        JScrollPane listScrollPane = new JScrollPane(sectionList);
        listScrollPane.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(listScrollPane, gbc);

        // River Port input
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("River Port:"), gbc);
        gbc.gridx = 1;
        riverPortField = new JTextField();
        add(riverPortField, gbc);

        // River rainFall input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Rainfall (m3):"), gbc);
        gbc.gridx = 1;
        riverRainFallField = new JTextField();
        add(riverRainFallField, gbc);

        // Set Water Discharge button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton setRainFall = new JButton("Set Rain Fall");
        setRainFall.addActionListener(e -> setRainFall());
        add(setRainFall, gbc);

    }

    private void startEnvironment() {
        int port;
        try {
            port = Integer.parseInt(environmentPortField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        environment = new Environment("localhost", port);
        environment.addObserver(this);
        environment.start();
    }

    private void setRainFall() {
        int port;
        int rainFall;
        try {
            port = Integer.parseInt(riverPortField.getText());
            rainFall = Integer.parseInt(riverRainFallField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        environment.setRainFall(port, rainFall);
    }


    //Get rid of this probably
    private void processRegisterRiverSectionRequest(String request, PrintWriter out) {
        String[] parts = request.substring(4).split(",");
        if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();
                String basin = host + ":" + port;

                //TO-DO: FIX THE ERROR WITH CONTAINS
                if (!listModel.contains(basin)) {
                    listModel.addElement(basin); // Add basin to GUI list
                    environment.assignRiverSection(port, host); // Add basin to Control Center
                }
                out.println("1"); // Response code 1 for success
            } catch (NumberFormatException ex) {
                out.println("0"); // Response code 0 for failure
                System.err.println("Invalid port format: " + parts[0]);
            }
        } else {
            out.println("0"); // Response code 0 for failure
            System.err.println("Invalid registration format: " + request);
        }
    }

    @Override
    public void update(String host, int port, String stringInfo, int rainFall) {
        SwingUtilities.invokeLater(() -> {
            String displayText = host + ":" + port + " - Rainfall: " + rainFall;
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
        SwingUtilities.invokeLater(EnvironmentApp::new);
    }
}