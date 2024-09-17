package org.kr.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kr.KRapplication;
import org.kr.metods.Terminal;

import java.io.IOException;



public class TerminalController {
    @FXML
    private TextField CommandEnterTerminal;

    @FXML
    private Button OKTerminal;

    @FXML
    private Button HelpTerminal;

    @FXML
    private ListView ListTerminal;



    //инициализация окна терминала
    public static void openTerminalWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(KRapplication.class.getResource("Terminal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Терминал");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();}
    @FXML
    public void initialize() {
        // Добавляем обработку нажатия Enter на текстовом поле
        CommandEnterTerminal.setOnAction(event -> onOKTerminalButtonClick(null));
    }


    public void onHelpTerminalButtonClick(ActionEvent actionEvent) {
        try {
            HelpTerminalController.openHelpTerminalWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void onOKTerminalButtonClick(ActionEvent actionEvent) {
        String command = CommandEnterTerminal.getText();
        if (command != null && !command.trim().isEmpty()) {
            Terminal.executeCommand(command.trim(), ListTerminal);
            CommandEnterTerminal.clear();
        }
    }
}
