package pl.edu.pwr.mrodak.jp;

public class Main {
    public static void main(String[] args) {
        // Initialize Retension Basins
        RetensionBasin basin1 = new RetensionBasin(1000, 8081, "localhost", 9090);
        RetensionBasin basin2 = new RetensionBasin(2000, 8082, "localhost", 9090);

        // Initialize Control Center
        ControlCenter controlCenter = new ControlCenter();
        controlCenter.assignRetensionBasin(8081, "localhost");
        controlCenter.assignRetensionBasin(8082, "localhost");

        // Start monitoring in the Control Center
        controlCenter.monitorBasins();
    }
}
