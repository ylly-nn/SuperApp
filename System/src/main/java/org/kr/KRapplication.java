package org.kr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.kr.controllers.KRController;
import org.kr.controllers.TerminalController;
import org.kr.metods.Working_with_files;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class KRapplication extends javafx.application.Application {
    public static String formattedDateTime;

    //Запуск приложения
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(KRapplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SuperApp");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        //получение времени запуска программы
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formattedDateTime = now.format(formatter);
        System.out.println(formattedDateTime);






    }





    public static void main(String[] args){
        launch();
    }
}