package pl.edu.pwr.mrodak.jp.ControlCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlCenterApp extends JFrame {
    private JTextField ControlCenterHost;
    private JTextField ControlCenterPort;
    private JTextField RetensionBasinHost;
    private JTextField RetensionBasinPort;
    private DefaultListModel<String> listModel;

    private IControlCenter controlCenter;

    public ControlCenterApp() {
        setTitle("Control Center Configuration");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Control Center Configuration
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Control Center Configuration"), gbc);

        // Control Center Host input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Control Center Host:"), gbc);
        gbc.gridx = 1;
        ControlCenterHost = new JTextField("localhost");
        add(ControlCenterHost, gbc);

        // Control Center Port input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Control Center Port:"), gbc);
        gbc.gridx = 1;
        ControlCenterPort = new JTextField("8080");
        add(ControlCenterPort, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Control Center");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = ControlCenterHost.getText();
                int port = Integer.parseInt(ControlCenterPort.getText());
                controlCenter = new ControlCenter(host, port);
            }
        });
        add(startButton, gbc);

        // Retension Basin Configuration
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(new JLabel("Retension Basin Assignment"), gbc);

        // Retension Basin Host input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        RetensionBasinHost = new JTextField("localhost");
        add(RetensionBasinHost, gbc);

        // Retension Basin Port input
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        RetensionBasinPort = new JTextField("8081");
        add(RetensionBasinPort, gbc);

        // List of assigned basins
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        add(new JLabel("Assigned Retension Basins:"), gbc);

        JList<String> basinList = new JList<>();
        listModel = new DefaultListModel<>(); // Initialize listModel
        basinList.setModel(listModel);
        JScrollPane listScrollPane = new JScrollPane(basinList);
        listScrollPane.setPreferredSize(new Dimension(350, 200));
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(listScrollPane, gbc);

        // Assign button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton assignButton = new JButton("Assign Retension Basin");
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = RetensionBasinHost.getText();
                int port = Integer.parseInt(RetensionBasinPort.getText());
                String basin = new StringBuilder().append(host).append(":").append(port).toString();

                if (controlCenter != null && !listModel.contains(basin)) {
                    controlCenter.assignRetensionBasin(port, host);
                    listModel.addElement(basin);
                }
            }
        });
        add(assignButton, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ControlCenterApp();
    }
}