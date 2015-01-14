package ru.ckesc.adbkeyboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.config.Config;

import java.net.URL;

public class Main extends Application {

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("main.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = (Parent) loader.load();

        primaryStage.setTitle("Adb Key Monkey!");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        primaryStage.show();

        Config defaultConfig = createDefaultConfig();

        controller = loader.getController();
        controller.init(primaryStage, defaultConfig);
    }

    public static Config createDefaultConfig() {
        return new Config.Builder()
                .setReconnectPeriod(5)
                .addKeyMapItem(KeyCode.UP, "KEYCODE_DPAD_UP")
                .addKeyMapItem(KeyCode.DOWN, "KEYCODE_DPAD_DOWN")
                .addKeyMapItem(KeyCode.LEFT, "KEYCODE_DPAD_LEFT")
                .addKeyMapItem(KeyCode.RIGHT, "KEYCODE_DPAD_RIGHT")
                .addKeyMapItem(KeyCode.ENTER, "KEYCODE_DPAD_CENTER")
                .addKeyMapItem(KeyCode.ESCAPE, "KEYCODE_BACK")
                .addKeyMapItem(KeyCode.BACK_SPACE, "KEYCODE_DEL")

                .addKeyMapItem(KeyCode.DIGIT0, "KEYCODE_0")
                .addKeyMapItem(KeyCode.DIGIT1, "KEYCODE_1")
                .addKeyMapItem(KeyCode.DIGIT2, "KEYCODE_2")
                .addKeyMapItem(KeyCode.DIGIT3, "KEYCODE_3")
                .addKeyMapItem(KeyCode.DIGIT4, "KEYCODE_4")
                .addKeyMapItem(KeyCode.DIGIT5, "KEYCODE_5")
                .addKeyMapItem(KeyCode.DIGIT6, "KEYCODE_6")
                .addKeyMapItem(KeyCode.DIGIT7, "KEYCODE_7")
                .addKeyMapItem(KeyCode.DIGIT8, "KEYCODE_8")
                .addKeyMapItem(KeyCode.DIGIT9, "KEYCODE_9")

                .addKeyMapItem(KeyCode.NUMPAD0, "KEYCODE_0")
                .addKeyMapItem(KeyCode.NUMPAD1, "KEYCODE_1")
                .addKeyMapItem(KeyCode.NUMPAD2, "KEYCODE_2")
                .addKeyMapItem(KeyCode.NUMPAD3, "KEYCODE_3")
                .addKeyMapItem(KeyCode.NUMPAD4, "KEYCODE_4")
                .addKeyMapItem(KeyCode.NUMPAD5, "KEYCODE_5")
                .addKeyMapItem(KeyCode.NUMPAD6, "KEYCODE_6")
                .addKeyMapItem(KeyCode.NUMPAD7, "KEYCODE_7")
                .addKeyMapItem(KeyCode.NUMPAD8, "KEYCODE_8")
                .addKeyMapItem(KeyCode.NUMPAD9, "KEYCODE_9")

                .build();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.stop();
    }


}
