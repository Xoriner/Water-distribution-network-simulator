package pl.edu.pwr.mrodak.jp.RetensionBasin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RetensionBasin implements IRetensionBasin {
    private int maxVolume;
    private String host;
    private int port;
    private String controlCenterHost;
    private int controlCenterPort;
    private int currentVolume;
    private int waterDischarge;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    private List<Integer> incomingRiverSectionPorts = new ArrayList<>();
    private int outgoingRiverSectionPort;

    public RetensionBasin(int maxVolume, String host, int port, String controlCenterHost, int controlCenterPort) {
        this.maxVolume = maxVolume;
        this.host = host;
        this.port = port;
        this.controlCenterHost = controlCenterHost;
        this.controlCenterPort = controlCenterPort;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void start() {
        registerWithControlCenter();
        executor.submit(this::startServer);
    }

    public int getWaterDischarge() {
        return waterDischarge;
    }

    public long getFillingPercentage() {
        return (int) ((double) currentVolume / maxVolume * 100);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.waterDischarge = waterDischarge;
        sendWaterDischargeToOutgoingSection();
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        if (incomingRiverSectionPorts.contains(port)) {
            currentVolume += waterInflow;
            if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            }
        }
    }

    @Override
    public void assignRiverSection(int port, String host) {
        this.outgoingRiverSectionPort = port;
        this.host = host;
    }

    public void addIncomingRiverSectionPort(int port) {
        incomingRiverSectionPorts.add(port);
    }

    public void setOutgoingRiverSectionPort(int port) {
        this.outgoingRiverSectionPort = port;
    }

    private void sendWaterDischargeToOutgoingSection() {
        if (outgoingRiverSectionPort > 0) {
            try (Socket socket = new Socket(host, outgoingRiverSectionPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("srd:" + waterDischarge);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String sendRequest(String host, int port, String request) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Error connecting to " + host + ":" + port + " - " + e.getMessage());
            return null;
        }
    }

    public void registerWithControlCenter() {
        String response = sendRequest(controlCenterHost, controlCenterPort, "arb:" + port + "," + host);
        if (!"1".equals(response)) {
            System.err.println("Failed to register with Control Center");
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Retension Basin started on port " + port);

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                } catch (Exception ex) {
                    if (!serverSocket.isClosed()) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Failed to start server: " + ex.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            String response = handleRequest(request);
            out.println(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String handleRequest(String request) {
        if ("gfp".equals(request)) {
            return String.valueOf(getFillingPercentage());
        } else if ("gwd".equals(request)) {
            return String.valueOf(getWaterDischarge());
        } else if (request.startsWith("swd:")) {
            setWaterDischarge(Integer.parseInt(request.substring(4)));
            return "1"; // Success response
        } else if (request.startsWith("swi:")) {
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            int waterInflow = Integer.parseInt(parts[1]);
            setWaterInflow(waterInflow, port);
            return "1"; // Success response
        } else if (request.startsWith("arb:")) {
            return processRegisterBasinRequest(request);
        }
        return "Unknown request";
    }

    private String processRegisterBasinRequest(String request) {
        String[] parts = request.substring(4).split(",");
        if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();
                String basin = host + ":" + port;

                if (!incomingRiverSectionPorts.contains(port)) {
                    incomingRiverSectionPorts.add(port);
                    System.out.println("Registered incoming river section: " + basin);
                }
                return "1"; // Response code 1 for success
            } catch (NumberFormatException ex) {
                System.err.println("Invalid port format: " + parts[0]);
                return "0"; // Response code 0 for failure
            }
        } else {
            System.err.println("Invalid registration format: " + request);
            return "0"; // Response code 0 for failure
        }
    }

    public void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
            System.out.println("Retension Basin has been shut down.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}