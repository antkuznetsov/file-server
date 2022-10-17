package client;

import common.Status;
import server.Request;
import server.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static common.Method.*;
import static common.Status.OK;

public class Client {
    private final String host;
    private final int port;

    private static final Path DATA_PATH = Path.of(System.getProperty("user.dir"), "src/client/data");

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        System.out.println("Client started!");
        try (
                Socket socket = new Socket(host, port);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
        ) {
            Action action = CLIManager.requestAction();
            switch (action) {
                case GET_FILE:
                    getFile(input, output);
                    break;
                case CREATE_FILE:
                    createFile(input, output);
                    break;
                case DELETE_FILE:
                    deleteFile(input, output);
                    break;
                case EXIT:
                    exit(input, output);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(DataInputStream input, DataOutputStream output) throws IOException {
        String fileName = CLIManager.requestFileName();
        String fileNameOnServer = CLIManager.requestFileNameOnServer();

        byte[] file = Files.readAllBytes(Path.of("src/client/data", fileName));

        output.writeUTF(String.format("PUT %s", fileNameOnServer));
        output.writeInt(file.length);
        output.write(file);

        System.out.println("The request was sent.");

        //Response response = getResponse(input) ;

        String response = input.readUTF();

        String code = response.substring(0, 3);
        String fileId = response.substring(4);

        if (code.equals("200")) {
            System.out.printf("Response says that file is saved! ID = %s%n", fileId);
        } else {
            System.out.println("Response says that creating the file was forbidden!");
        }

        /*
        printResponse(response,
                String.format("Response says that file is saved! ID = %s", response.getFileId()),
                "Response says that creating the file was forbidden!"
        );
         */
    }

    private void getFile(DataInputStream input, DataOutputStream output) throws IOException {
        AccessType accessType = CLIManager.requestAccessType();
        String requestContent = createRequestContent(accessType);
        Request request = new Request(GET, requestContent);
        sendRequest(request, output);

        //String response = input.readUTF();

        //Response response = getResponse(input.readUTF()) ;

        String status = input.readUTF();

        if (status.equals("200")) {
            int fileSize = input.readInt();
            byte[] fileContent = new byte[fileSize];
            input.readFully(fileContent, 0, fileSize);

            String fileName = CLIManager.requestFileNameOnClient();

            Path filePath = Path.of(DATA_PATH + "/" + fileName);
            Files.write(filePath, fileContent);
            System.out.println("File saved on the hard drive!");
        } else {
            System.out.println("The response says that this file is not found!");
        }

        /*
        printResponse(response,
                String.format("The content of the file is: " + response.getContent()),
                "The response says that this file is not found!"
        );
         */
    }

    private void deleteFile(DataInputStream input, DataOutputStream output) throws IOException {
        AccessType accessType = CLIManager.requestAccessType();
        String requestContent = createRequestContent(accessType);;
        Request request = new Request(DELETE, requestContent);
        sendRequest(request, output);

        //Response response = getResponse(input.readUTF()) ;

        String response = input.readUTF();

        if (response.equals("200")) {
            System.out.println("The response says that this file was deleted successfully!");
        } else {
            System.out.println("The response says that this file is not found!");
        }

        /*
        printResponse(response,
                "The response says that the file was successfully deleted!",
                "The response says that the file was not found!"
        );
         */
    }

    private void printResponse(Response response, String successMessage,  String failMessage) {
        if (response.getStatus() == OK) {
            System.out.println(successMessage);
        } else {
            System.out.println(failMessage);
        }
    }

    private void exit(DataInputStream input, DataOutputStream output) throws IOException {
        Request request = new Request(EXIT, "");
        sendRequest(request, output);
    }

    private void sendRequest(Request request, DataOutputStream output) throws IOException {
        output.writeUTF(request.toString());
        System.out.println("The request was sent.");
    }

    private String createRequestContent(AccessType accessType) {
        switch (accessType) {
            case BY_NAME:
                String fileName = CLIManager.requestFileName();
                return String.format("BY_NAME %s", fileName);
            case BY_ID:
                int fileId = CLIManager.requestFileId();
                return String.format("BY_ID %d", fileId);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private Response getResponse(DataInputStream input) throws IOException {
        String message = input.readUTF();
        input.readInt();
        input.read();
        /*
        PUT: 200 42
        GET 200 byte[]
        DELETE 200
         */

        if (message.length() > 3) {
            String code = message.substring(0, 3);
            String content = message.substring(4);
            return new Response(Status.getByCode(code));
        }
        return new Response(Status.getByCode(message));
    }
}
