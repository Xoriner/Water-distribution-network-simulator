package pl.edu.pwr.mrodak.jp;

import pl.edu.pwr.mrodak.jp.RetensionBasin.RetensionBasin;
import pl.edu.pwr.mrodak.jp.RiverSection.RiverSection;

public class Main {
    public static void main(String[] args) {
        // Create RetensionBasin instance
        RetensionBasin retensionBasin = new RetensionBasin(1000, "localhost", 8081, "localhost", 8080);

        // Create RiverSection instance
        RiverSection riverSection = new RiverSection(1000, 8082, "localhost", 8080, "localhost", 8081);
        RiverSection riverSection1 = new RiverSection(1000, 8083, "localhost", 8080, "localhost", 8081);

        // Start the RetensionBasin and RiverSection
        retensionBasin.start();
        riverSection.start();
        riverSection1.start();

        // Simulate sending water inflow to RetensionBasin
        retensionBasin.setWaterInflow(500, 8082);
        retensionBasin.setWaterInflow(400, 8083);

        // Simulate setting water discharge from RetensionBasin
        retensionBasin.setWaterDischarge(300);

        // Print the current state
        System.out.println("Retension Basin Filling Percentage: " + retensionBasin.getFillingPercentage() + "%");
        System.out.println("Retension Basin Water Discharge: " + retensionBasin.getWaterDischarge() + " L/s");

        // Shutdown the components
        retensionBasin.shutdown();
        riverSection.shutdown();
    }
}