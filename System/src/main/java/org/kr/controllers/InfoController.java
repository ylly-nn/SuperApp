package org.kr.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kr.KRapplication;

import java.io.IOException;

public class InfoController {
    public static void openInfoWindow() throws IOException {
    Stage stage = new Stage();
    FXMLLoader fxmlLoader = new FXMLLoader(KRapplication.class.getResource("info.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("О программе");
    stage.setResizable(false);
    stage.setScene(scene);
    stage.show();}

}
