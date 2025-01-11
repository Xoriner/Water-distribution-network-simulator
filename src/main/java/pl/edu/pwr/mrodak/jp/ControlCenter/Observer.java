package pl.edu.pwr.mrodak.jp.ControlCenter;

public interface Observer {
    void update(String host, int port, String fillStatus, int waterDischarge);
}