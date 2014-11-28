package ru.ckesc.adbkeyboard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.connection.ConnectionListener;
import ru.ckesc.adbkeyboard.connection.DeviceConnection;
import ru.ckesc.adbkeyboard.connection.KeyAction;
import ru.ckesc.adbkeyboard.connection.MonkeyDeviceConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application implements ConnectionListener {

    private Map<KeyCode, Integer> adbEventMap = new HashMap<>();
    private ExecutorService executor;
    private Scene scene;
    private Label status;

    //Colors from material design http://www.google.com/design/spec/style/color.html#color-color-palette
    private final static Color gray50 = Color.web("#FAFAFA");
    private final static Color red200 = Color.web("#EF9A9A");
    private final static Color red300 = Color.web("#E57373");
    private final static Color lime200 = Color.web("#E6EE9C");
    private final static Color lime300 = Color.web("#DCE775");
    private final static Color green100 = Color.web("#C8E6C9");
    private final static Color green300 = Color.web("#81C784");

    private ViewState currentState;
    private boolean isFocused = false;

    public static void main(String[] args) {
        launch(args);
    }

    public DeviceConnection deviceConnection;

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        executor = Executors.newFixedThreadPool(1);
        initUI(primaryStage);
        initMap();

        showState(ViewState.Connecting);
        scene.setOnKeyPressed(new KeyDownEventHandler());
        scene.setOnKeyReleased(new KeyUpEventHandler());
        primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean focused) {
                isFocused = focused;
                showState(currentState); //update showing state
            }
        });

//        deviceConnection = new ShellDeviceConnection();
        deviceConnection = new MonkeyDeviceConnection();
        deviceConnection.setConnectionListener(this);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                deviceConnection.connect();
            }
        });
    }

    private void initUI(Stage primaryStage) {
        StackPane stackPane = new StackPane();
        status = new Label();
        status.setFont(Font.font(Font.getDefault().getName(),30));
        stackPane.getChildren().add(status);
        scene = new Scene(stackPane, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Adb Key Monkey!");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                deviceConnection.disconnect();
            }
        });
        executor.shutdown();
    }

    @Override
    public void onConnectionLost() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showState(ViewState.Disconnected);
            }
        });
    }

    @Override
    public void onConnectionOk() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showState(ViewState.Connected);
            }
        });
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
                status.setText("Init...");
                scene.setFill(gray50);
                break;
            case Connecting:
                status.setText("Connecting...");
                if (isFocused) {
                    scene.setFill(lime300);
                } else {
                    scene.setFill(lime200);
                }
                break;
            case Connected:
                if (isFocused) {
                    status.setText("Connected!");
                    scene.setFill(green300);
                } else {
                    status.setText("Connected!\nNot focused");
                    scene.setFill(green100);
                }

                break;
            case Disconnected:
                status.setText("Disconnected");
                if (isFocused) {
                    scene.setFill(red300);
                } else {
                    scene.setFill(red200);
                }
                break;
        }
        currentState = viewState;
    }

    private enum ViewState {
        Init,
        Connecting,
        Connected,
        Disconnected
    }


}
