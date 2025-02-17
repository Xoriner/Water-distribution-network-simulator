package pl.edu.pwr.mrodak.jp.ControlCenter;

import pl.edu.pwr.mrodak.jp.Observer;

public interface IControlCenter {
    void assignRetensionBasin(int port, String host);
    void monitorBasins();

    void addObserver(ControlCenterApp controlCenterApp);
    void removeObserver(Observer observer);

    void start();

    void setWaterDischarge(int port, int waterDischarge);
}