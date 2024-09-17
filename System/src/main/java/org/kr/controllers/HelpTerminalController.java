package org.kr.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kr.KRapplication;

import java.io.IOException;

public class HelpTerminalController {

    public static void openHelpTerminalWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(KRapplication.class.getResource("helpTerminal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Справка");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();}
}
