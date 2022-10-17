package server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileServer implements Serializable {
    private static final Path DATA_PATH = Path.of("src/server/data");
    public Map<Integer, String> files = new HashMap<>();

    private ServerSocket serverSocket;
    private volatile boolean keepProcessing = true;

    public FileServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            loadState();
        } catch (IOException e) {
            handle(e);
        }
    }

    public void start() {
        System.out.println("Server started!");
        while (keepProcessing) {
            try {
                Socket socket = serverSocket.accept();
                Session session = new Session(socket, this);
                session.start(); // it does not block current thread
            } catch (IOException e) {
                handle(e);
            }
        }
    }

    private void handle(Exception e) {
        if (!(e instanceof SocketException)) {
            e.printStackTrace();
        }
    }

    public void stop() {
        keepProcessing = false;
        saveState();
        closeIgnoringException(serverSocket);
    }

    public void saveState() {
        String fileName = "src/server/bin/server.data";
        try (
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadState() {
        String fileName = "src/server/bin/server.data";
        try (
                FileInputStream fis = new FileInputStream(fileName);
                ObjectInputStream oos = new ObjectInputStream(fis)
        ) {
            files = (HashMap) oos.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeIgnoringException(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignore) {

            }
        }
    }
}
