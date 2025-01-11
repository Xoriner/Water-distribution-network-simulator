package pl.edu.pwr.mrodak.jp.RiverSection;

import pl.edu.pwr.mrodak.jp.TcpConnectionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RiverSection implements IRiverSection, TcpConnectionHandler.RequestHandler {
    private String host;
    private int port;
    private int waterFlow;
    private int maxCapacity;
    private ExecutorService executor;
    private TcpConnectionHandler tcpConnectionHandler;

    public RiverSection(String host, int port, int maxCapacity) {
        this.host = host;
        this.port = port;
        this.maxCapacity = maxCapacity;
        this.waterFlow = 0; // Initial water flow
        this.executor = Executors.newCachedThreadPool();
        this.tcpConnectionHandler = new TcpConnectionHandler();
    }


    public void start() {
        executor.submit(() -> tcpConnectionHandler.startServer(port, this));
    }

    public void setWaterFlow(int waterFlow) {
        if (waterFlow > maxCapacity) {
            this.waterFlow = maxCapacity; // Cap the flow to the maximum capacity
        } else {
            this.waterFlow = waterFlow;
        }
        System.out.println("Water flow set to: " + this.waterFlow);
    }

    @Override
    public String handleRequest(String request) {
        if ("gwf".equals(request)) {
            return String.valueOf(waterFlow);
        } else if (request.startsWith("swf:")) {
            try {
                int flow = Integer.parseInt(request.substring(4));
                setWaterFlow(flow);
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
    public void setRealDischarge(int realDischarge) {

    }

    @Override
    public void setRainfall(int rainfall) {

    }

    @Override
    public void assignRetensionBasin(int port, String host) {

    }
}
