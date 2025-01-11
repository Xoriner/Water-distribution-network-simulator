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
    private DefaultListModel<String> listModel;
    private IEnvironment environment;
    private ServerSocket serverSocket;
    private ExecutorService executor;

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
        JButton startButton = new JButton("Start Environment Monitoring");
        startButton.addActionListener(e -> startEnvironmentMonitoring());
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
    }

    private void startEnvironmentMonitoring() {
        int port;
        try {
            port = Integer.parseInt(environmentPortField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        environment = new Environment("localhost", port);
        environment.addObserver(this);
        environment.monitorRiverSections();

        executor = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
            JOptionPane.showMessageDialog(this, "Environment started on port " + port, "Success", JOptionPane.INFORMATION_MESSAGE);

            executor.submit(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);
                    } catch (Exception ex) {
                        if (!serverSocket.isClosed()) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to start environment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //TO-DO: Implement this method
    private void listenForClients() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (Exception ex) {
            if (!serverSocket.isClosed()) {
                ex.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        executor.submit(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request != null && request.startsWith("ars:")) {
                    processRegisterRiverSectionRequest(request, out);
                } else {
                    System.err.println("Unrecognized request: " + request);
                    out.println("0"); // Response code 0 for failure
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
    public void update(String host, int port, String waterQuality, int waterLevel) {
        SwingUtilities.invokeLater(() -> {
            String displayText = host + ":" + port + " - Water Quality: " + waterQuality + ", Water Level: " + waterLevel + "cm";
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