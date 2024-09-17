package org.kr.metods;

import java.io.*;

public  class Utilites {


    public static void TerminalLauncher (){
        try {
            // Запуск терминала
            Runtime.getRuntime().exec("gnome-terminal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        public static void FileManagerLauncher() {
            try {
                // Запуск файлового менеджера Nautilus
                Runtime.getRuntime().exec("nautilus");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void SystemMonitorLauncher(){
            try {
                Process process = Runtime.getRuntime().exec("gnome-system-monitor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static void LogsLauncher() {
        try {
            Process process = Runtime.getRuntime().exec("gnome-logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}


