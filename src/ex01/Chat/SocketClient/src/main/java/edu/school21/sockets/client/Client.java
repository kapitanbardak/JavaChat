package edu.school21.sockets.client;

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Client {

    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public Client(int port) {
        try {
            clientSocket = new Socket("localhost", port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void sendMessageToServer(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String getMessageFromServer() {
        String result = "";
        try {
            result = in.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        return result;
    }

    public void closeConnection() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            ;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } finally {
            super.finalize();
        }
    }
}
