package pl.edu.pwr.mrodak.jp.ControlCenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenter extends Observable implements IControlCenter {
    private String host;
    private int port;
    private Map<Integer, String> retensionBasins = new HashMap<>();
    private ScheduledExecutorService scheduler;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public ControlCenter(String host, int port) {
        this.host = host;
        this.port = port;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void start() {
        executor.submit(this::listenForClients);
        monitorBasins();
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

                String fillStatus = sendRequest(basinHost, basinPort, "gfp");
                int waterDischarge = Integer.parseInt(sendRequest(basinHost, basinPort, "gwd"));

                if (fillStatus != null) {
                    notifyObservers(basinHost, basinPort, fillStatus, waterDischarge);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void addObserver(ControlCenterApp controlCenterApp) {
        super.addObserver(controlCenterApp);
    }

    @Override
    public void removeObserver(Observer observer) {
        super.removeObserver(observer);
    }

    public void listenForClients() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Control Center started on port " + port);

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
            if (request != null && request.startsWith("arb:")) {
                processRegisterBasinRequest(request, out);
            } else {
                System.err.println("Unrecognized request: " + request);
                out.println("0"); // Response code 0 for failure
            }
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

    private void processRegisterBasinRequest(String request, PrintWriter out) {
        String[] parts = request.substring(4).split(",");
        if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();
                String basin = host + ":" + port;

                if (!retensionBasins.containsKey(port)) {
                    retensionBasins.put(port, host);
                    System.out.println("Registered retension basin: " + basin);
                }
                out.println("1"); // Response code 1 for success
            } catch (NumberFormatException ex) {
                out.println("0"); // Response code 0 for failure
                System.err.println("Invalid port format: " + parts[0]);
            }
        } else {
            out.println("0"); // Response code 0 for failure
            System.err.println("Invalid registration format: " + request);
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
            scheduler.shutdownNow();
            System.out.println("ControlCenter has been shut down.");
        } catch (Exception ex) {
            ex.printStackTrace();
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
}