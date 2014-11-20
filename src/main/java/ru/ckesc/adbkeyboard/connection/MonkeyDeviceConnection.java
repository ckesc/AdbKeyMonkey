package ru.ckesc.adbkeyboard.connection;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.TouchPressType;

public class MonkeyDeviceConnection implements DeviceConnection {
    private ConnectionListener connectionListener;

    private IChimpDevice device;

    @Override
    public void sendEventToDevice(int eventType) {
        device.press(String.valueOf(eventType), TouchPressType.DOWN_AND_UP);
    }

    @Override
    public void sendTextToDevice(String text) {
        device.press(text, TouchPressType.DOWN_AND_UP);
    }

    @Override
    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    public void connect() {
        try {
            // sdk/platform-tools has to be in PATH env variable in order to find adb
            device = new AdbBackend().waitForConnection();

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
        device.dispose();
    }
}
