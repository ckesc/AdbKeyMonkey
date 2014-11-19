package ru.ckesc.adbkeyboard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ShellDeviceConnection implements DeviceConnection {
    private Process adbShellProcess;
    private BufferedWriter adbStdIn;

    public ShellDeviceConnection() {
        try {
            adbShellProcess = Runtime.getRuntime().exec("adb shell");
            adbStdIn = new BufferedWriter(new OutputStreamWriter(adbShellProcess.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEventToDevice(int eventType) {

        String s = String.format("adb shell input keyevent %d", eventType);
        System.out.println(s);
        try {
            adbStdIn.write(String.format("input keyevent %d%n", eventType));
            adbStdIn.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTextToDevice(String text) {
        String s = String.format("adb shell input text %s", text);
        try {
            Process process = Runtime.getRuntime().exec(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(s);
    }

}
