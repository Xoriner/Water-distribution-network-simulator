package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.Observable;
import pl.edu.pwr.mrodak.jp.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Environment extends Observable implements IEnvironment {
    private String riverSectionHost;
    private int riverSectionPort;
    private Map<Integer, String> riverSections = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Environment(String host, int port) {
        this.riverSectionHost = host;
        this.riverSectionPort = port;
        this.scheduler = Executors.newScheduledThreadPool(1);
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

                // Simulate monitoring logic
                String waterQuality = "Good"; // Example data
                int waterLevel = 100; // Example data
                waterLevel = waterLevel + 10; // Example data

                notifyObservers(sectionHost, sectionPort, waterQuality, waterLevel);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        super.removeObserver(observer);
    }
}
