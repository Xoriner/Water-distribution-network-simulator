package pl.edu.pwr.mrodak.jp.ControlCenter;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String host, int port, String fillStatus, int waterDischarge) {
        for (Observer observer : observers) {
            observer.update(host, port, fillStatus, waterDischarge);
        }
    }
}