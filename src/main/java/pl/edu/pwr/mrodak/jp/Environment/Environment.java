package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.Observable;
import pl.edu.pwr.mrodak.jp.Observer;
import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.Map;
import java.util.concurrent.*;

public class Environment extends Observable implements IEnvironment, TcpConnectionHandler.RequestHandler {
    private String riverSectionHost;
    private int riverSectionPort;
    private Map<Integer, String> riverSections = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private ExecutorService executor;
    private TcpConnectionHandler tcpConnectionHandler;

    public Environment(String host, int port) {
        this.riverSectionHost = host;
        this.riverSectionPort = port;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.executor = Executors.newCachedThreadPool();
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }

    @Override
    public void start() {
        executor.submit(() -> tcpConnectionHandler.startServer(riverSectionPort, this));
        monitorRiverSections();
    }
    @Override
    public void assignRiverSection(int port, String host) {
        riverSections.put(port, host);
        System.out.println("Assigned river section: " + host + ":" + port);
    }

    @Override
    public void monitorRiverSections() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Integer, String> entry : riverSections.entrySet()) {
                int sectionPort = entry.getKey();
                String sectionHost = entry.getValue();

                int rainFall = Integer.parseInt(tcpConnectionHandler.sendRequest(sectionHost, sectionPort, "grf"));
                notifyObservers(sectionHost, sectionPort, "", rainFall);
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        super.removeObserver(observer);
    }

    @Override
    public void setRainFall(int port, int rainFall) {
        String host = riverSections.get(port);
        if (host != null) {
            tcpConnectionHandler.sendRequest(host, port, "srf:" + rainFall);
        } else {
            System.err.println("No river section found on port: " + port);
        }
    }

    @Override
    public String handleRequest(String request) {
        //Ars:port,host Assign river section request
        if (request != null && request.startsWith("ars:")) {
            return processRegisterRiverRequest(request);
        } else {
            System.err.println("Unrecognized request: " + request);
            return "0"; // Response code 0 for failure
        }
    }

    private String processRegisterRiverRequest(String request) {
        String[] parts = request.substring(4).split(",");
        if(parts.length > 0) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();

                if(!riverSections.containsKey(port)) {
                    assignRiverSection(port, host);
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
}
