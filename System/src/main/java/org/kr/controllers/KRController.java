package org.kr.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.kr.metods.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static final Logger logger =LoggerUtil.getLogger(KRController.class);
    @FXML
    private void onLocalTerminalMenuItemClick(ActionEvent actionEvent){
        Utilites.TerminalLauncher();
        logger.info("Встроенный терминал запущен");
    }

    @FXML
    private void onFileManagerMenuItemClick(ActionEvent actionEvent) {
        Utilites.FileManagerLauncher();
        logger.info("Файловый менеджер запущен");
    }

    @FXML
    private void onSysMonMenuItemClick(ActionEvent actionEvent) {
        Utilites.SystemMonitorLauncher();
        logger.info("Системный монитор запущен");
    }

    @FXML
    private void onLogMenuItemClick(ActionEvent actionEvent) {
        Utilites.LogsLauncher();
        logger.info("Система логирования запущена");
    }


    //обработчик кнопки поиска
    @FXML
    private void onSearchButtonClick(ActionEvent actionEvent) {
        logger.info("Нажатие кнопки поиск");
        String searchFileName = Name.getText(); // Получаем имя файла или папки из текстового поля
        if (searchFileName == null || searchFileName.trim().isEmpty()) {
            showAlert("Введите название для поиска.");
            logger.warning("Поле для поиска пустое");
            return;
        }

        File rootDirectory = new File(directoryPath); // Папка SuperApp
        ObservableList<FileInfo> foundItems = searchFilesAndFolders(rootDirectory, searchFileName);

        if (foundItems.isEmpty()) {
            showAlert("Файлы или папки с таким именем не найдены.");
            logger.warning("Файлы или папки с именем "+searchFileName+" не найдены.");
        } else {
            TableView.setItems(foundItems); // Отображаем найденные файлы и папки в TableView
            logger.info("Поиск файла "+searchFileName+" прошёл успешно");
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

    //Переход к файлу по пути
    @FXML
    private void onPathEntered(ActionEvent actionEvent) {
        logger.info("Ввод пути");
        // Получаем введённый пользователем путь
        String inputPath = Path.getText();

        // Преобразуем введённый путь в абсолютный, добавив корневую директорию SuperApp
        String absolutePath;
        if (inputPath.startsWith("/SuperApp")) {
            absolutePath = directoryPath + inputPath.replace("/SuperApp", "");
        } else if (inputPath.startsWith("/USB") && !usbName.isEmpty()) {
            absolutePath = mediaDirectoryPath + usbName + inputPath.replace("/USB/" + usbName, "");

        } else {
            // Если путь не начинается с SuperApp или USB, показываем ошибку
            showError("Неверный путь. Путь должен начинаться с '/SuperApp' или 'USB/'.");
            logger.warning("Неверный путь: "+inputPath+" Путь должен начинаться с '/SuperApp' или 'USB/'.");
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
            logger.info("Перход к "+ inputPath+" прошёл успешно");
        } else {
            showError("Путь не найден: " + inputPath);
            logger.warning("Путь не найден: " + inputPath);
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
                logger.info("Открытие О приложении");
            } catch (IOException e) {
                logger.warning("Ошибка при открытии О приложении");
            }
        }
    }


    //Терминал -> Запуск
    public void onStartTerminalMenuItemClick(ActionEvent actionEvent) {
        try {
            TerminalController.openTerminalWindow();
            logger.info("Запуск локального терминала");
        } catch (IOException e) {
            logger.warning("Ошибка при запуске локального терминала");
        }
    }

    //18) количество запущенных системных процессов
    @FXML
    public void onProcessTaskMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {
        logger.info("Запуск задания 18");

        // Указываем директорию, в которой нужно выполнить команду
        File dir = new File(path()+"/System/Task18");




        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task18/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);


        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid18 != 0) {
            try {

                Process killProcess = Runtime.getRuntime().exec("kill " + Pid18);
                killProcess.waitFor(); // Ожидаем завершения команды
                logger.info("Процесс с PID " + Pid18 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                logger.warning("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid18 = process.pid();
            logger.info("Запущен новый процесс с PID: " + Pid18);

        } catch (IOException e) {
            logger.warning("Ошибка при запуске Task.jar "+e);
        }
        TaskСonnection.connection18();

        logger.info("Команда 18 выполнена успешно.");
    }


    // 21) Состояние беспроводной сети
    @FXML
    private void onNetworkTaskMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {

        logger.info("Запуск задания 21: Состояние беспроводной сети");

        // Указываем директорию, в которой нужно выполнить команду
        File dir = new File(path() + "/System/Task21");

        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task21/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);

        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid21 != 0) {
            try {
                Process killProcess = Runtime.getRuntime().exec("kill " + Pid21);
                killProcess.waitFor(); // Ожидаем завершения команды
                logger.info("Процесс с PID " + Pid21 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                logger.warning("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid21 = process.pid(); // Получаем PID нового процесса
            logger.info("Запущен новый процесс с PID: " + Pid21);

        } catch (IOException e) {
            logger.warning("Ошибка при запуске Task.jar: " + e.getMessage());
        }

        // Выполнение подключения Task21
        TaskСonnection.connection21();

        logger.info("Команда 21 выполнена успешно.");
    }



    //44) Факт создания файлов
    @FXML
    public void onFactsFilesMenuItemClick(ActionEvent actionEvent) throws IOException, InterruptedException {

        logger.info("Запуск задания 44: Факт создания файлов");

        // Указываем директорию, в которой нужно выполнить команду
        File dir = new File(path() + "/System/Task44");

        // Создаем ProcessBuilder для выполнения команды
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path() + "/System/Task44/out/artifacts/Task_jar/Task.jar");

        // Устанавливаем рабочую директорию
        processBuilder.directory(dir);

        // Если PID процесса не равен 0, завершаем старый процесс
        if (Pid44 != 0) {
            try {
                Process killProcess = Runtime.getRuntime().exec("kill " + Pid44);
                killProcess.waitFor(); // Ожидаем завершения команды
                logger.info("Процесс с PID " + Pid44 + " успешно завершён.");
            } catch (IOException | InterruptedException e) {
                logger.warning("Произошла ошибка при завершении процесса: " + e.getMessage());
            }
        }

        // Запуск нового процесса
        try {
            Process process = processBuilder.start();
            Pid44 = process.pid(); // Получаем PID нового процесса
            logger.info("Запущен новый процесс с PID: " + Pid44);

        } catch (IOException e) {
            logger.warning("Ошибка при запуске Task.jar: " + e.getMessage());
        }

        // Выполнение подключения Task44
        TaskСonnection.connection44();

        logger.info("Команда 44 выполнена успешно.");
    }


    public void onLeftMouseButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onNameEnter(ActionEvent actionEvent) {
        String searchFileName = Name.getText(); // Получаем имя файла или папки из текстового поля

        // Логируем ввод пользователя
        logger.info("Пользователь ввел имя для поиска: " + searchFileName);

        if (searchFileName == null || searchFileName.trim().isEmpty()) {
            // Логируем предупреждение, что имя файла не было введено
            logger.warning("Имя файла или папки не введено.");
            showAlert("Введите название для поиска.");
            return;
        }

        File rootDirectory = new File(directoryPath); // Папка SuperApp

        // Логируем начало поиска файлов и папок
        logger.info("Начат поиск в директории: " + rootDirectory.getAbsolutePath());

        ObservableList<FileInfo> foundItems = searchFilesAndFolders(rootDirectory, searchFileName);

        if (foundItems.isEmpty()) {
            // Логируем, что поиск не дал результатов
            logger.info("Файлы или папки с именем '" + searchFileName + "' не найдены.");
            showAlert("Файлы или папки с таким именем не найдены.");
        } else {
            // Логируем количество найденных элементов
            logger.info("Найдено файлов или папок: " + foundItems.size());
            TableView.setItems(foundItems); // Отображаем найденные файлы и папки в TableView
        }
    }



    private Stack<String> backStack = new Stack<>(); // Стек для кнопки "назад"
    private Stack<String> forwardStack = new Stack<>(); // Стек для кнопки "вперед"



    //
    public void onBackButtonClick(ActionEvent actionEvent) {
        // Логируем текущее состояние пути перед его изменением
        logger.info("Текущий путь перед изменением: " + nowPath);

        // Сохраняем текущий путь в backStack перед его изменением
        backStack.push(nowPath);
        logger.info("Путь сохранен в backStack: " + nowPath);

        String localPath = "";
        int superAppIndex = nowPath.indexOf("/SuperApp");
        int usbIndex = nowPath.indexOf(usbName);

        // Логика определения, какой путь нужно обрезать
        if (superAppIndex != -1) {
            localPath = nowPath.substring(superAppIndex);
            logger.info("LocalPath (SuperApp): " + localPath);
        } else if (usbIndex != -1) {
            localPath = nowPath.substring(usbIndex);
            localPath = "/USB/" + localPath;
            logger.info("LocalPath (USB): " + localPath);
        } else {
            logger.warning("Не найдено ни /SuperApp, ни USB в пути: " + nowPath);
            return;
        }

        // Логика обрезки пути до последнего слэша
        if ((localPath.startsWith("/USB") && !localPath.equals("/USB/" + usbName)) ||
                (localPath.startsWith("/SuperApp") && !localPath.equals("/SuperApp"))) {

            int lastSlashIndex = nowPath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                nowPath = nowPath.substring(0, lastSlashIndex); // Обрезаем путь
                localPath = localPath.substring(0, localPath.lastIndexOf("/"));
                logger.info("nowPath после обрезки: " + nowPath);
            }
            Path.setText(localPath); // Обновляем текстовое поле
            File selectedFile = new File(nowPath);

            logger.info("Обновление таблицы с файлом: " + selectedFile.getAbsolutePath());
            populateTableView(selectedFile); // Обновляем таблицу
        } else {
            Path.setText(localPath);
            logger.info("Путь не может быть обрезан дальше: " + localPath);
        }

        // Логируем измененный путь
        logger.info("Путь после изменения: " + Path.getText());
    }


    public void onForwardButtonClick(ActionEvent actionEvent) {
        if (!backStack.isEmpty()) {
            // Извлекаем последний путь из backStack
            String previousPath = backStack.pop();
            logger.info("Переход на предыдущий путь из backStack: " + previousPath);
            nowPath = previousPath; // Устанавливаем его как текущий путь

            // Логика для обновления localPath
            String localPath = "";
            int superAppIndex = nowPath.indexOf("/SuperApp");
            int usbIndex = nowPath.indexOf(usbName);

            if (superAppIndex != -1) {
                localPath = nowPath.substring(superAppIndex);
                logger.info("LocalPath (SuperApp): " + localPath);
            } else if (usbIndex != -1) {
                localPath = nowPath.substring(usbIndex);
                localPath = "/USB/" + localPath;
                logger.info("LocalPath (USB): " + localPath);
            } else {
                logger.warning("Не найдено ни /SuperApp, ни /USB в пути: " + nowPath);
                return;
            }

            // Обновляем интерфейс
            Path.setText(localPath);
            File selectedFile = new File(nowPath);

            logger.info("Обновление таблицы с файлом: " + selectedFile.getAbsolutePath());
            populateTableView(selectedFile); // Обновляем таблицу

            logger.info("Путь (после изменения на вперёд): " + Path.getText());
        } else {
            logger.info("Нет доступных путей для перехода вперёд.");
        }
    }


    public void onpopUpMenuOpenMenuItemClick(ActionEvent actionEvent) {
        logger.info(" Открытие файла/папки по пути: " + AlgoritmPath());
        AlgoritmOpen();
        logger.info("Запуск всплывающее меню открыть");
    }

    public void onpopUpMenuCopyMenuItemClick(ActionEvent actionEvent) throws IOException {
        logger.info("Копирование файла/папки по пути: " + AlgoritmPath());
        Working_with_files.copyTableView(AlgoritmPath());
        logger.info("Файл для копирования: " + Working_with_files.sourceFile);
    }

    public void onpopUpMenuPasteMenuItemClick(ActionEvent actionEvent) throws IOException {
        logger.info("Вставка файла/папки в директорию: " + AlgoritmPathDirectory());
        Working_with_files.pasteTableView(AlgoritmPathDirectory());
        logger.info("Исходный файл: " + Working_with_files.sourceFile);
        logger.info("Файл назначения: " + Working_with_files.destinationFile);
    }

    public void onpopUpMenuDeleteMenuItemClick(ActionEvent actionEvent) throws IOException {
        logger.info("Удаление файла/папки по пути: " + AlgoritmPath());
        Working_with_files.deleteTableView(AlgoritmPath());
        logger.info("Файл/папка успешно удалены.");
    }

    public void onpopUpMenuDeleteTrashMenuItemClick(ActionEvent actionEvent) throws IOException {
        logger.info("Перемещение файла/папки в корзину по пути: " + AlgoritmPath());
        Working_with_files.moveToTrash(AlgoritmPath());
        logger.info("Файл/папка успешно перемещены в корзину.");
    }

    public void onpopUpMenuCreateFolderMenuItemClick(ActionEvent actionEvent) throws IOException {
        logger.info("Создание новой папки в директории: " + AlgoritmPathDirectory());
        Working_with_files.createDirectory(AlgoritmPathDirectory());
        logger.info("Папка успешно создана.");
    }

    public void onpopUpMenuCreateFileMenuItemClick(ActionEvent actionEvent) {
        logger.info("Создание нового файла в директории: " + AlgoritmPathDirectory());
        Working_with_files.createFile(AlgoritmPathDirectory());
        logger.info("Файл успешно создан.");
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
        logger.info("Инициализация приложения начата.");

        // Инициализация TreeView
        logger.info("Инициализация TreeView с корневым каталогом: " + directoryPath);
        File rootDirectory = new File(directoryPath);
        TreeItem<String> rootItem = new TreeItem<>(rootDirectory.getName());
        rootItem.setExpanded(true);
        TreeView.setRoot(rootItem);
        populateTreeView(rootDirectory, rootItem);
        logger.info("TreeView успешно инициализирован.");

        // Инициализация TableView
        logger.info("Инициализация TableView колонок: имя, размер, дата изменения.");
        TableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        TableChange.setCellValueFactory(new PropertyValueFactory<>("change"));

        // Отображение файлов в TableView при выборе в TreeView
        TreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.info("Выбран элемент в TreeView: " + newValue.getValue());
                String parnt = "";
                String val = "";
                String USBornotUSB = "";
                String nowValue = newValue.getValue();

                if (nowValue.equals(usbName)) {
                    File mediaDirectory = new File(mediaDirectoryPath + usbName);
                    nowPath = mediaDirectoryPath + usbName;
                    Path.setText("/USB/" + usbName);
                    logger.info("Открытие USB: " + usbName);
                    populateTableView(mediaDirectory);
                } else {
                    try {
                        while (!parnt.equals("SuperApp")) {
                            parnt = newValue.getParent().getValue();
                            if (parnt.equals(usbName)) {
                                USBornotUSB = "USB";
                                break;
                            }
                            if (parnt.equals("SuperApp")) {
                                USBornotUSB = "notUSB";
                                break;
                            }
                            val = parnt + "/" + val;
                            newValue = newValue.getParent();
                        }
                        switch (USBornotUSB) {
                            case "notUSB":
                                File selectedDirectory = new File(directoryPath + "/" + val + nowValue);
                                nowPath = directoryPath + "/" + val + nowValue;
                                Path.setText("/SuperApp/" + val + nowValue);
                                logger.info("Открытие директории: " + nowPath);
                                populateTableView(selectedDirectory);
                                break;

                            case "USB":
                                File mediaDirectory = new File(mediaDirectoryPath + usbName + "/" + val + nowValue);
                                nowPath = mediaDirectoryPath + usbName + "/" + val + nowValue;
                                Path.setText("/USB/" + usbName + "/" + val + nowValue);
                                logger.info("Открытие USB директории: " + nowPath);
                                populateTableView(mediaDirectory);
                                break;
                        }
                    } catch (Exception e) {
                        logger.warning("Ошибка при выборе элемента в TreeView: " + e.getMessage());
                        File selectedDirectory = new File(directoryPath);
                        nowPath = directoryPath;
                        Path.setText("/SuperApp");
                        populateTableView(selectedDirectory);
                    }
                }
            }
        });

        TableView.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.T) {
                logger.info("Открытие терминала.");
                try {
                    TerminalController.openTerminalWindow();
                } catch (IOException e) {
                    logger.warning("Ошибка открытия терминала: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                logger.info("Копирование выбранного файла/папки.");
                try {
                    Working_with_files.copyTableView(AlgoritmPath());
                } catch (IOException e) {
                    logger.warning("Ошибка копирования: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                logger.info("Вставка файла/папки.");
                Working_with_files.pasteTableView(AlgoritmPathDirectory());
            }

            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                logger.info("Создание новой папки.");
                Working_with_files.createDirectory(AlgoritmPathDirectory());
            }

            if (event.isControlDown() && event.getCode() == KeyCode.F) {
                logger.info("Создание нового файла.");
                Working_with_files.createFile(AlgoritmPathDirectory());
            }
        });

        TreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Двойной клик
                TreeItem<String> selectedItem = TreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    logger.info("Двойной клик по элементу TreeView: " + selectedItem.getValue());
                    Working_with_files.openTreeView();
                }
            }
        });

        TableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Двойной клик
                logger.info("Двойной клик по элементу TableView.");
                AlgoritmOpen();
            }
        });

        logger.info("Проверка USB-устройств для добавления в TreeView.");
        File mediaDirectory = new File(mediaDirectoryPath);
        File[] usbDrives = mediaDirectory.listFiles();
        if (usbDrives != null) {
            for (File usbDrive : usbDrives) {
                if (usbDrive.isDirectory()) {
                    logger.info("Найдено USB-устройство: " + usbDrive.getName());
                    TreeItem<String> usbRootItem = new TreeItem<>(usbDrive.getName());
                    usbName = usbDrive.getName();
                    TreeView.getRoot().getChildren().add(usbRootItem);
                    populateTreeView(usbDrive, usbRootItem);
                }
            }
        }

        Thread thread = new Thread(() -> {
            try {
                monitorUSB();
            } catch (IOException | InterruptedException e) {
                logger.warning("Ошибка при мониторинге USB: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
        logger.info("Инициализация завершена.");
    }


    // Используется в TreeView(отображение)
    private void populateTreeView(File directory, TreeItem<String> rootItem) {
        rootItem.getChildren().clear();  // Очищаем старые элементы
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> item = new TreeItem<>(file.getName());
                rootItem.getChildren().add(item);
                // Логирование имени добавленного файла или папки
                System.out.println("Добавление в TreeView: " + file.getName());
                if (file.isDirectory()) {
                    populateTreeView(file, item);
                }
            }
        } else {
            // Логирование ошибки чтения директории
            System.out.println("Ошибка: Не удалось прочитать содержимое директории: " + directory.getAbsolutePath());
        }
    }

    // Нужное но ?????
    private void populateTableView(File directory) {
        File[] files = directory.listFiles();
        ObservableList<FileInfo> fileList = FXCollections.observableArrayList();
        if (files != null) {
            for (File file : files) {
                String size = (file.length() / 1024) + " KB"; // Размер файла в KB
                Date change = new Date(file.lastModified());
                fileList.add(new FileInfo(file.getName(), size, change));
                // Логирование добавленных файлов в таблицу
                System.out.println("Добавление в TableView: " + file.getName() + " (размер: " + size + ", изменен: " + change + ")");
            }
        } else {
            // Логирование ошибки чтения директории
            System.out.println("Ошибка: Не удалось прочитать содержимое директории для таблицы: " + directory.getAbsolutePath());
        }
        TableView.setItems(fileList);
    }

    // Получение полного пути до текущей директории (включая текущую)
    public static String path () {
        String relativePath = ""; // Замените "yourFolder" на вашу папку
        File folder = new File(relativePath);
        String basePath = folder.getAbsolutePath();
        // Логирование полного пути
        System.out.println("Полный путь до папки: " + basePath);
        return basePath;
    }

    // Создание наблюдателя за состояниями флешки
    public void monitorUSB() throws IOException, InterruptedException {
        Path path = Paths.get("/media/ylly");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE);

        // Логирование начала мониторинга
        logger.log(Level.INFO, "Мониторинг изменений в: {0}", path.toString());

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path fileName = (Path) event.context();

                if (kind == ENTRY_CREATE || kind == ENTRY_DELETE) {
                    // Логирование событий изменения флешки
                    logger.log(Level.INFO, "Событие на флешке: {0} устройство: {1}",
                            new Object[]{(kind == ENTRY_CREATE ? "Подключено" : "Отключено"), fileName});
                    handleUSBChange(kind, fileName);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                // Логирование прекращения мониторинга
                logger.log(Level.WARNING, "Мониторинг флешек прекращен.");
                break;
            }
        }
    }

    // Динамическое обновление флешки
    private void handleUSBChange(WatchEvent.Kind<?> kind, Path fileName) {
        Platform.runLater(() -> {
            String action = (kind == ENTRY_CREATE) ? "подключено" : "отключено";
            // Логирование подключения/отключения устройства
            logger.log(Level.INFO, "Устройство {0}: {1}", new Object[]{action, fileName});

            // Обновление дерева
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
                        usbName = usbDrive.getName();
                        TreeView.getRoot().getChildren().add(usbRootItem);
                        populateTreeView(usbDrive, usbRootItem);
                        // Логирование обновления дерева для USB
                        logger.log(Level.INFO, "Обновление дерева для USB: {0}", usbDrive.getName());
                    }
                }
            }
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

    public void AlgoritmOpen() {
        String localPath = ""; // Двойной клик

        // Получаем выбранный элемент
        FileInfo selectedItem = TableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Логируем выбранный элемент
            System.out.println("Выбранный элемент: " + selectedItem.name);

            if (selectedItem.name.startsWith("/SuperApp")) {
                String output = selectedItem.name.substring("/SuperApp".length());
                File selectedFile = new File(directoryPath + output);

                // Логируем путь к выбранному файлу/папке
                System.out.println("Путь к файлу/папке (SuperApp): " + selectedFile.getPath());

                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    // Находим индекс "SuperApp" в пути и обрезаем его
                    int index = localPath.indexOf("/SuperApp");
                    if (index != -1) {
                        localPath = nowPath.substring(index);
                        System.out.println("Обрезанный путь: " + localPath);
                    }

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println("Открытие файла: " + selectedFile.getPath());
                    Working_with_files.openTableView(selectedFile.getPath());
                }

            } else if (selectedItem.name.startsWith("/USB")) {
                System.out.println("Строка начинается с /USB");
                String output = selectedItem.name.substring("/USB/".length());
                File selectedFile = new File(mediaDirectoryPath + output);

                // Логируем путь к выбранному файлу/папке (USB)
                System.out.println("Путь к файлу/папке (USB): " + selectedFile.getPath());

                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    int index = localPath.indexOf("/" + usbName);
                    if (index != -1) {
                        localPath = nowPath.substring(index);
                        System.out.println("Обрезанный путь: " + localPath);
                    }
                    localPath = "/USB" + localPath;

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println("Открытие файла: " + selectedFile.getPath());
                    Working_with_files.openTableView(selectedFile.getPath());
                }

            } else {
                // Формируем путь к выбранному файлу/папке
                File selectedFile = new File(nowPath + "/" + selectedItem.getName());

                // Логируем путь к выбранному элементу
                System.out.println("Путь к выбранному файлу/папке: " + selectedFile.getPath());

                if (selectedFile.isDirectory()) {
                    nowPath = selectedFile.getPath();
                    localPath = nowPath;

                    // Находим индекс "SuperApp" или "USB" в пути и обрезаем его
                    int indexSuperApp = localPath.indexOf("/SuperApp");
                    int indexUSB = localPath.indexOf(usbName);
                    if (indexSuperApp != -1) {
                        localPath = nowPath.substring(indexSuperApp);
                        System.out.println("Обрезанный путь (SuperApp): " + localPath);
                    }
                    if (indexUSB != -1) {
                        localPath = nowPath.substring(indexUSB);
                        localPath = "/USB/" + localPath;
                        System.out.println("Обрезанный путь (USB): " + localPath);
                    }

                    // Обновляем текстовое поле Path и таблицу
                    Path.setText(localPath);
                    populateTableView(selectedFile);
                } else {
                    System.out.println("Открытие файла: " + selectedFile.getPath());
                    Working_with_files.openTableView(selectedFile.getPath());
                }
            }
        } else {
            // Логирование случая, если ничего не выбрано
            System.out.println("Ничего не выбрано.");
        }
    }

    public String AlgoritmPath() {
        // Получаем выбранный элемент
        FileInfo selectedItem = TableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Логируем выбранный элемент
            System.out.println("Выбранный элемент для пути: " + selectedItem.name);

            if (selectedItem.name.startsWith("/SuperApp")) {
                if (selectedItem.name.startsWith("/SuperApp/System")) {
                    System.out.println("Выбран элемент из System, возвращаем пустую строку.");
                    return "";
                }
                String output = selectedItem.name.substring("/SuperApp".length());
                System.out.println("Путь для SuperApp: " + directoryPath + output);
                return directoryPath + output;

            } else if (selectedItem.name.startsWith("/USB")) {
                System.out.println("Строка начинается с /USB");
                String output = selectedItem.name.substring("/USB/".length());
                System.out.println("Путь для USB: " + mediaDirectoryPath + output);
                return mediaDirectoryPath + output;

            } else {
                if (nowPath.contains("/SuperApp/System") || selectedItem.name.equals("System")) {
                    System.out.println("Выбран элемент из System, возвращаем пустую строку.");
                    return "";
                }
                System.out.println("Путь для обычного элемента: " + nowPath + "/" + selectedItem.getName());
                return nowPath + "/" + selectedItem.getName();
            }
        }
        // Логирование случая, если ничего не выбрано
        System.out.println("Ничего не выбрано, возвращаем пустую строку.");
        return "";
    }

    public String AlgoritmPathDirectory() {
        // Логируем текущий путь
        System.out.println("Текущий путь: " + Path.getText());

        if (Path.getText().startsWith("/SuperApp")) {
            if (Path.getText().startsWith("/SuperApp/System")) {
                System.out.println("Путь содержит System, возвращаем пустую строку.");
                return "";
            }
            String output = Path.getText().substring("/SuperApp".length());
            System.out.println("Путь для директории SuperApp: " + directoryPath + output);
            return directoryPath + output;

        } else if (Path.getText().startsWith("/USB")) {
            System.out.println("Строка начинается с /USB");
            String output = Path.getText().substring("/USB/".length());
            System.out.println("Путь для директории USB: " + mediaDirectoryPath + output);
            return mediaDirectoryPath + output;
        }

        // Логирование случая, если путь не найден
        System.out.println("Не удалось определить путь, возвращаем пустую строку.");
        return "";
    }




}



