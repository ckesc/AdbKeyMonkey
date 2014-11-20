package ru.ckesc.adbkeyboard.connection;

public interface DeviceConnection {
    void sendEventToDevice(int eventType, KeyAction keyAction);

    void sendTextToDevice(String text, KeyAction keyAction);

    void setConnectionListener(ConnectionListener connectionListener);

    void connect();

    void disconnect();
}
