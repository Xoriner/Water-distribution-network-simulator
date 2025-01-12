package pl.edu.pwr.mrodak.jp.RetensionBasin;

import pl.edu.pwr.mrodak.jp.Observable;
import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.List;
import java.util.concurrent.*;

public class RetensionBasin extends Observable implements IRetensionBasin, TcpConnectionHandler.RequestHandler {
    private int maxVolume;
    private String host;
    private int port;
    private String controlCenterHost;
    private int controlCenterPort;
    private int currentVolume;
    private int waterDischarge = 10;
    private ExecutorService executor;
    private ScheduledExecutorService scheduler;
    private TcpConnectionHandler tcpConnectionHandler;

    private List<Integer> incomingRiverSectionPorts = new CopyOnWriteArrayList<>();
    private String outgoingRiverSectionHost;
    private int outgoingRiverSectionPort;
    private ConcurrentMap<Integer, Integer> inflows = new ConcurrentHashMap<>();

    public RetensionBasin(int maxVolume, String host, int port, String controlCenterHost, int controlCenterPort) {
        this.maxVolume = maxVolume;
        this.host = host;
        this.port = port;
        this.controlCenterHost = controlCenterHost;
        this.controlCenterPort = controlCenterPort;
        this.executor = Executors.newCachedThreadPool();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }

    @Override
    public void start() {
        registerWithControlCenter();
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
        monitorOutgoingRiverSection();
        scheduler.scheduleAtFixedRate(this::updateCurrentVolume, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void connectWithIncomingRiverSections() {
        System.out.println("Connecting with incoming river sections");
        registerWithIncomingRiverSections();
    }


    private void updateCurrentVolume() {
        int totalInflow = inflows.values().stream().mapToInt(Integer::intValue).sum();
        currentVolume += totalInflow - waterDischarge;
        if (currentVolume > maxVolume) {
            currentVolume = maxVolume;
            //CHANGE THIS PROBABLY
            waterDischarge = totalInflow;
        } else if (currentVolume < 0) {
            currentVolume = 0;
        }
        System.out.println("Current volume: " + currentVolume);
    }

    public int getWaterDischarge() {
        return waterDischarge;
    }

    public long getFillingPercentage() {
        return (int) ((double) currentVolume / maxVolume * 100);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.waterDischarge = waterDischarge;
        sendWaterDischargeToOutgoingSection();
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        if (incomingRiverSectionPorts.contains(port)) {
            inflows.put(port, waterInflow);
            updateCurrentVolume();
        }
    }

    //Assign the River Section (output for the Retention Basin)
    @Override
    public void assignRiverSection(int port, String host) {
        this.outgoingRiverSectionPort = port;
        this.outgoingRiverSectionHost = host;
        System.out.println("Assigned output River Section: " + host + ":" + port);
        notifyObservers(outgoingRiverSectionHost, outgoingRiverSectionPort, "", 0);
    }

    public void monitorOutgoingRiverSection() {
        scheduler.scheduleAtFixedRate(() -> {
            notifyObservers(outgoingRiverSectionHost, outgoingRiverSectionPort, "", 0);
        }, 0, 2, TimeUnit.SECONDS);
    }


    //Send assignRetensionBasin request to the River Section (input for the Retension Basin)
    public void registerWithIncomingRiverSections() {
        for (int port : incomingRiverSectionPorts) {
            String response = sendRequest(host, port, "arb:" + this.port + "," + this.host);
            if ("1".equals(response)) {
                System.out.println("Retention Basin registered with Incoming River Section on port " + port);
            } else {
                System.err.println("Failed to register Retention Basin with Incoming River Section on port " + port);
            }
        }
    }

    @Override
    public void addIncomingRiverSection(String host, int port) {
        if (incomingRiverSectionPorts.contains(port)) {
            System.out.println("Incoming river section already added: " + host + ":" + port);
            return;
        }
        incomingRiverSectionPorts.add(port);
        System.out.println("Added incoming river section: " + host + ":" + port);
    }

    private void sendWaterDischargeToOutgoingSection() {
        if (outgoingRiverSectionPort > 0) {
            tcpConnectionHandler.sendRequest(host, outgoingRiverSectionPort, "srd:" + waterDischarge);
        }
    }

    private String sendRequest(String host, int port, String request) {
        System.out.println("Sending request to " + host + ":" + port + " - " + request);
        return tcpConnectionHandler.sendRequest(host, port, request);
    }

    public void registerWithControlCenter() {
        String response = sendRequest(controlCenterHost, controlCenterPort, "arb:" + port + "," + host);
        if (!"1".equals(response)) {
            System.err.println("Failed to register with Control Center");
        }
    }

    @Override
    public String handleRequest(String request) {
        if ("gfp".equals(request)) {
            return String.valueOf(getFillingPercentage());
        } else if ("gwd".equals(request)) {
            return String.valueOf(getWaterDischarge());
        } else if (request.startsWith("swd:")) {
            setWaterDischarge(Integer.parseInt(request.substring(4)));
            return "1"; // Success response
        } else if (request.startsWith("swi:")) {
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            int waterInflow = Integer.parseInt(parts[1]);
            setWaterInflow(waterInflow, port);
            System.out.println("Water inflow set to " + waterInflow + " from river section on port " + port);
            return "1"; // Success response
        } else if (request.startsWith("ars:")) {
            System.out.println("Assign Output River Section request: " + request);
            return processRegisterOutgoingRiverSectionRequest(request);
        }
        return "Unknown request";
    }

    private String processRegisterOutgoingRiverSectionRequest(String request) {
        String[] parts = request.substring(4).split(",");
        if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[0].trim());
                String host = parts[1].trim();
                assignRiverSection(port, host);
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
        scheduler.shutdownNow();
        System.out.println("Retention Basin has been shut down.");
    }

    @Override
    public void addObserver(RetensionBasinApp retensionBasinApp) {
        super.addObserver(retensionBasinApp);
    }
}