package org.kr.metods;

import org.kr.KRapplication;
import org.kr.controllers.KRController;

import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Tasks {
    public static void Task_18() throws IOException {
        String contentToWrite = "";

        try {
            Process process = Runtime.getRuntime().exec("ps -e");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int processCount = 0;
            StringBuilder processList = new StringBuilder();
            String line;

            // Читаем строки и собираем список процессов
            while ((line = reader.readLine()) != null) {
                processCount++;
                processList.append(line).append("\n");
            }

            // Уменьшаем на 1, так как первая строка - это заголовок
            processCount--;

            // Устанавливаем текст в TextArea через контроллер
            contentToWrite = "Количество системных процессов: " + processCount + "\n\nСписок процессов:\n" + processList.toString();


        } catch (Exception e) {
            e.printStackTrace();

        }
        write(contentToWrite);


    }

    public static void Task_21() throws IOException {
        String contentToWrite = "";




        try {
            // Выполняем команду nmcli для получения статуса сети
            Process process = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "nmcli dev status | grep wifi"});

            // Считываем результат выполнения команды
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder WiFiList = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                WiFiList.append(line).append("\n");
            }
            contentToWrite ="Информация о статусе беспроводной сети:\n"+WiFiList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        write(contentToWrite);


    }

    public static void Task_44() throws IOException {
        String startTime=(KRapplication.formattedDateTime);
        System.out.println(startTime);
        String contentToWrite = "";
        String time = "2024-09-13 20:20:30";


        List<String> stringList = new ArrayList<>();
        String[] command = { "find", "/home/ylly/SuperApp", "-type", "f", "-newermt"};
        for (int i=0; i< command.length; i++){
            stringList.add(command[i]);
        }
        stringList.add(startTime);
        System.out.println(stringList);

        try {
            // Создаем процесс с заданной командой
            ProcessBuilder processBuilder = new ProcessBuilder(stringList);
            Process process = processBuilder.start();
            StringBuilder FactList = new StringBuilder();
            String line;

            // Читаем вывод процесса
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    FactList.append(line).append("\n");
                }
            }
            if (FactList.length()>0) {
                System.out.println("!: "+FactList);
                contentToWrite = "Создан файл: " + FactList.toString();
            }
            else {
                contentToWrite="Не был создан ни один файл с момента последнего запуска программы";
            }

            // Ожидаем завершения процесса
            int exitCode = process.waitFor();
            System.out.println("Процесс завершен с кодом: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
       write(contentToWrite);


    }
    public static void write(String contentToWrite) throws IOException {
        String filePath =  KRController.path() +"/System/Task/ipc";// Укажите путь к вашему файлу

        try {
            // Открываем файл в режиме записи (если файла не существует, он будет создан)
            FileWriter fileWriter = new FileWriter(filePath, false); // false означает перезапись файла
            fileWriter.write(""); // Записываем пустую строку, чтобы очистить файл
            fileWriter.close();
            System.out.println("Файл успешно очищен.");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при очистке файла: " + e.getMessage());
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        try  {
            // Запись содержимого в файл
            writer.write(contentToWrite);
            System.out.println("Файл успешно записан!");

        } catch (IOException e) {
            System.err.println("Произошла ошибка при записи в файл: " + e.getMessage());

        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

}
