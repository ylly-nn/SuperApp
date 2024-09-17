package org.kr.metods;

import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    public Terminal() {
        // Конструктор, если необходимо инициализировать какие-то значения
    }

    public static void executeCommand(String command, ListView<String> outputList) {
        String[] parts = command.split(" ");
        String baseCommand = parts[0];
        String output;

        switch (baseCommand) {
            case "ps":
                output = executeSystemCommand("ps -aux");
                break;

            case "kill":
                if (parts.length > 1) {
                    output = executeSystemCommand("kill " + parts[1]);
                } else {
                    output = "kill: missing operand";
                }
                break;

            case "top":
                output = executeSystemCommand("top -n 1");
                break;

            case "df":
                output = executeSystemCommand("df -h");
                break;

            case "du":
                output = executeSystemCommand("du -sh *");
                break;

            case "dmesg":
                output = executeSystemCommand("dmesg");
                break;

            case "lsblk":
                output = executeSystemCommand("lsblk");
                break;

            case "iostat":
                output = executeSystemCommand("iostat");
                break;

            case "mount":
                if (parts.length > 2) {
                    output = executeSystemCommand("mount " + parts[1] + " " + parts[2]);
                } else {
                    output = "mount: missing operand";
                }
                break;

            case "umount":
                if (parts.length > 1) {
                    output = executeSystemCommand("umount " + parts[1]);
                } else {
                    output = "umount: missing operand";
                }
                break;

            default:
                output = "Unknown command: " + baseCommand;
                break;
        }

        outputList.getItems().add(output);
    }

    private static String executeSystemCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(command);
        }

        return output.toString();
    }
}
