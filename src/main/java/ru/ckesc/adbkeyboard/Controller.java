package ru.ckesc.adbkeyboard;

import javafx.animation.FillTransitionBuilder;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.connection.ConnectionListener;
import ru.ckesc.adbkeyboard.connection.KeyAction;
import ru.ckesc.adbkeyboard.connection.MonkeyDeviceConnection;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller implements ConnectionListener {

    private Logger logger = new FxLogger(Controller.class.getName());
    private Map<KeyCode, Integer> adbEventMap = new HashMap<>();
    private ExecutorService executor;
    private ScheduledExecutorService scheduledExecutorService;

    //Colors from material design http://www.google.com/design/spec/style/color.html#color-color-palette
    private final static String COLOR_INIT = "#BDBDBD";
    private final static String COLOR_DISCONNECTED_UNFOCUSED = "#E57373";
    private final static String COLOR_DISCONNECTED_FOCUSED = "#F44336";
    private final static String COLOR_CONNECTING_UNFOCUSED = "#BDBDBD";
    private final static String COLOR_CONNECTING_FOCUSED = "#9E9E9E";
    private final static String COLOR_CONNECTED_UNFOCUSED = "#81C784";
    private final static String COLOR_CONNECTED_FOCUSED = "#4CAF50";

    private ViewState currentState;

    private Stage stage;
    @FXML
    public TextArea logArea;
    public Label statusLabel;
    public AnchorPane mainPane;
    public AnchorPane topBar;
    public Rectangle rectBg;

    private MonkeyDeviceConnection deviceConnection;

    public void init(Stage stage) {
        this.stage = stage;

        executor = Executors.newFixedThreadPool(1);

        //        deviceConnection = new ShellDeviceConnection();
        deviceConnection = new MonkeyDeviceConnection(new FxLogger(MonkeyDeviceConnection.class.getName()));

        initMap();

        mainPane.addEventFilter(KeyEvent.KEY_PRESSED, new KeyDownEventHandler());
        mainPane.addEventFilter(KeyEvent.KEY_RELEASED, new KeyUpEventHandler());

        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean focused) {
                showState(currentState); //update current state with focused value
            }
        });

        deviceConnection.setConnectionListener(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showState(ViewState.Init);
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                deviceConnection.connect();
            }
        });

        //Check connectivity every 5 sec
        logger.info("Checking connectivity every 5 sec");
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (deviceConnection) {
                    if (!deviceConnection.isAlive()) {
                        logger.info("Auto reconnect...");
                        deviceConnection.disconnect();
                        deviceConnection.connect();
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

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
        return stage != null && stage.isFocused();
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
            keyEvent.consume();
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
            keyEvent.consume();
        }
    }


    private void showState(ViewState viewState) {
        switch (viewState) {
            case Init:
                statusLabel.setText("Init...");
                changeBgColor(COLOR_INIT, false);
                break;
            case Connecting:
                statusLabel.setText("Connecting...");
                if (isFocused()) {
                    changeBgColor(COLOR_CONNECTING_FOCUSED, true);
                } else {
                    changeBgColor(COLOR_CONNECTING_UNFOCUSED, true);
                }
                break;
            case Connected:
                if (isFocused()) {
                    statusLabel.setText("Connected!");
                    changeBgColor(COLOR_CONNECTED_FOCUSED, true);
                } else {
                    statusLabel.setText("Connected! Not focused");
                    changeBgColor(COLOR_CONNECTED_UNFOCUSED, true);
                }

                break;
            case Disconnected:
                statusLabel.setText("Disconnected");
                if (isFocused()) {
                    changeBgColor(COLOR_DISCONNECTED_FOCUSED, true);
                } else {
                    changeBgColor(COLOR_DISCONNECTED_UNFOCUSED, true);
                }
                break;
        }
        currentState = viewState;
    }

    /**
     * Change color of top bar
     *
     * @param color        new color in web format (example @code{#aa0011})
     * @param useAnimation use or not animation
     */
    private void changeBgColor(String color, boolean useAnimation) {
        if (!useAnimation) {
            topBar.styleProperty().set("-fx-background-color:" + color);
            rectBg.setFill(Color.web(color));
            return;
        }

        //Fix rect size (in case windows been resized)
        rectBg.setWidth(topBar.getWidth());
        rectBg.setHeight(topBar.getHeight());

        //Filling end bar with end color
        topBar.styleProperty().set("-fx-background-color:" + color);

        //Run transition
        FillTransitionBuilder.create()
                .fromValue((Color) rectBg.getFill())
                .toValue(Color.web(color))
                .shape(rectBg)
                .build().play();
    }


    private enum ViewState {
        Init,
        Connecting,
        Connected,
        Disconnected
    }

    private class FxLogger implements Logger {
        private String name;

        private FxLogger(String name) {
            this.name = name;
        }

        private void log(String message) {
            StringBuilder stringBuilder = new StringBuilder()
                    .append("[")
                    .append(DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault()).format(new Date()))
                    .append("] ")
                    .append(message)
                    .append("\n");
            logArea.appendText(stringBuilder.toString());
        }

        @Override
        public void info(final String message) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    log(message);
                }
            });
        }

        @Override
        public void error(final String message) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    log(message);
                }
            });
        }
    }


}
