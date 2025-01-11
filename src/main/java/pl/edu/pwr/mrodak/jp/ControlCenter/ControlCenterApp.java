package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlCenterApp extends JFrame {
    private JTextField controlCenterPortField;
    private DefaultListModel<String> listModel;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public ControlCenterApp() {
        setTitle("Control Center");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
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
        gbc.gridwidth = 2;
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

        setVisible(true);
    }

    private void startControlCenter() {
        int port;
        try {
            port = Integer.parseInt(controlCenterPortField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        executor = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
            JOptionPane.showMessageDialog(this, "Control Center started on port " + port, "Success", JOptionPane.INFORMATION_MESSAGE);

            // Start listening for connections
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
            JOptionPane.showMessageDialog(this, "Failed to start server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClient(Socket clientSocket) {
        executor.submit(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request.startsWith("arb:")) { // Register Retension Basin
                    String[] parts = request.substring(4).split(",");
                    if (parts.length == 2) {
                        int port;
                        try {
                            port = Integer.parseInt(parts[0].trim());
                        } catch (NumberFormatException ex) {
                            out.println("0"); // Response code 0 for failure
                            System.err.println("Invalid port format: " + parts[0]);
                            return;
                        }

                        String host = parts[1].trim();
                        String basin = host + ":" + port;

                        if (!listModel.contains(basin)) {
                            listModel.addElement(basin); // Add basin to GUI list
                        }

                        // Respond to confirm registration
                        out.println("1"); // Response code 1 for success
                    } else {
                        out.println("0"); // Response code 0 for failure
                        System.err.println("Invalid registration format: " + request);
                    }
                } else {
                    System.err.println("Unrecognized request: " + request);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ControlCenterApp::new);
    }
}
