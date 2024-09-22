package org.kr;

import org.kr.controllers.KRController;
import org.kr.metods.Tasks;

import java.io.File;
import java.io.IOException;

public class Maindemo {
    public static void main(String [] args){
        long Pid18 = 0; // Инициализация переменной для PID процесса

        try {
            // Указываем директорию, в которой нужно выполнить команду
            File dir = new File("/home/ylly/SuperApp/System/Task");

            // Вызов вашей задачи Task_18
            Tasks.Task_18();

            // Создаем ProcessBuilder для выполнения команды
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-jar", KRController.path() + "/System/Task/out/artifacts/Task_jar/Task.jar"
            );

            // Устанавливаем рабочую директорию
            processBuilder.directory(dir);

            // Печатаем путь к JAR файлу для отладки
            System.out.println(KRController.path() + "/System/Task/out/artifacts/Task_jar/Task.jar");

            // Выводим текущий PID, если не равен 0 (предыдущий процесс)
            System.out.println("Предыдущий PID: " + Pid18);

            // Если PID процесса не равен 0, завершаем старый процесс
            if (Pid18 != 0) {
                try {
                    // Запуск команды для завершения процесса с PID18
                    Process killProcess = Runtime.getRuntime().exec("kill " + Pid18);
                    killProcess.waitFor(); // Ожидаем завершения команды

                    // Сообщение об успешном завершении процесса
                    System.out.println("Процесс с PID " + Pid18 + " успешно завершён.");
                } catch (IOException | InterruptedException e) {
                    // Сообщение об ошибке при завершении процесса
                    System.out.println("Произошла ошибка при завершении процесса: " + e.getMessage());
                }
            }

            // Запуск нового процесса
            try {
                Process process = processBuilder.start();
                Pid18 = process.pid(); // Получаем PID нового процесса

                // Печатаем новый PID процесса
                System.out.println("Запущен новый процесс с PID: " + Pid18);

            } catch (IOException e) {
                // Сообщение об ошибке запуска нового процесса
                throw new RuntimeException("Ошибка при запуске Task.jar", e);
            }

            System.out.println("Команда выполнена успешно.");
        } catch (IOException e) {
            // Обработка ошибки ввода-вывода
            throw new RuntimeException(e);
        }
    }
}
