package pl.edu.pwr.mrodak.jp.RiverSection;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall);
    void assignRetensionBasin(int port, String host);
}