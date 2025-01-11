package pl.edu.pwr.mrodak.jp.Environment;

import pl.edu.pwr.mrodak.jp.ControlCenter.Observer;

public interface IEnvironment {
    void assignRiverSection(int port, String host);
    void monitorRiverSections();
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}