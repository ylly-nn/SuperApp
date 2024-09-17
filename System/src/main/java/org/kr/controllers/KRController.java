package org.kr.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.kr.metods.Tasks;
import org.kr.metods.Utilites;
import org.kr.metods.Working_with_files;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;


public class KRController {

    //-------------------------------------------------------------------------------------------------------------
    //Переменные
    public long Pid18 = 0;
    public long Pid21 = 0;
    public long Pid44 = 0;
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
    private void onbuttonOpenButtonClick(ActionEvent actionEvent){
        Working_with_files.open();
    }

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
    public void onProcessTaskMenuItemClick(ActionEvent actionEvent) throws IOException {

        Tasks.Task_18();



        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path()+"/System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(path()+"System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(Pid18);
        if(Pid18 !=0) {
            try {
                // Запускаем команду в командной строке
                Process process = Runtime.getRuntime().exec("kill " + Pid18);
                process.waitFor(); // Ожидаем завершения команды
                System.out.println("Процесс с PID " + Pid18 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }


        try {
            Process process = processBuilder.start();
            Pid18 =process.pid();
            //InputStream inputStream = process.getInputStream();
            // InputStream errorStream = process.getErrorStream();

        } catch (IOException e) {
            // More specific error message
            throw new RuntimeException("Error starting the process for Task.jar", e);
        }
        System.out.println(Pid18);


    }


    // 21) Состояние беспроводной сети
    @FXML
    private void onNetworkTaskMenuItemClick(ActionEvent actionEvent) throws IOException {
        Tasks.Task_21();
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path()+"/System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(path()+"System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(Pid21);
        if(Pid21 !=0) {
            try {
                // Запускаем команду в командной строке
                Process process = Runtime.getRuntime().exec("kill " + Pid21);
                process.waitFor(); // Ожидаем завершения команды
                System.out.println("Процесс с PID " + Pid21 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }


        try {
            Process process = processBuilder.start();
            Pid21 =process.pid();
            //InputStream inputStream = process.getInputStream();
            // InputStream errorStream = process.getErrorStream();

        } catch (IOException e) {
            // More specific error message
            throw new RuntimeException("Error starting the process for Task.jar", e);
        }
        System.out.println(Pid21);

    }


    //44) Факт создания файлов
    @FXML
    public void onFactsFilesMenuItemClick(ActionEvent actionEvent) throws IOException {


        Tasks.Task_44();
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path()+"/System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(path()+"System/Task/out/artifacts/Task_jar/Task.jar");
        System.out.println(Pid44);
        if(Pid44 !=0) {
            try {
                // Запускаем команду в командной строке
                Process process = Runtime.getRuntime().exec("kill " + Pid44);
                process.waitFor(); // Ожидаем завершения команды
                System.out.println("Процесс с PID " + Pid44 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }


        try {
            Process process = processBuilder.start();
            Pid44 =process.pid();
            //InputStream inputStream = process.getInputStream();
            // InputStream errorStream = process.getErrorStream();

        } catch (IOException e) {
            // More specific error message
            throw new RuntimeException("Error starting the process for Task.jar", e);
        }
        System.out.println(Pid44);




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
        System.out.println(directoryPath);
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
                System.out.println(mediaDirectoryPath+usbName);
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
                        System.out.println(directoryPath + "/" + val + nowValue);
                        File selectedDirectory = new File(directoryPath + "/" + val + nowValue);
                        nowPath = directoryPath + "/" + val + nowValue;
                        Path.setText("/SuperApp/"+val + nowValue);
                        populateTableView(selectedDirectory);
                    break;

                    case "USB":
                        System.out.println( mediaDirectoryPath+ "/"+usbName+"/" + val + nowValue);
                        File mediaDirectory = new File(mediaDirectoryPath+usbName+"/" + val + nowValue);
                        nowPath=mediaDirectoryPath+usbName+"/" + val + nowValue;
                        Path.setText("/USB/"+usbName+"/" + val + nowValue);
                        populateTableView(mediaDirectory);
                        break;
                    }
                }
            catch (Exception e)
            {
                System.out.println(newValue.getValue());
                File selectedDirectory = new File(directoryPath);
                nowPath=directoryPath;
                Path.setText("/SuperApp");
                populateTableView(selectedDirectory);
            }
            }
        }}
        );

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
                String name = file.getName();
                String size = (file.length() / 1024) + " KB"; // Размер файла в KB
                Date change =new Date(file.lastModified());
                fileList.add(new FileInfo(name, size, change));
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

        System.out.println("Полный путь до папки: " + basePath);
        return basePath;
    }


    //Создание наблюдателя за состояниями флешки
    public void monitorUSB() throws IOException, InterruptedException {
        Path path = Paths.get("/media/ylly");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE);

        System.out.println("Мониторинг изменений в: " + path.toString());

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
            System.out.println("Устройство " + action + ": " + fileName);

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


    }



