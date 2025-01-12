package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.Observer;

public interface IEnvironment {
    void start();

    void assignRiverSection(int port, String host);
    void monitorRiverSections();
    void addObserver(Observer observer);
    void removeObserver(Observer observer);

    void setRainFall(int port, int rainFall);
}