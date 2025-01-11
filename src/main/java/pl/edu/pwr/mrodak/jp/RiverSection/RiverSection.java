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
    private String outputBasinHost;
    private int outputBasinPort;
    private ExecutorService executor;
    private TcpConnectionHandler tcpConnectionHandler;
    private int rainFall;

    public RiverSection(int delay, int port, String environmentHost, int environmentPort, String retentionBasinHost, int outputBasinPort) {
        this.delay = delay;
        this.port = port;
        this.environmentHost = environmentHost;
        this.environmentPort = environmentPort;
        this.outputBasinHost = retentionBasinHost;
        this.outputBasinPort = outputBasinPort;
        this.executor = Executors.newCachedThreadPool();
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }


    @Override
    public void start() {
        registerWithEnvironment();
        //registerWithRetentionBasin();
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
    }
    @Override
    public void setRealDischarge(int realDischarge) {

    }

    @Override
    public void setRainfall(int rainfall) {
        this.rainFall = rainfall;
    }



    //RetentionBasin at the end of the river section
    @Override
    public void assignRetensionBasin(int port, String host) {
        this.outputBasinPort = port;
        this.outputBasinHost = host;
    }

    private String sendRequest(String host, int port, String request) {
        System.out.println("Sending request to " + host + ":" + port + ": " + request);
        return tcpConnectionHandler.sendRequest(host, port, request);
    }

    //River Section
    public void registerWithRetentionBasin() {
        String response = sendRequest(outputBasinHost, outputBasinPort, "ars:" + port);
        if ("1".equals(response)) {
            System.out.println("River Section registered with Retention Basin.");
        } else {
            System.err.println("Failed to register River Section with Retention Basin.");
        }
    }
    public void registerWithEnvironment() { //change the localhost to host
        String response = sendRequest(environmentHost, environmentPort, "ars:" + port + "," + "localhost");
        if ("1".equals(response)) {
            System.out.println("River Section registered with Environment.");
        } else {
            System.err.println("Failed to register River Section with Environment.");
        }
    }
    @Override
    public String handleRequest(String request) {
        if ("gwf".equals(request)) {
            return String.valueOf(1);//TODO: Implement this method
        } else if (request.startsWith("swf:")) {
            try {
                int flow = Integer.parseInt(request.substring(4));
                return "1"; // Success response
            } catch (NumberFormatException e) {
                System.err.println("Invalid water flow value: " + request);
                return "0"; // Failure response
            }
        } else if(request.startsWith("arb:")) { //assignRetensionBasin
            return processRegisterBasinRequest(request);
        } else if(request.startsWith("grf:")) {
            //getRainfall
            System.out.println("Rainfall: " + getRainfall());
            return String.valueOf(getRainfall());
        }
        return "Unknown request";
    }

    private int getRainfall() {
        return rainFall;
    }

    private String processRegisterBasinRequest(String request) {
        String[] parts = request.substring(4).split(",");
        if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();
                assignRetensionBasin(port, host);
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
