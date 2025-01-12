package pl.edu.pwr.mrodak.jp;

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

    protected void notifyObservers(String host, int port, String stringInfo, int infInfo) {
        for (Observer observer : observers) {
            observer.update(host, port, stringInfo, infInfo);
        }
    }
}