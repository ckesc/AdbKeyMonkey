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


    private void restartStd() {
        connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Connecting);
        try {
            if (adbStdIn != null) {
                adbShellProcess.destroy();
                adbStdIn.close();
            }

            adbShellProcess = Runtime.getRuntime().exec("adb shell");
            adbStdIn = new BufferedWriter(new OutputStreamWriter(adbShellProcess.getOutputStream()));

            //Alive check
            try {
                waitFor(adbShellProcess,1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean alive = isAlive(adbShellProcess);

            setConnected(alive);
        } catch (IOException e) {
            setConnected(false);
            e.printStackTrace();
        }
    }

    /**
     * Tests whether the subprocess represented by this Process is alive.
     * From JDK8
     * @return true if the subprocess represented by this Process object has not yet terminated.
     */
    public static boolean isAlive(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException var2) {
            return true;
        }
    }

    public static boolean waitFor(Process process, long timeout, TimeUnit unit) throws InterruptedException{
        long var4 = System.nanoTime();
        long var6 = unit.toNanos(timeout);

        while(true) {
            try {
                process.exitValue();
                return true;
            } catch (IllegalThreadStateException var9) {
                if(var6 > 0L) {
                    Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(var6) + 1L, 100L));
                }

                var6 = unit.toNanos(timeout) - (System.nanoTime() - var4);
                if(var6 <= 0L) {
                    return false;
                }
            }
        }
    }

    @Override
    public void connect() {
        restartStd();
    }

    @Override
    public void disconnect() {
        connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Disconnected);
        try {
            adbStdIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adbShellProcess.destroy();
    }

    @Override
    public void sendEventToDevice(int eventType, KeyAction keyAction) {
        if (keyAction == KeyAction.UP) {
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
    }

    @Override
    public void sendTextToDevice(String text, KeyAction keyAction) {
        if (keyAction == KeyAction.UP) {
            String s = String.format("adb: send text: %s", text);
            System.out.println(s);
            try {
                adbStdIn.write(String.format("input text %s", text));
                adbStdIn.flush();
                setConnected(true);
            } catch (IOException e) {
                setConnected(false);
                e.printStackTrace();
                connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Disconnected);
                restartStd();
            }
        }
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
                connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Connected);
            } else {
                connectionListener.onConnectionStatusChanged(ConnectionListener.ConnectionStatus.Disconnected);
            }
        }
    }
}
