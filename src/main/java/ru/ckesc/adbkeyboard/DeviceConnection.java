package ru.ckesc.adbkeyboard;

public interface DeviceConnection {
    void sendEventToDevice(int eventType);

    void sendTextToDevice(String text);
}
