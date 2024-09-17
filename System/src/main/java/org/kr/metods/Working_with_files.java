package org.kr.metods;

import org.kr.controllers.KRController;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Working_with_files {
    public static void open(){


        // Путь к файлу передается как аргумент
        String filePath = KRController.nowPath;
        Path path = Paths.get(filePath);
        boolean IsDirectory;

        // Проверяем, существует ли файл
        if (!Files.exists(path)) {
            System.out.println("Файл не найден: " + filePath);
            return;
        }

        try {
            // Определяем тип файла
            //ProcessBuilder processBuilderIsDirectory=new ProcessBuilder("file",filePath +"");

            File file = new File(filePath);
            if (file.isDirectory()) {
                System.out.println("This is a directory.");
                IsDirectory=true;
            } else {
                System.out.println("This is not a directory.");
                IsDirectory=false;
            }

            // Открытие файла с помощью программы по умолчанию
            //openFileWithDefaultApp(filePath);
            if (IsDirectory==false) {
                ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", filePath);
                Process process = processBuilder.start();
                System.out.println("Типа открытие файла");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при определении типа файла или открытии: " + e.getMessage());
        }
    }

}
