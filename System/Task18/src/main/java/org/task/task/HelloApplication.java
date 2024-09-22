package org.task.task;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends javafx.application.Application {

    //Запуск приложения
    @Override
    public void start(Stage stage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Получаем контроллер из FXML загрузчика
        HelloController controller = fxmlLoader.getController();

        // Настраиваем сцену
        stage.setTitle("Задание");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args){
        launch();
    }
}

