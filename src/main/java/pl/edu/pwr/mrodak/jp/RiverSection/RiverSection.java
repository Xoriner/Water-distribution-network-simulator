package pl.edu.pwr.mrodak.jp.RiverSection;

import pl.edu.pwr.mrodak.jp.Observable;
import pl.edu.pwr.mrodak.jp.Observer;
import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RiverSection extends Observable implements IRiverSection, TcpConnectionHandler.RequestHandler {

    private int delay;
    private int port;
    private String environmentHost;
    private int environmentPort;
    private String inputBasinHost;
    private int inputBasinPort;
    private ExecutorService executor;
    private ScheduledExecutorService scheduler;
    private TcpConnectionHandler tcpConnectionHandler;

    //get Rainfall from Environment
    private int rainFall = 10;
    private int realDischarge = 0;

    private String outputBasinHost;
    private int outputBasinPort;

    public RiverSection(int delay, int port, String environmentHost, int environmentPort, String inputBasinHost, int inputBasinPort) {
        this.delay = delay;
        this.port = port;
        this.environmentHost = environmentHost;
        this.environmentPort = environmentPort;
        this.inputBasinHost = inputBasinHost;
        this.inputBasinPort = inputBasinPort;
        this.executor = Executors.newCachedThreadPool();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }


    @Override
    public void start() {
        registerWithEnvironment();
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
        monitorOutputRetentionBasin();
        scheduler.scheduleAtFixedRate(this::calculateAndSendWaterInflow, 0, delay, TimeUnit.MILLISECONDS);
        registerWithInputRetentionBasin();
    }
    @Override
    public void setRealDischarge(int realDischarge) {
        this.realDischarge = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) {
        this.rainFall = rainfall;
    }

    public void sendWaterInflowToOutputBasin(int waterInflow) {
        if (outputBasinHost != null && outputBasinPort > 0) {
            String request = "swi:" + port + "," + waterInflow;
            String response = sendRequest(outputBasinHost, outputBasinPort, request);
            if ("1".equals(response)) {
                System.out.println("Water inflow sent to output basin: " + waterInflow);
            } else {
                System.err.println("Failed to send water inflow to output basin.");
            }
        } else {
            System.err.println("Output basin not assigned.");
        }
    }

    //RetentionBasin at the end of the river section
    @Override
    public void assignRetensionBasin(int port, String host) {
        this.outputBasinPort = port;
        this.outputBasinHost = host;
        System.out.println("Assigned output Retention Basin: " + host + ":" + port);
        notifyObservers(outputBasinHost, outputBasinPort, "", 0);
    }

    public void monitorOutputRetentionBasin() {
        scheduler.scheduleAtFixedRate(() -> {
            notifyObservers(outputBasinHost, outputBasinPort, "", 0);
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void calculateAndSendWaterInflow() {
        int waterInflow = calculateWaterInflow();
        sendWaterInflowToOutputBasin(waterInflow);
    }

    private int calculateWaterInflow() {
        return realDischarge + rainFall;
    }

    private String sendRequest(String host, int port, String request) {
        System.out.println("Sending request to " + host + ":" + port + ": " + request);
        return tcpConnectionHandler.sendRequest(host, port, request);
    }

    //River Section
    public void registerWithInputRetentionBasin() {
        String response = sendRequest(inputBasinHost, inputBasinPort, "ars:" + port + "," + "localhost");
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
        //System.out.println(request);
        if (request.startsWith("swf:")) {
            try {
                int flow = Integer.parseInt(request.substring(4));
                return "1"; // Success response
            } catch (NumberFormatException e) {
                System.err.println("Invalid water flow value: " + request);
                return "0"; // Failure response
            }
        } else if(request.startsWith("arb:")) {
            //assignRetensionBasin
            System.out.println("Assign Retention Basin request: " + request);
            return processRegisterBasinRequest(request);
        } else if(request.equals("grf")) {
            //getRainfall
            System.out.println("Rainfall: " + getRainfall());
            return String.valueOf(getRainfall());
        } else if(request.startsWith("srf:")) {
            //setRainfall
            try {
                int rainFall = Integer.parseInt(request.substring(4));
                setRainfall(rainFall);
                return "1"; // Success response
            } catch (NumberFormatException e) {
                System.err.println("Invalid rainfall value: " + request);
                return "0"; // Failure response
            }
        } else if(request.startsWith("srd:")) {
            //setRealDischarge
            try {
                int realDischarge = Integer.parseInt(request.substring(4));
                setRealDischarge(realDischarge);
                return "1"; // Success response
            } catch (NumberFormatException e) {
                System.err.println("Invalid real discharge value: " + request);
                return "0"; // Failure response
            }
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
