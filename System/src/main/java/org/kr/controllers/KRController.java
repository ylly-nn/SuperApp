package org.kr.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.kr.metods.Tasks;
import org.kr.metods.TaskСonnection;
import org.kr.metods.Utilites;
import org.kr.metods.Working_with_files;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.Stack;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;


public class KRController {

    //-------------------------------------------------------------------------------------------------------------
    //Переменные
    public static long Pid18 = 0;
    public static long Pid21 = 0;
    public static long Pid44 = 0;
    public String usbName ="";
    public static String nowPath="";
    @FXML
    private Label welcomeText;

    @FXML
    private Button Help;

    @FXML
    private MenuItem Info;

    @FXML
    private Button Terminal;

    @FXML
    private MenuItem StartTerminal;

    @FXML
    private Button Task;

    @FXML
    private Button Utilities;

    @FXML
    private Button Back;

    @FXML
    private Button Forward;

    @FXML
    private TextField Path;

    @FXML
    private TextField Name;

    @FXML
    private Button Search;


    @FXML
    private Button buttonOpen;

    @FXML
    private MenuItem LocalTerminal;

    @FXML
    private MenuItem FileManager;

    @FXML
    private MenuItem SysMon;

    @FXML
    private MenuItem Log;

    @FXML
    private MenuItem ProcessTask;

    @FXML
    private MenuItem NetworkTask;

    @FXML
    private MenuItem FactsFiles;

    @FXML
    private TableView <FileInfo> TableView;

    @FXML
    private TableColumn<FileInfo, String> TableName;

    @FXML
    private TableColumn <FileInfo, String>TableSize;

    @FXML
    private TableColumn <FileInfo, Date> TableChange;

    @FXML
    private javafx.scene.control.TreeView<String> TreeView;

    //-----------------------------------------------------------------------------------------------------------------
    // Функции связанные с кнопками




    @FXML
    private void onLocalTerminalMenuItemClick(ActionEvent actionEvent){
        Utilites.TerminalLauncher();
    }

    @FXML
    private void onFileManagerMenuItemClick(ActionEvent actionEvent){
        Utilites.FileManagerLauncher();
    }

    @FXML
    private void onSysMonMenuItemClick(ActionEvent actionEvent){
        Utilites.SystemMonitorLauncher();
    }

    @FXML
    private void onLogMenuItemClick(ActionEvent actionEvent){
        Utilites.LogsLauncher();
    }


    //обработчик кнопки поиска
    @FXML
    private void onSearchButtonClick(ActionEvent actionEvent) {
        String searchFileName = Name.getText(); // Получаем имя файла или папки из текстового поля
        if (searchFileName == null || searchFileName.trim().isEmpty()) {
            showAlert("Введите название для поиска.");
            return;
        }

        File rootDirectory = new File(directoryPath); // Папка SuperApp
        ObservableList<FileInfo> foundItems = searchFilesAndFolders(rootDirectory, searchFileName);

        if (foundItems.isEmpty()) {
            showAlert("Файлы или папки с таким именем не найдены.");
        } else {
            TableView.setItems(foundItems); // Отображаем найденные файлы и папки в TableView
        }
    }

    //поиск файлов
    private ObservableList<FileInfo> searchFilesAndFolders(File directory, String searchFileName) {
        ObservableList<FileInfo> foundItems = FXCollections.observableArrayList();

        // Поиск в указанной директории
        foundItems.addAll(searchInDirectory(directory, searchFileName));

        // Если подключена флешка, выполняем поиск в её директории
        if (!usbName.isEmpty()) {
            File usbDirectory = new File(mediaDirectoryPath + usbName);
            foundItems.addAll(searchInDirectory(usbDirectory, searchFileName));
        }

        return foundItems;
    }

    private ObservableList<FileInfo> searchInDirectory(File directory, String searchFileName) {
        ObservableList<FileInfo> foundItems = FXCollections.observableArrayList();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    foundItems.addAll(searchInDirectory(file, searchFileName)); // Рекурсивный вызов для вложенных папок
                }

