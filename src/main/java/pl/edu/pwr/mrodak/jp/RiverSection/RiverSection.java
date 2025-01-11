package pl.edu.pwr.mrodak.jp.RiverSection;

import pl.edu.pwr.mrodak.jp.Observable;
import pl.edu.pwr.mrodak.jp.Observer;
import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RiverSection extends Observable implements IRiverSection, TcpConnectionHandler.RequestHandler {

    private int delay;
    private int port;
    private String environmentHost;
    private int environmentPort;
    private String retentionBasinHost;
    private int retentionBasinPort;
    private ExecutorService executor;
    private TcpConnectionHandler tcpConnectionHandler;

    public RiverSection(int delay, int port, String environmentHost, int environmentPort, String retentionBasinHost, int retentionBasinPort) {
        this.delay = delay;
        this.port = port;
        this.environmentHost = environmentHost;
        this.environmentPort = environmentPort;
        this.retentionBasinHost = retentionBasinHost;
        this.retentionBasinPort = retentionBasinPort;
        this.executor = Executors.newCachedThreadPool();
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }


    @Override
    public void start() {
        //registerWithEnvironment();
        //registerWithRetentionBasin();
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
    }
    @Override
    public void setRealDischarge(int realDischarge) {

    }

    @Override
    public void setRainfall(int rainfall) {

    }

    @Override
    public void assignRetensionBasin(int port, String host) {

    }

    private String sendRequest(String host, int port, String request) {
        return tcpConnectionHandler.sendRequest(host, port, request);
    }

    public void registerWithEnvironment() {
        String response = sendRequest(environmentHost, environmentPort, "rws:" + port);
        if ("1".equals(response)) {
            System.out.println("River Section registered with Environment.");
        } else {
            System.err.println("Failed to register River Section with Environment.");
        }
    }
    @Override
    public String handleRequest(String request) {
        if ("gwf".equals(request)) {
            return String.valueOf(0);
        } else if (request.startsWith("swf:")) {
            try {
                int flow = Integer.parseInt(request.substring(4));
                return "1"; // Success response
            } catch (NumberFormatException e) {
                System.err.println("Invalid water flow value: " + request);
                return "0"; // Failure response
            }
        }
        return "Unknown request";
    }

    public void shutdown() {
        tcpConnectionHandler.shutdown();
        executor.shutdownNow();
        System.out.println("River Section has been shut down.");
    }

    @Override
    public void addObserver(RiverSectionApp riverSectionApp) {
        super.addObserver(riverSectionApp);
    }

    @Override
    public void removeObserver(Observer observer) {
        super.removeObserver(observer);
    }
}
