package edu.school21.sockets.app;

import edu.school21.sockets.client.Client;
import org.apache.commons.cli.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

public class Main {

    private static volatile boolean logged = false;
    private static volatile boolean working = false;

    private static boolean loop = true;

    public static void main(String[] args) {

        synchronized (Main.class) {
            working = true;
        }

        Options options = new Options();
        options.addOption("sp", "server-port", true, "Specify the server port number");

        int port = -1;
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("server-port")) {
                port = Integer.parseInt(cmd.getOptionValue("server-port"));
            } else {
                System.err.println("Server port not specified");
                System.exit(1);
            }

        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            System.exit(1);
        }
        Client client = new Client(port);
        System.out.println(client.getMessageFromServer());

        Thread thread = new Thread(() -> MessageLoop(client));
        thread.start();

        String input = "";
        Scanner sc = new Scanner(System.in);
        logged = false;
        while (loop) {
            System.out.print("> ");
            input = sc.nextLine();

            if (input.equals("Exit")) {
                loop = false;
                break;
            }
            if (!logged) {
                switch (input) {
                    case "signUp":
                        signUpFunction(client);
                        break;
                    case "signIn":
                        synchronized (Main.class) {
                            logged = signInFunction(client);
                            loop = logged;
                        }
                        break;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            } else {
                sendMessage(client, input);
            }
        }
        synchronized (Main.class) {
            working = false;
        }
        client.closeConnection();
        thread.interrupt();
    }

    private static void signUpFunction(Client client) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username:");
        System.out.print("> ");
        String username = sc.nextLine();
        if (username.equals("Exit")) {
            loop = false;
            return;
        }
        System.out.println("Enter password:");
        System.out.print("> ");
        String password = sc.nextLine();
        if (password.equals("Exit")) {
            loop = false;
            return;
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("type", "signUp")
                .add("username", username)
                .add("password", password)
                .build();
        client.sendMessageToServer(jsonToString(jsonObject));
        System.out.println(client.getMessageFromServer());
    }

    private static boolean signInFunction(Client client) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username:");
        System.out.print("> ");
        String username = sc.nextLine();
        if (username.equals("Exit")) {
            return false;
        }
        System.out.println("Enter password:");
        System.out.print("> ");
        String password = sc.nextLine();
        if (password.equals("Exit")) {
            return false;
        }
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("type", "signIn")
                .add("username", username)
                .add("password", password)
                .build();
        client.sendMessageToServer(jsonToString(jsonObject));
        String answer = client.getMessageFromServer();
        System.out.println(answer);
        return answer.equals("Start messaging");
    }

    private static void sendMessage(Client client, String message) {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("type", "message")
                .add("text", message)
                .build();
        client.sendMessageToServer(jsonToString(jsonObject));
    }

    private static String jsonToString(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        return stringWriter.toString();
    }

    private static void MessageLoop(Client client) {

        while (working && !Thread.currentThread().isInterrupted())  {
            if (logged) {
                String answer = client.getMessageFromServer();
                try {
                    JsonReader jsonReader = Json.createReader(new StringReader(answer));
                    JsonObject jsonObject = jsonReader.readObject();
                    jsonReader.close();
                    if (jsonObject.getString("type").equals("message")) {
                        System.out.print("\033[2K");
                        System.out.print("\r");
                        System.out.println(jsonObject.getString("text"));
                        System.out.print("> ");
                    }
                } catch (Exception e) {
                    ;
                }
            }
        }
    }
}