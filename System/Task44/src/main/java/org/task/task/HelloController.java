package org.task.task;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.task.task.*;



public class HelloController {
    Path filePathpath = Paths.get(path()+"/Task44/ipc44");
    String filePath = filePathpath.toString();
    File file = new File(path()+"/Task44/ipc44");

    @FXML
    public TextArea textTask;

    @FXML
    public Button restart;

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
        Path filePath = Paths.get(path()+"/Task44/ipc44");

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

    public void onrestartButtonClick(ActionEvent actionEvent) {

        // Создаем задачу для чтения файла в фоновом режиме
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                StringBuilder readList = new StringBuilder();

                if (file.exists() && file.canRead()) {
                    System.out.println("Файл доступен для чтения.");
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            readList.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        System.out.println("Ошибка при чтении файла: " + e.getMessage());
                    }
                } else {
                    return "Файл недоступен для чтения или не существует.";
                }

                return readList.toString(); // Возвращаем результат
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            textTask.setText(task.getValue()); // Отображаем результат в TextArea
        });

        task.setOnFailed(workerStateEvent -> {
            textTask.setText("Ошибка при чтении файла.");
        });

        // Запускаем задачу в фоновом потоке
        new Thread(task).start();



        }
    }


