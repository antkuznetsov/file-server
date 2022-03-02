package client;

import common.Method;
import common.Status;
import server.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static common.Status.OK;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Client started!");
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file)");
            String action = scanner.nextLine();
            switch (action) {
                case "1":
                    getFile(input, output);
                    break;
                case "2":
                    createFile(input, output);
                    break;
                case "3":
                    deleteFile(input, output);
                    break;
                case "exit":
                    exit(input, output);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFile(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter filename:");
        String fileName = scanner.nextLine();

        System.out.println("Enter file content:");
        String fileContent = scanner.nextLine();

        String msg = String.format("%s %s %s", Method.PUT, fileName, fileContent);
        output.writeUTF(msg);

        System.out.println("The request was sent.");

        Response response = parseInputMessage(input.readUTF()) ;

        if (response.getStatus() == OK) {
            System.out.println("The response says that file was created!");
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private static void getFile(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter filename:");
        String fileName = scanner.nextLine();

        String msg = String.format("%s %s", Method.GET, fileName);
        output.writeUTF(msg);

        System.out.println("The request was sent.");

        Response response = parseInputMessage(input.readUTF()) ;

        if (response.getStatus() == OK) {
            System.out.println("The content of the file is: " + response.getContent());
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private static void deleteFile(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter filename:");
        String fileName = scanner.nextLine();

        String request = String.format("%s %s", Method.DELETE, fileName);
        output.writeUTF(request);

        System.out.println("The request was sent.");

        Response response = parseInputMessage(input.readUTF()) ;

        if (response.getStatus() == OK) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private static Response parseInputMessage(String message) {
        if (message.length() > 3) {
            Status status = Status.getByCode(Integer.parseInt(message.substring(0, 3)));
            String content = message.substring(4);
            return new Response(status, content);
        }
        return new Response(Status.getByCode(Integer.parseInt(message)));
    }

    private static void exit(DataInputStream input, DataOutputStream output) throws IOException {
        String request = "exit";
        output.writeUTF(request);

        System.out.println("The request was sent.");
    }
}
