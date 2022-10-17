package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;

import static common.Method.*;
import static common.Status.*;

public class Session extends Thread {
    private static final Path DATA_PATH = Path.of(System.getProperty("user.dir"), "src/server/data");
    private final Socket socket;
    private final FileServer server;

    public Session(Socket socket, FileServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            processRequest(input, output);



            //Request request = createRequest(input.readUTF());
/*
            Request request = createRequest(input);

            //System.out.printf("Received: %s%n", request);
            //String name = input.readUTF();
            int length = input.readInt();                // read length of incoming message
            byte[] message = new byte[length];
            input.readFully(message, 0, message.length);

            String fileName = UUID.randomUUID().toString();
            Path file = Path.of(DATA_PATH + "/" + fileName);

            Files.write(file, message);

            int id = server.files.size() + 1;
            server.files.put(id, fileName);

            //Response response = processRequest(request);
            //output.writeUTF(response.toString());
            output.writeUTF("200 " + id);

            */
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(DataInputStream input, DataOutputStream output) throws IOException {
        String message = input.readUTF();
        if (message.startsWith("PUT")) {
            String fileName = message.substring(4);
            int fileSize = input.readInt();
            byte[] fileContent = new byte[fileSize];
            input.readFully(fileContent, 0, fileSize);
            Path filePath = Path.of(DATA_PATH + "/" + fileName);
            Files.write(filePath, fileContent);
            int id = server.files.size() + 1;
            server.files.put(id, fileName);
            output.writeUTF("200 " + id);
        } else if (message.startsWith("GET")) {
            Request req = new Request(GET, message.substring(4));

            //Response response = processRequest(req);

            //output.writeUTF(response.toString());

            try {
                String fileName = getFileName(req.getContent());
                byte[] file = Files.readAllBytes(Path.of(DATA_PATH + "/" + fileName));
                output.writeUTF("200");
                output.writeInt(file.length);
                output.write(file);
            } catch (NoSuchFileException e) {
                output.writeUTF("404");
            }

        } else if (message.startsWith("DELETE")) {
            Request req = new Request(DELETE, message.substring(7));
            Response response = processRequest(req);
            output.writeUTF(response.toString());
        } if (message.startsWith("EXIT")) {
            handleExitRequest();
        }
        //socket.close();

        //throw new UnsupportedOperationException();
    }

    private Request createRequest(String message) {
        if (message.startsWith("PUT")) {
            return new Request(PUT, message.substring(4));
        } else if (message.startsWith("GET")) {
            return new Request(GET, message.substring(4));
        } else if (message.startsWith("DELETE")) {
            return new Request(DELETE, message.substring(7));
        } if (message.startsWith("EXIT")) {
            return new Request(EXIT, "");
        }
        throw new UnsupportedOperationException();
    }

    private Response processRequest(Request request) throws IOException {
        String content = request.getContent();
        switch (request.getMethod()) {
            case PUT:
                return handlePutRequest(content);
            case GET:
                return handleGetRequest(content);
            case DELETE:
                return handleDeleteRequest(content);
            case EXIT:
                return handleExitRequest();
            default:
                throw new UnsupportedOperationException();
        }
    }

    private Response handlePutRequest(String content) throws IOException {


        int delimiterIdx = content.indexOf(' ');
        String fileName = content.substring(0, delimiterIdx);
        String fileContent = content.substring(delimiterIdx + 1);
        Path file = Path.of(DATA_PATH + "/" + fileName);
        if (Files.notExists(file)) {
            Files.writeString(file, fileContent);
            int id = server.files.size() + 1;
            server.files.put(id, fileName);
            return new Response(OK, id);
        }
        return new Response(FORBIDDEN);
    }

    private Response handleGetRequest(String content) throws IOException {
        String fileName = getFileName(content);
        Path file = Path.of(DATA_PATH + "/" + fileName);

        if (Files.exists(file)) {
            byte[] fileContent = Files.readAllBytes(file);
            return new Response(OK, fileContent);
        }

        return new Response(NOT_FOUND);
    }

    private Response handleDeleteRequest(String content) throws IOException {
        String fileName = getFileName(content);
        Path file = Path.of(DATA_PATH + "/" + fileName);
        if (Files.exists(file)) {
            Files.delete(file);
            return new Response(OK);
        }
        return new Response(NOT_FOUND);
    }

    private Response handleExitRequest() {
        server.stop();
        return new Response(OK);
    }

    private String getFileName(String content) {
        if (content.startsWith("BY_ID")) {
            int fileId = Integer.parseInt(content.substring(6));
            return server.files.get(fileId);
        } else if (content.startsWith("BY_NAME")) {
            return content.substring(8);
        }
        throw new UnsupportedOperationException();
    }
}