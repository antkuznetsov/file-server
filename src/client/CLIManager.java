package client;

import java.util.Scanner;
import java.util.UUID;

public class CLIManager {
    private final static Scanner scanner = new Scanner(System.in);

    public static Action requestAction() {
        String actionCode = requestString("Enter action (1 - get a file, 2 - create a file, 3 - delete a file)");
        return Action.getByCode(actionCode);
    }

    public static AccessType requestAccessType() {
        String accessTypeCode = requestString("Do you want to get the file by name or by id (1 - name, 2 - id):");
        return AccessType.getByCode(accessTypeCode);
    }

    public static String requestFileName() {
        return requestString("Enter filename:");
    }

    public static String requestFileNameOnServer() {
        String fileNameOnServer = requestString("Enter name of the file to be saved on server:");
        if (!fileNameOnServer.isBlank()) {
            return fileNameOnServer;
        }
        return UUID.randomUUID().toString();
    }

    public static String requestFileNameOnClient() {
        return requestString("The file was downloaded! Specify a name for it:");
    }

    public static String requestFileContent() {
        return requestString("Enter file content:");
    }

    public static int requestFileId() {
        return requestInteger("Enter id:");
    }

    private static String requestString(String requestText) {
        System.out.println(requestText);
        return scanner.nextLine();
    }

    private static int requestInteger(String requestText) {
        return Integer.parseInt(requestString(requestText));
    }
}
