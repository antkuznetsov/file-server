package server;

public class Main {
    public static void main(String[] args) {
        FileServer server = new FileServer(8080);
        server.start();
    }
}
