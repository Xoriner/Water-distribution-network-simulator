package pl.edu.pwr.mrodak.jp.ControlCenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenter implements IControlCenter {
    private Map<Integer, String> retensionBasins = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void assignRetensionBasin(int port, String host) {
        retensionBasins.put(port, host);
    }

    public void monitorBasins() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Integer, String> entry : retensionBasins.entrySet()) {
                int port = entry.getKey();
                String host = entry.getValue();
                String status = sendRequest(host, port, "gfp");
                if (status != null) {
                    System.out.println("Status of basin at " + host + ":" + port + " - " + status);
                } else {
                    System.err.println("Failed to get status from basin at " + host + ":" + port);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private String sendRequest(String host, int port, String request) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Failed to connect to " + host + ":" + port + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}