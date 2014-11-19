package ru.ckesc.adbkeyboard.connection;

public interface DeviceConnection {
    void sendEventToDevice(int eventType);

    void sendTextToDevice(String text);

    void setConnectionListener(ConnectionListener connectionListener);

    void connect();
}
