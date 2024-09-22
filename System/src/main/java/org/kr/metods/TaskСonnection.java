package org.kr.metods;

import org.kr.controllers.KRController;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskСonnection {
    public static void connection18() throws InterruptedException, IOException {
        // Вызов вашей задачи Task_18
        long pid = KRController.getPid18() ;// Укажите PID процесса, который нужно отслеживать
      //  System.out.println("long pid taskconnection "+pid);
        int pidToWatch=(int) pid;
      //  System.out.println("pidToWatch "+pidToWatch);

        // Запускаем задачу в отдельном потоке
        Thread taskThread = new Thread(() -> {
            try {
                // Имитация выполнения задачи
         //       System.out.println("Задача началась...");

                ///писулька раз в 3 секунды
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                    scheduler.scheduleAtFixedRate(() -> {
                        try {
                            Tasks.Task_18();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                while (true) {
                    if (!isProcessRunning(pidToWatch)) {
                //        System.out.println("Процесс завершен. Остановка задачи.");
                        scheduler.close();
                        break; // Завершаем задачу, если процесс с pid завершен
                    }
                    // Здесь можно выполнять другие действия
                    Thread.sleep(1000); // Задержка для снижения нагрузки на CPU
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              //  System.out.println("Задача была прервана.");
            }
        });

        taskThread.start();
    }

    public static void connection21() throws InterruptedException, IOException {
        // Вызов вашей задачи Task_18
        long pid = KRController.getPid21() ;// Укажите PID процесса, который нужно отслеживать
    //    System.out.println("long pid taskconnection "+pid);
        int pidToWatch=(int) pid;
    //    System.out.println("pidToWatch "+pidToWatch);

        // Запускаем задачу в отдельном потоке
        Thread taskThread = new Thread(() -> {
            try {
                // Имитация выполнения задачи
             //   System.out.println("Задача началась...");

                ///писулька раз в 3 секунды
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        Tasks.Task_21();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 0, 1, TimeUnit.SECONDS);
                while (true) {
                    if (!isProcessRunning(pidToWatch)) {
                     //   System.out.println("Процесс завершен. Остановка задачи.");
                        scheduler.close();
                        break; // Завершаем задачу, если процесс с pid завершен
                    }
                    // Здесь можно выполнять другие действия
                    Thread.sleep(1000); // Задержка для снижения нагрузки на CPU
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //   System.out.println("Задача была прервана.");
            }
        });

        taskThread.start();
    }

    public static void connection44() throws InterruptedException, IOException {
        // Вызов вашей задачи Task_18
        long pid = KRController.getPid44() ;// Укажите PID процесса, который нужно отслеживать
      //  System.out.println("long pid taskconnection "+pid);
        int pidToWatch=(int) pid;
      //  System.out.println("pidToWatch "+pidToWatch);

        // Запускаем задачу в отдельном потоке
        Thread taskThread = new Thread(() -> {
            try {
                // Имитация выполнения задачи
            //    System.out.println("Задача началась...");

                ///писулька раз в 3 секунды
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                scheduler.scheduleAtFixedRate(() -> {
                    try {
                        Tasks.Task_44();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 0, 1, TimeUnit.SECONDS);
                while (true) {
                    if (!isProcessRunning(pidToWatch)) {
                 //       System.out.println("Процесс завершен. Остановка задачи.");
                        scheduler.close();
                        break; // Завершаем задачу, если процесс с pid завершен
                    }
                    // Здесь можно выполнять другие действия
                    Thread.sleep(1000); // Задержка для снижения нагрузки на CPU
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              //  System.out.println("Задача была прервана.");
            }
        });

        taskThread.start();
    }


    private static boolean isProcessRunning(int pid) {
        try {
            Process process = Runtime.getRuntime().exec("ps -p " + pid);
            return process.waitFor() == 0; // Если процесс существует, возвращает true
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    }