                if (file.getName().toLowerCase().contains(searchFileName.toLowerCase())) {
                    String fullPath = file.getAbsolutePath();
                    String superAppRelativePath = fullPath.replace(directoryPath, "/SuperApp");
                    if (fullPath.startsWith("/media")){
                        superAppRelativePath = fullPath.replace(mediaDirectoryPath, "/USB/");
                    }
                    String size = file.isDirectory() ? "Папка" : (file.length() / 1024) + " KB";
                    Date change = new Date(file.lastModified());
                    foundItems.add(new FileInfo(superAppRelativePath, size, change));
                }
            }
        }
        return foundItems;
    }



    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Результат поиска");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onPathEntered(ActionEvent actionEvent) {
        // Получаем введённый пользователем путь
        String inputPath = Path.getText();

        // Преобразуем введённый путь в абсолютный, добавив корневую директорию SuperApp
        String absolutePath;
        if (inputPath.startsWith("/SuperApp")) {
            absolutePath = directoryPath + inputPath.replace("/SuperApp", "");
            System.out.println("absolutePath /SuperApp: "+absolutePath);
        } else if (inputPath.startsWith("/USB") && !usbName.isEmpty()) {
            absolutePath = mediaDirectoryPath + usbName + inputPath.replace("/USB/" + usbName, "");
            System.out.println("absolutePath /USB: " + absolutePath);

        } else {
            // Если путь не начинается с SuperApp или USB, показываем ошибку
            showError("Неверный путь. Путь должен начинаться с '/SuperApp' или 'USB/'.");
            return;
        }

        File fileOrDirectory = new File(absolutePath);
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory()) {
                // Если это папка, отображаем её содержимое в таблице
                nowPath = absolutePath;
                populateTableView(fileOrDirectory);
            } else {
                // Если это файл, открываем родительскую папку и выделяем файл
                nowPath = fileOrDirectory.getParent();
                populateTableView(new File(nowPath));

                // Логика для выделения файла в таблице
                ObservableList<FileInfo> items = TableView.getItems();
                for (FileInfo fileInfo : items) {
                    if (fileInfo.getName().endsWith(fileOrDirectory.getName())) {
                        TableView.getSelectionModel().select(fileInfo);
                        break;
                    }
                }
            }
        } else {
            showError("Путь не найден: " + inputPath);
        }
    }

    // Метод для отображения ошибок пользователю
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    //Справка -> О приложении
    public void onInfoMenuItemClick(ActionEvent actionEvent) {
        {
            try {
                InfoController.openInfoWindow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //Терминал -> Запуск
    public void onStartTerminalMenuItemClick(ActionEvent actionEvent) {
        try {
            TerminalController.openTerminalWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //18) количество запущенных системных процессов
    @FXML
    public void onProcessTaskMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {

        // Указываем директорию, в которой нужно выполнить команду
        File dir = new File(path()+"/System/Task18");




        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task18/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);

        // Печатаем путь к JAR файлу для отладки
     //   System.out.println(path() + "/System/Task/out/artifacts/Task_jar/Task.jar");

        // Выводим текущий PID, если не равен 0 (предыдущий процесс)
      //  System.out.println("Предыдущий PID: " + Pid18);

        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid18 != 0) {
            try {
                // Запуск команды для завершения процесса с PID18
                Process killProcess = Runtime.getRuntime().exec("kill " + Pid18);
                killProcess.waitFor(); // Ожидаем завершения команды

                // Сообщение об успешном завершении процесса
             //   System.out.println("Процесс с PID " + Pid18 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                // Сообщение об ошибке при завершении процесса
             //   System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid18 = process.pid(); // Получаем PID нового процесса

            // Печатаем новый PID процесса
        //    System.out.println("Запущен новый процесс с PID: " + Pid18);

        } catch (IOException e) {
            // Сообщение об ошибке запуска нового процесса
            throw new RuntimeException("Ошибка при запуске Task.jar", e);
        }
        TaskСonnection.connection18();

     //   System.out.println("Команда выполнена успешно.");
    }


    // 21) Состояние беспроводной сети
    @FXML
    private void onNetworkTaskMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {
        // Указываем директорию, в которой нужно выполнить команду
        File dir = new File(path()+"/System/Task21");




        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task21/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);

        // Печатаем путь к JAR файлу для отладки
      //  System.out.println(path() + "/System/Task21/out/artifacts/Task_jar/Task.jar");

        // Выводим текущий PID, если не равен 0 (предыдущий процесс)
     //   System.out.println("Предыдущий PID: " + Pid21);

        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid21 != 0) {
            try {
                // Запуск команды для завершения процесса с PID18
                Process killProcess = Runtime.getRuntime().exec("kill " + Pid21);
                killProcess.waitFor(); // Ожидаем завершения команды

                // Сообщение об успешном завершении процесса
             //   System.out.println("Процесс с PID " + Pid21 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                // Сообщение об ошибке при завершении процесса
             //   System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid21 = process.pid(); // Получаем PID нового процесса

            // Печатаем новый PID процесса
        //    System.out.println("Запущен новый процесс с PID: " + Pid21);

        } catch (IOException e) {
            // Сообщение об ошибке запуска нового процесса
            throw new RuntimeException("Ошибка при запуске Task.jar", e);
        }
        TaskСonnection.connection21();

    //    System.out.println("Команда выполнена успешно.");

    }


    //44) Факт создания файлов
    @FXML
    public void onFactsFilesMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {


        Tasks.Task_44();
        File dir = new File(path()+"/System/Task44");




        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task44/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);

        // Печатаем путь к JAR файлу для отладки
     //   System.out.println(path() + "/System/Task44/out/artifacts/Task_jar/Task.jar");

        // Выводим текущий PID, если не равен 0 (предыдущий процесс)
     //   System.out.println("Предыдущий PID: " + Pid44);

        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid44 != 0) {
            try {
                // Запуск команды для завершения процесса с PID18
                Process killProcess = Runtime.getRuntime().exec("kill " + Pid44);
                killProcess.waitFor(); // Ожидаем завершения команды

                // Сообщение об успешном завершении процесса
             //   System.out.println("Процесс с PID " + Pid44 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                // Сообщение об ошибке при завершении процесса
              //  System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid44 = process.pid(); // Получаем PID нового процесса

            // Печатаем новый PID процесса
         //System.out.println("Запущен новый процесс с PID: " + Pid44);

        } catch (IOException e) {
            // Сообщение об ошибке запуска нового процесса
            throw new RuntimeException("Ошибка при запуске Task.jar", e);
        }
        TaskСonnection.connection44();

    //    System.out.println("Команда выполнена успешно.");

    }

    public void onLeftMouseButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onNameEnter(ActionEvent actionEvent) {
        String searchFileName = Name.getText(); // Получаем имя файла или папки из текстового поля
        if (searchFileName == null || searchFileName.trim().isEmpty()) {
            showAlert("Введите название для поиска.");
            return;
        }

        File rootDirectory = new File(directoryPath); // Папка SuperApp
        ObservableList<FileInfo> foundItems = searchFilesAndFolders(rootDirectory, searchFileName);

        if (foundItems.isEmpty()) {
            showAlert("Файлы или папки с таким именем не найдены.");
        } else {
            TableView.setItems(foundItems); // Отображаем найденные файлы и папки в TableView
        }
    }

    private Stack<String> backStack = new Stack<>(); // Стек для кнопки "назад"
    private Stack<String> forwardStack = new Stack<>(); // Стек для кнопки "вперед"



    public void onBackButtonClick(ActionEvent actionEvent) {
        // Сохраняем текущий путь в backStack перед его изменением
        backStack.push(nowPath);

        String localPath = "";
        int superAppIndex = nowPath.indexOf("/SuperApp");
        int usbIndex = nowPath.indexOf(usbName);

        // Логика определения, какой путь нужно обрезать
        if (superAppIndex != -1) {
            localPath = nowPath.substring(superAppIndex);
            System.out.println("LocalPath (SuperApp): " + localPath);
        } else if (usbIndex != -1) {
            localPath = nowPath.substring(usbIndex);
            localPath = "/USB/" + localPath;
            System.out.println("LocalPath (USB): " + localPath);
        } else {
            System.out.println("Не найдено ни /SuperApp, ни /USB");
            return;
        }

        // Логика обрезки пути до последнего слэша
        if ((localPath.startsWith("/USB") && !localPath.equals("/USB/" + usbName)) ||
                (localPath.startsWith("/SuperApp") && !localPath.equals("/SuperApp"))) {

            int lastSlashIndex = nowPath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                nowPath = nowPath.substring(0, lastSlashIndex); // Обрезаем путь
                localPath = localPath.substring(0, localPath.lastIndexOf("/"));
                System.out.println("nowPath после обрезки: " + nowPath);
            }
            Path.setText(localPath); // Обновляем текстовое поле
            File selectedFile = new File(nowPath);
            populateTableView(selectedFile); // Обновляем таблицу
        } else {
            Path.setText(localPath);
            System.out.println("Путь не может быть обрезан дальше: " + localPath);
        }

        System.out.println("Path (после изменения): " + Path.getText());
    }

    public void onForwardButtonClick(ActionEvent actionEvent) {
        if (!backStack.isEmpty()) {
            String previousPath = backStack.pop(); // Извлекаем последний путь из backStack
            nowPath = previousPath; // Устанавливаем его как текущий путь

            // Логика для обновления localPath
            String localPath = "";
            int superAppIndex = nowPath.indexOf("/SuperApp");
            int usbIndex = nowPath.indexOf(usbName);

            if (superAppIndex != -1) {
                localPath = nowPath.substring(superAppIndex);
                System.out.println("LocalPath (SuperApp): " + localPath);
            } else if (usbIndex != -1) {
                localPath = nowPath.substring(usbIndex);
                localPath = "/USB/" + localPath;
                System.out.println("LocalPath (USB): " + localPath);
            } else {
                System.out.println("Не найдено ни /SuperApp, ни /USB");
                return;
            }

            // Обновляем интерфейс
            Path.setText(localPath);
            File selectedFile = new File(nowPath);
            populateTableView(selectedFile); // Обновляем таблицу
            System.out.println("Path (после изменения на вперёд): " + Path.getText());
        } else {
            System.out.println("Нет доступных путей для вперёд.");
        }
    }

    public void onpopUpMenuOpenMenuItemClick(ActionEvent actionEvent) {
        AlgoritmOpen();
    }

    public void onpopUpMenuCopyMenuItemClick(ActionEvent actionEvent) throws IOException {
        System.out.println("copy"+AlgoritmPath());
        Working_with_files.copyTableView(AlgoritmPath());
        System.out.println(Working_with_files.sourceFile);
    }

    public void onpopUpMenuPasteMenuItemClick(ActionEvent actionEvent) throws IOException {
        Working_with_files.pasteTableView(AlgoritmPathDirectory());
        System.out.println(Working_with_files.sourceFile);
        System.out.println(Working_with_files.destinationFile);
    }

    public void onpopUpMenuDeleteMenuItemClick(ActionEvent actionEvent) throws IOException {
        Working_with_files.deleteTableView(AlgoritmPath());

    }

    public void onpopUpMenuDeleteTrashMenuItemClick(ActionEvent actionEvent) throws IOException {
        Working_with_files.moveToTrash(AlgoritmPath());
    }

    public void onpopUpMenuCreateFolderMenuItemClick(ActionEvent actionEvent) throws IOException {
        Working_with_files.createDirectory(AlgoritmPathDirectory());
    }

    public void onpopUpMenuCreateFileMenuItemClick(ActionEvent actionEvent) {
        Working_with_files.createFile(AlgoritmPathDirectory());
    }
//---------------------------------------------------------------------------------------------------------------------
    //работа с подгрузкой данных в окно



    // Класс для хранения информации о файле
    public static class FileInfo {
        private final String name;
        private final String size;
        private final Date change;

        public FileInfo(String name, String size, Date change) {
            this.name = name;
            this.size = size;
            this.change = change;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public Date getChange() {
            return change;
        }
    }









    private final String directoryPath = path();
    private final String mediaDirectoryPath = "/media/ylly/";

    @FXML
    public void initialize() throws IOException, InterruptedException {



        // Инициализация TreeView
     //   System.out.println(directoryPath);
        File rootDirectory = new File(directoryPath);
        TreeItem<String> rootItem = new TreeItem<>(rootDirectory.getName());
        rootItem.setExpanded(true);
        TreeView.setRoot(rootItem);
        populateTreeView(rootDirectory, rootItem);

        // Инициализация TableView
        TableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        TableChange.setCellValueFactory(new PropertyValueFactory<>("change"));

        // Отображение файлов в TableView при выборе в TreeView

        TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String parnt = "";
            String val = "";
            String USBornotUSB ="";

            if (newValue!=null){
            String nowValue=newValue.getValue();
            if (newValue.getValue().equals(usbName)){
               // System.out.println(mediaDirectoryPath+usbName);
                File mediaDirectory = new File(mediaDirectoryPath+usbName);
                nowPath=mediaDirectoryPath+usbName;
                Path.setText("/USB/"+usbName);
                populateTableView(mediaDirectory);
            }
            else {
            try {
                while (!parnt.equals("SuperApp")) {
                    parnt = newValue.getParent().getValue();
                    if (parnt.equals(usbName)){
                        USBornotUSB="USB";
                        break;
                    }
                    if (parnt.equals("SuperApp")) {
                        USBornotUSB="notUSB";
                        break;
                    }
                    val = parnt + "/" + val;
                    newValue = newValue.getParent();
                }
                switch (USBornotUSB) {
                    case "notUSB":
                    //    System.out.println(directoryPath + "/" + val + nowValue);
                        File selectedDirectory = new File(directoryPath + "/" + val + nowValue);
                        nowPath = directoryPath + "/" + val + nowValue;
                        Path.setText("/SuperApp/"+val + nowValue);
                        populateTableView(selectedDirectory);
                    break;

                    case "USB":
                     //   System.out.println( mediaDirectoryPath+ "/"+usbName+"/" + val + nowValue);
                        File mediaDirectory = new File(mediaDirectoryPath+usbName+"/" + val + nowValue);
                        nowPath=mediaDirectoryPath+usbName+"/" + val + nowValue;
                        Path.setText("/USB/"+usbName+"/" + val + nowValue);
                        populateTableView(mediaDirectory);
                        break;
                    }
                }
            catch (Exception e)
            {
              //  System.out.println(newValue.getValue());
                File selectedDirectory = new File(directoryPath);
                nowPath=directoryPath;
                Path.setText("/SuperApp");
                populateTableView(selectedDirectory);
            }
            }
        }}
        );

        TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String parnt = "";
            String val = "";
            String USBornotUSB ="";

            if (newValue!=null){
                String nowValue=newValue.getValue();
                if (newValue.getValue().equals(usbName)){
                    // System.out.println(mediaDirectoryPath+usbName);
                    File mediaDirectory = new File(mediaDirectoryPath+usbName);
                    nowPath=mediaDirectoryPath+usbName;
                    Path.setText("/USB/"+usbName);
                    populateTableView(mediaDirectory);
                }
                else {
                    try {
                        while (!parnt.equals("SuperApp")) {
                            parnt = newValue.getParent().getValue();
                            if (parnt.equals(usbName)){
                                USBornotUSB="USB";
                                break;
                            }
                            if (parnt.equals("SuperApp")) {
                                USBornotUSB="notUSB";
                                break;
                            }
                            val = parnt + "/" + val;
                            newValue = newValue.getParent();
                        }
                        switch (USBornotUSB) {
                            case "notUSB":
                                //    System.out.println(directoryPath + "/" + val + nowValue);
                                File selectedDirectory = new File(directoryPath + "/" + val + nowValue);
                                nowPath = directoryPath + "/" + val + nowValue;
                                Path.setText("/SuperApp/"+val + nowValue);
                                populateTableView(selectedDirectory);
                                break;

                            case "USB":
                                //   System.out.println( mediaDirectoryPath+ "/"+usbName+"/" + val + nowValue);
                                File mediaDirectory = new File(mediaDirectoryPath+usbName+"/" + val + nowValue);
                                nowPath=mediaDirectoryPath+usbName+"/" + val + nowValue;
                                Path.setText("/USB/"+usbName+"/" + val + nowValue);
                                populateTableView(mediaDirectory);
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        //  System.out.println(newValue.getValue());
                        File selectedDirectory = new File(directoryPath);
                        nowPath=directoryPath;
                        Path.setText("/SuperApp");
                        populateTableView(selectedDirectory);
                    }
                }
            }
        }
        );

        TableView.setOnKeyPressed(event -> {
                    if(event.isControlDown() && event.getCode() == KeyCode.T) {
                        System.out.println("Открытие терминала");
                        try {
                            TerminalController.openTerminalWindow();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                        if(event.isControlDown() && event.getCode() == KeyCode.C)
                        {
                            System.out.println("Открытие терминала");
                            try {
                                Working_with_files.copyTableView(AlgoritmPath());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if(event.isControlDown() && event.getCode() == KeyCode.V)
                        {
                            System.out.println("Открытие терминала");
                            Working_with_files.pasteTableView(AlgoritmPathDirectory());
                        }
            if(event.isControlDown() && event.getCode() == KeyCode.D)
            {
                System.out.println("Открытие терминала");
                Working_with_files.createDirectory(AlgoritmPathDirectory());
            }
            if(event.isControlDown() && event.getCode() == KeyCode.F)
            {
                System.out.println("Открытие терминала");
                Working_with_files.createFile(AlgoritmPathDirectory());
            }
                    });




        TreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Двойной клик
                TreeItem<String> selectedItem = TreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Working_with_files.openTreeView(); // Запуск команды открытия файла
                }
            }
        });

        //нажатие мыши в TableView
        TableView.setOnMouseClicked(event -> {

            // Обработка двойного клика
            if (event.getClickCount() == 2) {
                AlgoritmOpen();
            }
        });



        File mediaDirectory = new File(mediaDirectoryPath);
        File[] usbDrives = mediaDirectory.listFiles();
        if (usbDrives != null) {
            for (File usbDrive : usbDrives) {
                if (usbDrive.isDirectory()) { // Только директории, так как это должны быть флешки
                    TreeItem<String> usbRootItem = new TreeItem<>(usbDrive.getName());
                    usbName=usbDrive.getName();
                    TreeView.getRoot().getChildren().add(usbRootItem);
                    populateTreeView(usbDrive, usbRootItem);


//
                }
            }
        }
        Thread thread=new Thread(() -> {
            try {
                monitorUSB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();



    }

    //Используется в TreeView(отображение)
    private void populateTreeView(File directory, TreeItem<String> rootItem) {
        rootItem.getChildren().clear();  // Очищаем старые элементы
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> item = new TreeItem<>(file.getName());
                rootItem.getChildren().add(item);
                if (file.isDirectory()) {
                    populateTreeView(file, item);
                }
            }
        }
    }

    //Нужное но ?????
    private void populateTableView(File directory) {
        File[] files = directory.listFiles();
        ObservableList<FileInfo> fileList = FXCollections.observableArrayList();
        if (files != null) {
            for (File file : files) {
                //String name = file.getName();
                String size = (file.length() / 1024) + " KB"; // Размер файла в KB
                Date change =new Date(file.lastModified());
                fileList.add(new FileInfo(file.getName(), size, change));
            }
        }
        TableView.setItems(fileList);
    }




    /*получение полного пути до текущей директории (Включая текущую)
   В данном случае /home/ylly/SuperApp*/
    public static String path () {
        // Задайте относительный путь к папке
        String relativePath = ""; // Замените "yourFolder" на вашу папку

        // Создайте объект File с этим относительным путем
        File folder = new File(relativePath);

        // Получите абсолютный путь к папке
        String basePath= folder.getAbsolutePath();

      //  System.out.println("Полный путь до папки: " + basePath);
        return basePath;
    }


    //Создание наблюдателя за состояниями флешки
    public void monitorUSB() throws IOException, InterruptedException {
        Path path = Paths.get("/media/ylly");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE);

      //  System.out.println("Мониторинг изменений в: " + path.toString());

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path fileName = (Path) event.context();

                if (kind == ENTRY_CREATE || kind == ENTRY_DELETE) {
                    handleUSBChange(kind, fileName);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }


    //динамическое обновление флешки
    private void handleUSBChange(WatchEvent.Kind<?> kind, Path fileName) {
        Platform.runLater(() -> {
            String action = (kind == ENTRY_CREATE) ? "подключено" : "отключено";
          //  System.out.println("Устройство " + action + ": " + fileName);

            // Обновляем дерево
            File rootDirectory = new File(directoryPath);
            TreeItem<String> rootItem = new TreeItem<>(rootDirectory.getName());
            rootItem.setExpanded(true);
            TreeView.setRoot(rootItem);
            populateTreeView(rootDirectory, rootItem);


            File mediaDirectory = new File(mediaDirectoryPath);
            File[] usbDrives = mediaDirectory.listFiles();
            if (usbDrives != null) {
                for (File usbDrive : usbDrives) {
                    if (usbDrive.isDirectory()) { // Только директории, так как это должны быть флешки
                        TreeItem<String> usbRootItem = new TreeItem<>(usbDrive.getName());
                        usbName=usbDrive.getName();
                        TreeView.getRoot().getChildren().add(usbRootItem);
                        populateTreeView(usbDrive, usbRootItem);
                    }
                }
            }


            // Заполняем дерево
        });
    }
    public static Long getPid18(){
        return Pid18;
    }

    public static Long getPid21(){
        return Pid21;
    }

    public static Long getPid44(){
        return Pid44;
    }

    public void AlgoritmOpen(){
        String localPath = "";// Двойной клик

        // Получаем выбранный элемент
        FileInfo selectedItem = TableView.getSelectionModel().getSelectedItem();
//                System.out.println(selectedItem.name);
//                Working_with_files.openTableView("/home/ylly/"+selectedItem.name);


        if (selectedItem != null) {
            if (selectedItem.name.startsWith("/SuperApp")) {
                String output = selectedItem.name.substring("/SuperApp".length());
                File selectedFile = new File(directoryPath + output);
                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    // Находим индекс "SuperApp" в пути и обрезаем его
                    int index = localPath.indexOf("/SuperApp");
                    if (index != -1) {
                        localPath = nowPath.substring(index);
                        System.out.println(localPath);
                    }

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println(selectedFile.getPath());
                    // Если это файл, открываем его с помощью программы по умолчанию
                    Working_with_files.openTableView(selectedFile.getPath());
                }
                // Выводим результат
            } else if (selectedItem.name.startsWith("/USB")) {
                System.out.println("Строка начинается с /USB");
                String output = selectedItem.name.substring("/USB/".length());
                File selectedFile = new File(mediaDirectoryPath + output);
                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    // Находим индекс "SuperApp" в пути и обрезаем его
                    int index = localPath.indexOf("/"+usbName);
                    if (index != -1) {
                        localPath = nowPath.substring(index);
                        System.out.println(localPath);
                    }
                    localPath="/USB"+localPath;

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println(selectedFile.getPath());
                    // Если это файл, открываем его с помощью программы по умолчанию
                    Working_with_files.openTableView(selectedFile.getPath());
                }

            }
            // Формируем путь к выбранному файлу/папке
            else {
                // Формируем путь к выбранному файлу/папке
                File selectedFile = new File(nowPath + "/" + selectedItem.getName());

                System.out.println(selectedFile.getPath());
                System.out.println(selectedFile.toString());

                // Если это папка, обновляем отображение в TableView
                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    // Находим индекс "SuperApp" в пути и обрезаем его
                    int indexSuperApp = localPath.indexOf("/SuperApp");
                    int indexUSB = localPath.indexOf(usbName);
                    if (indexSuperApp != -1) {
                        localPath = nowPath.substring(indexSuperApp);
                        System.out.println(localPath);
                    }
                    if (indexUSB != -1) {
                        localPath = nowPath.substring(indexUSB);
                        localPath="/USB/"+localPath;
                        System.out.println(localPath);
                    }

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println(selectedFile.getPath());
                    // Если это файл, открываем его с помощью программы по умолчанию
                    Working_with_files.openTableView(selectedFile.getPath());
                }
            }
        }
    }

    public String AlgoritmPath(){

        // Получаем выбранный элемент
        FileInfo selectedItem = TableView.getSelectionModel().getSelectedItem();
//                System.out.println(selectedItem.name);
//                Working_with_files.openTableView("/home/ylly/"+selectedItem.name);


        if (selectedItem != null) {

            if (selectedItem.name.startsWith("/SuperApp")) {
                if (selectedItem.name.startsWith("/SuperApp/System")){
                    return "";
                }
                String output = selectedItem.name.substring("/SuperApp".length());
                    return directoryPath+output;
                }
                // Выводим результат
             else if (selectedItem.name.startsWith("/USB")) {
                System.out.println("Строка начинается с /USB");
                String output = selectedItem.name.substring("/USB/".length());
                    // Если это файл, открываем его с помощью программы по умолчанию
                    return mediaDirectoryPath + output;

            }
            // Формируем путь к выбранному файлу/папке
            else {
                // Формируем путь к выбранному файлу/папке
                if ((nowPath.contains("/SuperApp/System"))||selectedItem.name.equals("System")){
                    return "";
                }
                return nowPath + "/" + selectedItem.getName();

                }
            }

        return "";
    }

    public String AlgoritmPathDirectory() {

        // Получаем выбранный элемент
        FileInfo selectedItem = TableView.getSelectionModel().getSelectedItem();
//                System.out.println(selectedItem.name);
//                Working_with_files.openTableView("/home/ylly/"+selectedItem.name);
        if (Path.getText().startsWith("/SuperApp")) {
            if (Path.getText().startsWith("/SuperApp/System")){
                return "";
            }
            String output = Path.getText().substring("/SuperApp".length());
            return directoryPath + output;
        }
        // Выводим результат
        else if (Path.getText().startsWith("/USB")) {
            System.out.println("Строка начинается с /USB");
            String output = Path.getText().substring("/USB/".length());
            // Если это файл, открываем его с помощью программы по умолчанию
            return mediaDirectoryPath + output;

        }
        return "";
    }




    }



