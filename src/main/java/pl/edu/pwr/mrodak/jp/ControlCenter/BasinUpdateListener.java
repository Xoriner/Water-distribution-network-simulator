package pl.edu.pwr.mrodak.jp.ControlCenter;

public interface BasinUpdateListener {
    void onBasinUpdate(String host, int port, String fillStatus);
}
