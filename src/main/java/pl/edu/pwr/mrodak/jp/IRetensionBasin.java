package pl.edu.pwr.mrodak.jp;

public interface IRetensionBasin {
    int getWaterDischarge();

    long getFillingPercentage();

    void setWaterDischarge(int waterDischarge);

    void setWaterInflow(int waterInflow, int port);

    void assignRiverSection(int port, String host);
}
