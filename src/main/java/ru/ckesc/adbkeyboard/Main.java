package ru.ckesc.adbkeyboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.connection.DeviceConnection;

import java.net.URL;

public class Main extends Application  {

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    public DeviceConnection deviceConnection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("main.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = (Parent)loader.load();

        primaryStage.setTitle("Adb Key Monkey!");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        primaryStage.show();

        controller = loader.getController();
        controller.init(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.stop();
    }




}
