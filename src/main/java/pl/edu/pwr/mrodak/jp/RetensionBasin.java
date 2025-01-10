package pl.edu.pwr.mrodak.jp;

import java.io.*;
import java.net.*;
import java.util.*;

public class RetensionBasin implements IRetensionBasin {
    private int maxVolume;
    private int currentVolume;
    private int waterDischarge;
    private Map<Integer, Integer> waterInflows = new HashMap<>();
    private String controlCenterHost;
    private int controlCenterPort;

    public RetensionBasin(int maxVolume, int port, String controlCenterHost, int controlCenterPort) {
        this.maxVolume = maxVolume;
        this.controlCenterHost = controlCenterHost;
        this.controlCenterPort = controlCenterPort;
        // Start server socket to listen for incoming connections
        new Thread(() -> startServer(port)).start();
    }

    @Override
    public int getWaterDischarge() {
        return waterDischarge;
    }

    @Override
    public long getFillingPercentage() {
        return (long) ((double) currentVolume / maxVolume * 100);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.waterDischarge = waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        waterInflows.put(port, waterInflow);
    }

    @Override
    public void assignRiverSection(int port, String host) {
        // Implementation to assign river section
    }

    private void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String request = in.readLine();
                    String response = handleRequest(request);
                    out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String request) {
        // Parse and handle the request
        // Return the appropriate response
        return "0"; // Placeholder response
    }
}