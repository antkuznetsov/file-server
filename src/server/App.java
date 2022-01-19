package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, String> storage = new HashMap<>();

    public App() {
        boolean isRun = true;
        while (isRun) {
            String command = scanner.nextLine();
            if (command.startsWith("add")) {
                String fileName = parseFileName(command);
                if (!storage.containsKey(fileName) && isValidName(fileName)) {
                    storage.put(fileName, null);
                    System.out.printf("The file %s added successfully%n", fileName);
                } else {
                    System.out.printf("Cannot add the file %s%n", fileName);
                }
            } else if (command.startsWith("get")) {
                String fileName = parseFileName(command);
                if (storage.containsKey(fileName) && isValidName(fileName)) {
                    System.out.printf("The file %s was sent%n", fileName);
                } else {
                    System.out.printf("The file %s not found%n", fileName);
                }
            } else if (command.startsWith("delete")) {
                String fileName = parseFileName(command);
                if (storage.containsKey(fileName) && isValidName(fileName)) {
                    storage.remove(fileName);
                    System.out.printf("The file %s was deleted%n", fileName);
                } else {
                    System.out.printf("The file %s not found%n", fileName);
                }
            } else if (command.startsWith("exit")) {
                isRun = false;
            }
        }
    }

    private String parseFileName(String command) {
        return command.substring(command.indexOf(" ")).trim();
    }

    private boolean isValidName(String name) {
        try {
            return name.startsWith("file") && Integer.parseInt(name.substring(4)) <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
