package org.task.task;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.task.task.*;



public class HelloController {

    @FXML
    public TextArea textTask;

    public static String path () {
        // Задайте относительный путь к папке
        String relativePath = ""; // Замените "yourFolder" на вашу папку

        // Создайте объект File с этим относительным путем
        File folder = new File(relativePath);

        // Получите абсолютный путь к папке
        String basePath= folder.getAbsolutePath();

        System.out.println("Полный путь до папки: " + basePath);
        int lastSlashIndex = basePath.lastIndexOf('/');

        // Если символ '/' найден, обрезаем строку до этого индекса
        if (lastSlashIndex != -1) {
            basePath = basePath.substring(0, lastSlashIndex);
            System.out.println(basePath); // Вывод: /home/ylly/SuperApp/System
        } else {
            System.out.println("Символ '/' не найден в строке.");
        }
        return basePath;
    }



    // Method to open the task window



    @FXML
    public  void initialize(){
        Path filePath = Paths.get(path()+"/Task/ipc");

        try {
            // Читаем все строки файла в список
            String fileContent = Files.readString(filePath);

            // Выводим содержимое файла на консоль
            System.out.println(fileContent);
            textTask.setText(fileContent);







        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
