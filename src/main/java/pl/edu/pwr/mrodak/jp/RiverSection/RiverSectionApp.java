package pl.edu.pwr.mrodak.jp.RiverSection;

import pl.edu.pwr.mrodak.jp.Observer;

import javax.swing.*;
import java.awt.*;

public class RiverSectionApp  extends JFrame implements Observer {
    private JTextField delayInput;
    private JTextField portInput;
    private JTextField envHostInput;
    private JTextField envPortInput;
    private JTextField basinHostInput;
    private JTextField basinPortInput;
    private IRiverSection riverSection;
    private JLabel outputBasinHostLabel;
    private JLabel outputBasinPortLabel;

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
        add(new JLabel("Input Basin Host:"), gbc);
        gbc.gridx = 1;
        basinHostInput = new JTextField("localhost");
        add(basinHostInput, gbc);

        //Basin Port Input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Input Basin Port:"), gbc);
        gbc.gridx = 1;
        basinPortInput = new JTextField("8082");
        add(basinPortInput, gbc);

        // Start Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start River Section");
        startButton.addActionListener(e -> startRiverSection());
        add(startButton, gbc);

        // Output Basin Host Label
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Output Basin Host:"), gbc);
        gbc.gridx = 1;
        outputBasinHostLabel = new JLabel("N/A");
        add(outputBasinHostLabel, gbc);

        // Output Basin Port Label
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Output Basin Port:"), gbc);
        gbc.gridx = 1;
        outputBasinPortLabel = new JLabel("N/A");
        add(outputBasinPortLabel, gbc);
    }

    private void startRiverSection() {
        try {
            int delay = Integer.parseInt(delayInput.getText());
            int port = Integer.parseInt(portInput.getText());
            String envHost = envHostInput.getText();
            int envPort = Integer.parseInt(envPortInput.getText());
            String inputBasinHost = basinHostInput.getText();
            int inputBasinPort = Integer.parseInt(basinPortInput.getText());

            riverSection = new RiverSection(delay, port, envHost, envPort, inputBasinHost, inputBasinPort);
            riverSection.addObserver(this);
            riverSection.start();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(String host, int port, String stringInfo, int intInfo) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Host: " + host + ", Port: " + port + ", String: " + stringInfo + ", Int: " + intInfo);
            outputBasinHostLabel.setText(host);
            outputBasinPortLabel.setText(String.valueOf(port));
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RiverSectionApp::new);
    }
}
