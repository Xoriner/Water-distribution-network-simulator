package pl.edu.pwr.mrodak.jp.Environment;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall);
    void assignRetensionBasin(int port, String host);
}