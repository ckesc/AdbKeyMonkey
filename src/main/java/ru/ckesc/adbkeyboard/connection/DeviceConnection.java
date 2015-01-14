package ru.ckesc.adbkeyboard.connection;

public interface DeviceConnection {
    void sendEventToDevice(String keyCode, KeyAction keyAction);

    void sendTextToDevice(String text, KeyAction keyAction);

    void setConnectionListener(ConnectionListener connectionListener);

    void connect();

    void disconnect();
}
