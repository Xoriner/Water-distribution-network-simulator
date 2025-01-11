package pl.edu.pwr.mrodak.jp.ControlCenter;

public interface IControlCenter {
    void assignRetensionBasin(int port, String host);
    void monitorBasins();

    void addObserver(ControlCenterApp controlCenterApp);
    void removeObserver(Observer observer);

    void startServer();

    void start();
}