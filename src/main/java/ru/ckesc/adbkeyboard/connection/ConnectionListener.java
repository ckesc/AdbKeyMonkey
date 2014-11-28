package ru.ckesc.adbkeyboard.connection;

public interface ConnectionListener {
    public void onConnectionStatusChanged(ConnectionStatus newConnectionStatus);

    public enum ConnectionStatus {
        Disconnected,
        Connecting,
        Connected
    }
}
