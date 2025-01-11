package pl.edu.pwr.mrodak.jp;

public interface Observer {
    void update(String host, int port, String fillStatus, int waterDischarge);
}