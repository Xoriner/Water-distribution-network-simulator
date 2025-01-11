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
    private String host;
    private int port;
    private Map<Integer, String> retensionBasins = new HashMap<>();
    private ScheduledExecutorService scheduler;
    private BasinUpdateListener updateListener;

    public ControlCenter(String host, int port) {
        this.host = host;
        this.port = port;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void assignRetensionBasin(int port, String host) {
        retensionBasins.put(port, host);
        System.out.println("Assigned retension basin: " + host + ":" + port);
    }

    @Override
    public void monitorBasins() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Integer, String> entry : retensionBasins.entrySet()) {
                int basinPort = entry.getKey();
                String basinHost = entry.getValue();

                String fillStatus = sendRequest(basinHost, basinPort, "gwd");
                if (fillStatus != null && updateListener != null) {
                    updateListener.onBasinUpdate(basinHost, basinPort, fillStatus);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void registerBasinUpdateListener(BasinUpdateListener listener) {
        this.updateListener = listener;
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

    public void shutdown() {
        scheduler.shutdownNow();
        System.out.println("ControlCenter has been shut down.");
    }
}
