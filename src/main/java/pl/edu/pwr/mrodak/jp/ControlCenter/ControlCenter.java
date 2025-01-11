package pl.edu.pwr.mrodak.jp.ControlCenter;

import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenter extends Observable implements IControlCenter, TcpConnectionHandler.RequestHandler {
    private String host;
    private int port;
    private Map<Integer, String> retensionBasins = new HashMap<>();
    private ScheduledExecutorService scheduler;
    private ExecutorService executor;
    private TcpConnectionHandler tcpConnectionHandler;

    public ControlCenter(String host, int port) {
        this.host = host;
        this.port = port;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.executor = Executors.newCachedThreadPool();
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }

    @Override
    public void start() {
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
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

                String fillStatus = tcpConnectionHandler.sendRequest(basinHost, basinPort, "gfp");
                int waterDischarge = Integer.parseInt(tcpConnectionHandler.sendRequest(basinHost, basinPort, "gwd"));

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

    @Override
    public String handleRequest(String request) {
        if (request != null && request.startsWith("arb:")) {
            return processRegisterBasinRequest(request);
        } else {
            System.err.println("Unrecognized request: " + request);
            return "0"; // Response code 0 for failure
        }
    }

    private String processRegisterBasinRequest(String request) {
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

    public void setWaterDischarge(int port, int waterDischarge) {
        String host = retensionBasins.get(port);
        if (host != null) {
            tcpConnectionHandler.sendRequest(host, port, "swd:" + waterDischarge);
        } else {
            System.err.println("No retension basin found on port: " + port);
        }
    }

    public void shutdown() {
        tcpConnectionHandler.shutdown();
        scheduler.shutdownNow();
        executor.shutdownNow();
        System.out.println("ControlCenter has been shut down.");
    }
}