package edu.school21.sockets.server;

import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server is running!");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void waitForClientConnection() {
        try {
            clientSocket = server.accept();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            sendMessageToClient("Hello from server!");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String waitForClientMessage() {
        String message = "";
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        return message;
    }

    public void sendMessageToClient(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            clientSocket.close();
            in.close();
            out.close();
            server.close();
            System.out.println("Server was stopped!");
        } finally {
            super.finalize();
        }
    }
}
