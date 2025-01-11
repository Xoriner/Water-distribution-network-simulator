package pl.edu.pwr.mrodak.jp.RetensionBasin;

import java.io.*;
import java.net.*;

public class RetensionBasin {
    private int maxVolume;
    private String host;
    private int port;
    private String controlCenterHost;
    private int controlCenterPort;
    private int currentVolume;
    private int waterDischarge;

    public RetensionBasin(int maxVolume, String host, int port, String controlCenterHost, int controlCenterPort) {
        this.maxVolume = maxVolume;
        this.host = host;
        this.port = port;
        this.controlCenterHost = controlCenterHost;
        this.controlCenterPort = controlCenterPort;
        new Thread(this::startServer).start();
    }

    public int getWaterDischarge() {
        return waterDischarge;
    }

    public int getFillingPercentage() {
        currentVolume = currentVolume + 10;
        return (int) ((double) currentVolume / maxVolume * 100);
    }

    public void registerWithControlCenter() {
        try (Socket socket = new Socket(controlCenterHost, controlCenterPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("arb:" + port + "," + host);
            String response = in.readLine();
            if (!"1".equals(response)) {
                System.err.println("Failed to register with Control Center");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request = in.readLine();
                    String response = handleRequest(request);
                    out.println(response);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String request) {
        if ("gfp".equals(request)) {
            return String.valueOf(getFillingPercentage());
        } else if ("gwd".equals(request)) {
            return String.valueOf(getWaterDischarge());
        }
        return "Unknown request";
    }
}