package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.RetensionBasin.RetensionBasinApp;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RiverSectionApp  extends JFrame {
    private JTextField delayInput;
    private JTextField portInput;
    private JTextField envHostInput;
    private JTextField envPortInput;
    private JTextField basinHostInput;
    private JTextField basinPortInput;
    private RiverSection riverSection;

    public RiverSectionApp() {
        setTitle("River Section Configuration");
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

        //Delay Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Delay:"), gbc);
        gbc.gridx = 1;
        delayInput = new JTextField("1000");
        add(delayInput, gbc);

        //Port Input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portInput = new JTextField("8080");
        add(portInput, gbc);

        //Environment Host Input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Environment Host:"), gbc);
        gbc.gridx = 1;
        envHostInput = new JTextField("localhost");
        add(envHostInput, gbc);

        //Environment Port Input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Environment Port:"), gbc);
        gbc.gridx = 1;
        envPortInput = new JTextField("8081");
        add(envPortInput, gbc);

        //Basin Host Input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Basin Host:"), gbc);
        gbc.gridx = 1;
        basinHostInput = new JTextField("localhost");
        add(basinHostInput, gbc);

        // Start Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start River Section");
        startButton.addActionListener(e -> startRiverSection());
        add(startButton, gbc);
    }

    private void startRiverSection() {
        try {
            int delay = Integer.parseInt(delayInput.getText());
            int port = Integer.parseInt(portInput.getText());
            String envHost = envHostInput.getText();
            int envPort = Integer.parseInt(envPortInput.getText());
            String basinHost = basinHostInput.getText();
            int basinPort = Integer.parseInt(basinPortInput.getText());

            riverSection = new RiverSection(port);
            riverSection.assignRetensionBasin(basinPort, basinHost);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RiverSectionApp::new);
    }
}
