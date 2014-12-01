package ru.ckesc.adbkeyboard;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.connection.ConnectionListener;
import ru.ckesc.adbkeyboard.connection.KeyAction;
import ru.ckesc.adbkeyboard.connection.MonkeyDeviceConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements ConnectionListener {

    private Map<KeyCode, Integer> adbEventMap = new HashMap<>();
    private ExecutorService executor;

    //Colors from material design http://www.google.com/design/spec/style/color.html#color-color-palette
    private final static Color gray50 = Color.web("#FAFAFA");
    private final static Color red200 = Color.web("#EF9A9A");
    private final static Color red300 = Color.web("#E57373");
    private final static Color lime200 = Color.web("#E6EE9C");
    private final static Color lime300 = Color.web("#DCE775");
    private final static Color green100 = Color.web("#C8E6C9");
    private final static Color green300 = Color.web("#81C784");

    private ViewState currentState;

    private Stage stage;
    @FXML
    public TextArea logArea;
    public Label statusLabel;
    public AnchorPane mainPane;

    private MonkeyDeviceConnection deviceConnection;

    public void init(Stage stage) {
        this.stage = stage;

        executor = Executors.newFixedThreadPool(1);

        //        deviceConnection = new ShellDeviceConnection();
        deviceConnection = new MonkeyDeviceConnection();

        initMap();

        showState(ViewState.Init);
        mainPane.setOnKeyPressed(new KeyDownEventHandler());
        mainPane.setOnKeyReleased(new KeyUpEventHandler());

        mainPane.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean focused) {
                showState(currentState); //update current state with focused value
            }
        });

        deviceConnection.setConnectionListener(this);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                deviceConnection.connect();
            }
        });

    }

    private void initMap() {
//        19 -->  "KEYCODE_DPAD_UP"
//        20 -->  "KEYCODE_DPAD_DOWN"
//        21 -->  "KEYCODE_DPAD_LEFT"
//        22 -->  "KEYCODE_DPAD_RIGHT"

        adbEventMap.put(KeyCode.UP, 19);
        adbEventMap.put(KeyCode.DOWN, 20);
        adbEventMap.put(KeyCode.LEFT, 21);
        adbEventMap.put(KeyCode.RIGHT, 22);
        adbEventMap.put(KeyCode.ENTER, 23);
        adbEventMap.put(KeyCode.ESCAPE, 4);
        adbEventMap.put(KeyCode.BACK_SPACE, 67);
    }

    public void stop() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                deviceConnection.disconnect();
            }
        });
        executor.shutdown();
    }

    @Override
    public void onConnectionStatusChanged(ConnectionListener.ConnectionStatus newConnectionStatus) {
        ViewState viewState = ViewState.Init;
        switch (newConnectionStatus) {
            case Disconnected:
                viewState = ViewState.Disconnected;
                break;
            case Connecting:
                viewState = ViewState.Connecting;
                break;
            case Connected:
                viewState = ViewState.Connected;
                break;
        }
        final ViewState finalViewState = viewState;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showState(finalViewState);
            }
        });
    }

    public boolean isFocused() {
        return stage.isFocused();
    }

    private class KeyDownEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(final KeyEvent keyEvent) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (adbEventMap.containsKey(keyEvent.getCode())) {
                        deviceConnection.sendEventToDevice(adbEventMap.get(keyEvent.getCode()), KeyAction.DOWN);
                    } else {
                        deviceConnection.sendTextToDevice(keyEvent.getText(), KeyAction.DOWN);
                    }
                }
            });
        }
    }

    private class KeyUpEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(final KeyEvent keyEvent) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (adbEventMap.containsKey(keyEvent.getCode())) {
                        deviceConnection.sendEventToDevice(adbEventMap.get(keyEvent.getCode()), KeyAction.UP);
                    } else {
                        deviceConnection.sendTextToDevice(keyEvent.getText(), KeyAction.UP);
                    }
                }
            });
        }
    }


    private void showState(ViewState viewState) {
        switch (viewState) {
            case Init:
                statusLabel.setText("Init...");
                changeBgColor(gray50);
                break;
            case Connecting:
                statusLabel.setText("Connecting...");
                if (isFocused()) {
                    changeBgColor(lime300);
                } else {
                    changeBgColor(lime200);
                }
                break;
            case Connected:
                if (isFocused()) {
                    statusLabel.setText("Connected!");
                    changeBgColor(green300);
                } else {
                    statusLabel.setText("Connected!\nNot focused");
                    changeBgColor(green100);
                }

                break;
            case Disconnected:
                statusLabel.setText("Disconnected");
                if (isFocused()) {
                    changeBgColor(red300);
                } else {
                    changeBgColor(red200);
                }
                break;
        }
        currentState = viewState;
    }

    private void changeBgColor(Paint paint) {

    }


    private enum ViewState {
        Init,
        Connecting,
        Connected,
        Disconnected
    }


}
