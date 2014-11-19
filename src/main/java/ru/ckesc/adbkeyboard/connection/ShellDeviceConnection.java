package ru.ckesc.adbkeyboard.connection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

public class ShellDeviceConnection implements DeviceConnection {
    private Process adbShellProcess;
    private BufferedWriter adbStdIn;
    private ConnectionListener connectionListener;
    private Boolean isConnected = null;

    public ShellDeviceConnection() {
    }

    @Override
    public void sendEventToDevice(int eventType) {

        String s = String.format("adb: send keyevent %d", eventType);
        System.out.println(s);
        try {
            adbStdIn.write(String.format("input keyevent %d%n", eventType));
            adbStdIn.flush();
            setConnected(true);
        } catch (IOException e) {
            setConnected(false);
            e.printStackTrace();
            restartStd();
        }
    }

    @Override
    public void sendTextToDevice(String text) {
        String s = String.format("adb: send text: %s", text);
        System.out.println(s);
        try {
            adbStdIn.write(String.format("input text %s", text));
            adbStdIn.flush();
            setConnected(true);
        } catch (IOException e) {
            setConnected(false);
            e.printStackTrace();
            connectionListener.onConnectionLost();
            restartStd();
        }
    }

    private void restartStd() {
        try {
            if (adbStdIn != null) {
                adbShellProcess.destroy();
                adbStdIn.close();
            }

            adbShellProcess = Runtime.getRuntime().exec("adb shell");
            adbStdIn = new BufferedWriter(new OutputStreamWriter(adbShellProcess.getOutputStream()));

            //Alive check
            try {
                adbShellProcess.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean alive = adbShellProcess.isAlive();

            setConnected(alive);
        } catch (IOException e) {
            setConnected(false);
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        restartStd();
    }

    @Override
    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }


    public void setConnected(boolean isConnected) {
        if (this.isConnected != null && this.isConnected == isConnected) {
            return;
        }

        this.isConnected = isConnected;
        if (connectionListener != null) {
            if (isConnected) {
                connectionListener.onConnectionOk();
            } else {
                connectionListener.onConnectionLost();
            }
        }
    }
}
