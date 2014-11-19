package ru.ckesc.adbkeyboard;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private Map<KeyCode, Integer> adbEventMap = new HashMap<>();
    public static void main(String[] args) {
        launch(args);
    }

    public DeviceConnection deviceConnection = new ShellDeviceConnection();

    private void initMap() {
//        19 -->  "KEYCODE_DPAD_UP"
//        20 -->  "KEYCODE_DPAD_DOWN"
//        21 -->  "KEYCODE_DPAD_LEFT"
//        22 -->  "KEYCODE_DPAD_RIGHT"

        adbEventMap.put(KeyCode.UP, 19);
        adbEventMap.put(KeyCode.DOWN, 20);
        adbEventMap.put(KeyCode.LEFT, 21);
        adbEventMap.put(KeyCode.RIGHT, 22);
        adbEventMap.put(KeyCode.ENTER, 66);
        adbEventMap.put(KeyCode.ESCAPE, 4);
        adbEventMap.put(KeyCode.BACK_SPACE, 67);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Adb keyboard");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//
//        Keyboard keyboard = new Keyboard(
//                new Key(KeyCode.LEFT),
//                new Key(KeyCode.RIGHT));

        final Keyboard keyboard = new Keyboard(new Key(KeyCode.A),
                new Key(KeyCode.S),
                new Key(KeyCode.D),
                new Key(KeyCode.F),
                new Key(KeyCode.LEFT),
                new Key(KeyCode.RIGHT),
                new Key(KeyCode.UP),
                new Key(KeyCode.DOWN),
                new Key(KeyCode.ENTER),
                new Key(KeyCode.ESCAPE));

        final Scene scene = new Scene(new Group(keyboard.createNode()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Keyboard Example");
        primaryStage.show();

        initMap();
        scene.setOnKeyReleased(new KeyEventHandler());
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (adbEventMap.containsKey(keyEvent.getCode())) {
                deviceConnection.sendEventToDevice(adbEventMap.get(keyEvent.getCode()));
            } else {
                deviceConnection.sendTextToDevice(keyEvent.getText());
            }
        }
    }
}
