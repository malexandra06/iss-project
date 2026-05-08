package minishop.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {

    private final int port;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        while (true) {
            System.out.println("Waiting for clients...");
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());
            processRequest(client);
        }
    }

    protected abstract void processRequest(Socket client);
}