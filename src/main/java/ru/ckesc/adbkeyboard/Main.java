package ru.ckesc.adbkeyboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.config.Config;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;

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

        Config config = loadConfig();

        controller = loader.getController();
        controller.init(primaryStage, config);
    }

    public static Config loadConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File("config.json");

        //Create config on disk if it not exists
        if (!file.exists()) {
            Config defaultConfig = createDefaultConfig();
            String stringConfig = gson.toJson(defaultConfig);
            try {
                PrintWriter printWriter = new PrintWriter(file, "UTF-8");
                printWriter.write(stringConfig);
                printWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return defaultConfig;
        }

        //Read config. Fallback to default
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"));
            Config config = gson.fromJson(reader, Config.class);
            reader.close();
            return config;
        } catch (Exception e) {
            System.out.println("Can`t read config! Fallback to default.");
            e.printStackTrace();
            return createDefaultConfig();
        }

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
                .addKeyMapItem(KeyCode.PERIOD, "KEYCODE_PERIOD")
                .addKeyMapItem(KeyCode.SPACE, "KEYCODE_SPACE")

                .addKeyMapItem(KeyCode.HOME, "KEYCODE_HOME")
                .addKeyMapItem(KeyCode.END, "KEYCODE_NOTIFICATION")
                .addKeyMapItem(KeyCode.INSERT, "KEYCODE_MENU")

                .addKeyMapItem(KeyCode.SHIFT, "KEYCODE_SHIFT_LEFT")
                .addKeyMapItem(KeyCode.CONTROL, "KEYCODE_CTRL_LEFT")
                .addKeyMapItem(KeyCode.ALT, "KEYCODE_ALT_LEFT")

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
