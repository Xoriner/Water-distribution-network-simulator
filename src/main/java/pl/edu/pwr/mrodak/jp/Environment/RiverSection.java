package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.io.*;
import java.net.*;

public class RiverSection implements IRiverSection, TcpConnectionHandler {
    private String host;
    private int port;

    private int realDischarge;
    private int rainfall;
    private String retensionBasinHost;
    private int retensionBasinPort;

    public RiverSection(int port) {
        this.realDischarge = 0;
        this.rainfall = 0;
        new Thread(this::startServer).start();
    }

    @Override
    public void setRealDischarge(int realDischarge) {
        this.realDischarge = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    @Override
    public void assignRetensionBasin(int port, String host) {
        this.retensionBasinPort = port;
        this.retensionBasinHost = host;
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String request) {
        if (request.startsWith("srd:")) {
            int realDischarge = Integer.parseInt(request.substring(4));
            setRealDischarge(realDischarge);
            return "0";
        } else if (request.startsWith("srf:")) {
            int rainfall = Integer.parseInt(request.substring(4));
            setRainfall(rainfall);
            return "0";
        } else if (request.startsWith("arb:")) {
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRetensionBasin(port, host);
            return "0";
        }
        return "Unknown request";
    }
}