package ru.ckesc.adbkeyboard.connection;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;

public class MonkeyDeviceConnection implements DeviceConnection {
    private ConnectionListener connectionListener;

    private IChimpDevice device;
    private AdbBackend adbBackend;

    @Override
    public void sendEventToDevice(int eventType, KeyAction keyAction) {
        if (!isAlive()) {
            disconnect();
            connect();
            return;
        }

        switch (keyAction) {
            case UP:
                device.press(String.valueOf(eventType), TouchPressType.UP);
                break;
            case DOWN:
                device.press(String.valueOf(eventType), TouchPressType.DOWN);
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
        try {
            if (adbBackend != null) {
                adbBackend.shutdown();
                adbBackend = null;
            }

            // sdk/platform-tools has to be in PATH env variable in order to find adb
            adbBackend = new AdbBackend();
            device = adbBackend.waitForConnection(3000, ".*");

            // Print Device Name
            System.out.println("Connected to: " + device.getProperty("build.model"));

            connectionListener.onConnectionOk();
        } catch (Exception e) {
            connectionListener.onConnectionLost();
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        connectionListener.onConnectionLost();
        if (device != null) {
			try {
                adbBackend.shutdown();
                adbBackend = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    public boolean isAlive() {
        return device != null && device.getPropertyList() != null;
    }
}
