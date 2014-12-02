package ru.ckesc.adbkeyboard.connection;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import ru.ckesc.adbkeyboard.Logger;

public class MonkeyDeviceConnection implements DeviceConnection {
    private ConnectionListener connectionListener;

    private IChimpDevice device;
    private AdbBackend adbBackend;
    private Logger logger;

    public MonkeyDeviceConnection(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void sendEventToDevice(int eventType, KeyAction keyAction) {
        if (!isAlive()) {
            disconnect();
            connect();
            return;
        }

        String keyCode = String.valueOf(eventType);
        switch (keyAction) {
            case UP:
//                log("Key up  : "+keyCode);
                device.press(keyCode, TouchPressType.UP);
                break;
            case DOWN:
//                log("Key down: "+keyCode);
                device.press(keyCode, TouchPressType.DOWN);
                break;
        }
        if (!isAlive()) {
            disconnect();
            connect();
        }
    }

    @Override
    public void sendTextToDevice(String text, KeyAction keyAction) {
        if (!isAlive()) {
            disconnect();
            connect();
            return;
        }

        switch (keyAction) {
            case UP:
                device.press(text, TouchPressType.UP);
                break;
            case DOWN:
                device.press(text, TouchPressType.DOWN);
                break;
        }
        if (!isAlive()) {
            disconnect();
            connect();
        }

    }

    @Override
    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    public void connect() {
        log("Connecting...");
        connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Connecting);
        try {
            if (adbBackend != null) {
                disconnect();
            }

            // sdk/platform-tools has to be in PATH env variable in order to find adb
            adbBackend = new AdbBackend();
            device = adbBackend.waitForConnection(3000, ".*");

            if (device == null) {
                log("Can`t connect. Disconnecting.");
                disconnect();
                return;
            }

            // Print Device Name
            log("Connected to: " + device.getProperty("build.model"));

            connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Connected);
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void disconnect() {
        connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Disconnected);
        if (adbBackend != null) {
            try {
                log("Disconnecting: Shutting down adb...");
                adbBackend.shutdown();
                adbBackend = null;
                log("Disconnected.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAlive() {
        return device != null && device.getPropertyList() != null;
    }

    private void log(String logLine) {
        System.out.println(logLine);
        logger.info(logLine);
    }
}
