package org.kr.metods;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.kr.controllers.KRController;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;    import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

public class Working_with_files {
    public  static String sourceFile = "";
    public static String destinationFile = "";
    public static void ShowAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Метод для создания файла с вводом имени файла
    public static void createFile(String directoryPath) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание файла");
        dialog.setHeaderText("Введите имя файла:");
        dialog.setContentText("Имя файла:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            Path path = Paths.get(directoryPath, fileName); // Создаем полный путь

            try {
                // Проверяем, существует ли файл
                if (Files.exists(path)) {
                    ShowAlert("Файл уже существует: " + path);
                    return;
                }
                // Создаём новый файл
                Files.createFile(path);
                ShowAlert("Файл успешно создан: " + path);
            } catch (IOException e) {
                ShowAlert("Ошибка при создании файла: " + e.getMessage());
            }
        });
    }

    // Метод для создания папки с вводом имени папки
    public static void createDirectory(String parentDirectoryPath) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание папки");
        dialog.setHeaderText("Введите имя папки:");
        dialog.setContentText("Имя папки:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(dirName -> {
            Path path = Paths.get(parentDirectoryPath, dirName); // Создаем полный путь

            try {
                // Проверяем, существует ли директория
                if (Files.exists(path)) {
                    ShowAlert("Папка уже существует: " + path);
                    return;
                }
                // Создаём новую директорию
                Files.createDirectory(path);
                ShowAlert("Папка успешно создана: " + path);
            } catch (IOException e) {
                ShowAlert("Ошибка при создании папки: " + e.getMessage());
            }
        });
    }

    public static void deleteTableView(String filePath) throws IOException {
            Path path = Paths.get(filePath);

            // Проверяем, существует ли файл или папка
            if (!Files.exists(path)) {
                System.out.println("Файл или папка не найдены: " + filePath);
                return;
            }

            try {
                File file = new File(filePath);

                // Проверяем, является ли объект директорией
                if (file.isDirectory()) {
                    // Удаляем непустую директорию рекурсивно
                    deleteDirectoryRecursively(path);
                    ShowAlert("Папка и её содержимое успешно удалены.");
                    System.out.println("Папка и её содержимое успешно удалены.");
                } else {
                    // Удаление файла
                    Files.delete(path);
                    ShowAlert("Файл успешно удалён.");
                    System.out.println("Файл успешно удалён.");
                }
            } catch (NoSuchFileException e) {
                System.out.println("Файл не существует: " + e.getMessage());
            } catch (DirectoryNotEmptyException e) {
                System.out.println("Папка не пуста: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Ошибка при удалении: " + e.getMessage());
            }
        }

        // Метод для рекурсивного удаления папки с содержимым
        public static void deleteDirectoryRecursively(Path path) throws IOException {
            Files.walk(path)
                    .sorted((p1, p2) -> p2.compareTo(p1))  // Сортировка для удаления файлов перед папками
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при удалении: " + p, e);
                        }
                    });
        }

    public static void moveToTrash(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }
        ProcessBuilder processBuilder = new ProcessBuilder("cp","-r", path.toString(), "/home/ylly/SuperApp/Trash");

        try {
            // Запускаем процесс
            Process process = processBuilder.start();
            // Ожидаем завершения процесса
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ShowAlert("перемещён в корзину");
                deleteTableView(path.toString());
            } else {
                ShowAlert("Ошибка при перемещении в корзину");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }






    public static void copyTableView(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        System.out.println("path "+path);

        // Проверяем, существует ли файл
        if (!Files.exists(path)) {
            System.out.println("Файл не найден копия");
            return;
        }
        sourceFile=path.toString();
    }

    public static void pasteTableView(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }
        destinationFile=filePath;
        ProcessBuilder processBuilder = new ProcessBuilder("cp","-r", sourceFile, destinationFile);

        try {
            // Запускаем процесс
            Process process = processBuilder.start();
            // Ожидаем завершения процесса
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ShowAlert("Файл успешно скопирован.");
            } else {
                ShowAlert("Ошибка при копировании файла.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void openTableView(String filePath){
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
                IsDirectory=true;
            } else {
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


    public static void openTreeView(){


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
                IsDirectory=true;
            } else {
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
