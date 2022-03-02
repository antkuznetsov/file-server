package server;

import common.Method;
import common.Status;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileServer {
    private final int port;
    private static final Path DATA_PATH = Path.of("src", "server", "data");

    public FileServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = server.accept();
                try (
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    Request request = parseInputMessage(input.readUTF());
                    Response response = null;
                    switch (request.getMethod()) {
                        case GET: {
                            String fileName = request.getContent();
                            Path file = Path.of(DATA_PATH + "/" + fileName);
                            if (Files.exists(file)) {
                                String fileContent = Files.readString(file);
                                response = new Response(Status.OK, fileContent);
                            } else {
                                response = new Response(Status.NOT_FOUND);
                            }
                            break;
                        }
                        case PUT: {
                            String content = request.getContent();
                            int separatorIdx = content.indexOf(' ');
                            String fileName = content.substring(0, separatorIdx);
                            String fileContent = content.substring(separatorIdx + 1);
                            Path file = Path.of(DATA_PATH  + "/" +  fileName);
                            if (Files.notExists(file)) {
                                Files.writeString(file, fileContent);
                                response = new Response(Status.OK);
                            } else {
                                response = new Response(Status.FORBIDDEN);
                            }
                            break;
                        }
                        case DELETE: {
                            String fileName = request.getContent();
                            Path file = Path.of(DATA_PATH + "/" + fileName);
                            if (Files.exists(file)) {
                                Files.delete(file);
                                response = new Response(Status.OK);
                            } else {
                                response = new Response(Status.NOT_FOUND);
                            }
                            break;
                        }
                        case EXIT:
                            System.exit(0);
                            break;
                    }
                    output.writeUTF(response.toString());
                } catch (IOException e) {
                    System.out.println("Cannot create IO Stream!");
                } finally {
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot create ServerSocket!");
        }
    }

    private Request parseInputMessage(String message) {
        int separatorIdx = message.indexOf(' ');
        Method method = Method.getByName(message.substring(0, separatorIdx));
        String content = message.substring(separatorIdx + 1);
        return new Request(method, content);
    }
}
