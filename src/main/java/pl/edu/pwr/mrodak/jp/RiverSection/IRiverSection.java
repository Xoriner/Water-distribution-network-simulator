package pl.edu.pwr.mrodak.jp.RiverSection;

import pl.edu.pwr.mrodak.jp.ControlCenter.ControlCenterApp;
import pl.edu.pwr.mrodak.jp.Observer;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall);
    void assignRetensionBasin(int port, String host);
    void addObserver(RiverSectionApp riverSectionApp);
    void removeObserver(Observer observer);
    void start();
}