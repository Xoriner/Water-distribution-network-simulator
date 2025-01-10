package pl.edu.pwr.mrodak.jp.RetensionBasin;

import java.io.*;
import java.net.*;
import java.util.*;

public class RetensionBasin implements IRetensionBasin {
    private int maxVolume;
    private String host;
    private int port;
    private String controlCenterHost;
    private int controlCenterPort;
    private Map<Integer, String> incomingRiverSections = new HashMap<>();
    private int currentVolume;
    private int waterDischarge;
    private Map<Integer, Integer> waterInflows = new HashMap<>();

    public RetensionBasin(int maxVolume, String host, int port, String controlCenterHost, int controlCenterPort, Map<Integer, String> incomingRiverSections) {
        this.maxVolume = maxVolume;
        this.host = host;
        this.port = port;
        this.controlCenterHost = controlCenterHost;
        this.controlCenterPort = controlCenterPort;
        this.incomingRiverSections = incomingRiverSections;
        new Thread(() -> startServer(host, port)).start();
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
        incomingRiverSections.put(port, host);
    }

    private void startServer(String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.bind(new InetSocketAddress(host, port));
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     InputStream InputStream = clientSocket.getInputStream();
                     InputStreamReader InputStreamReader = new InputStreamReader(InputStream);
                     BufferedReader in = new BufferedReader(InputStreamReader);
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

    // Request Handling
    private String handleRequest(String request) {
        if ("gwd".equals(request)) {
            return String.valueOf(getWaterDischarge());
        } else if ("gfp".equals(request)) {
            return String.valueOf(getFillingPercentage());
        } else if (request.startsWith("swd:")) {
            int waterDischarge = Integer.parseInt(request.substring(4));
            setWaterDischarge(waterDischarge);
            return "0";
        } else if (request.startsWith("swi:")) {
            String[] parts = request.substring(4).split(",");
            int waterInflow = Integer.parseInt(parts[0]);
            int port = Integer.parseInt(parts[1]);
            setWaterInflow(waterInflow, port);
            return "0";
        } else if (request.startsWith("arb:")) {
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRiverSection(port, host);
            return "0";
        }
        return "Unknown request";
    }
}