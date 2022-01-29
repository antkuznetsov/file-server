package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {
    private final Socket socket;

    public Session(Socket socketForClient) {
        this.socket = socketForClient;
    }

    @Override
    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String receivedMsg = input.readUTF();
            System.out.println("Received: " + receivedMsg);

            String msg = "All files were sent!";
            output.writeUTF(msg);
            System.out.println("Sent: " + msg);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
