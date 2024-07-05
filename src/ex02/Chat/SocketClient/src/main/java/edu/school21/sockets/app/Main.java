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
import java.util.List;
import java.util.ArrayList;
import javax.json.JsonArray;
import javax.json.JsonValue;

public class Main {

    private static volatile boolean logged = false;
    private static volatile boolean roomed = false;
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

        showMenu(client);

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
            if (logged && roomed) {
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

    private static void showMenu(Client client) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        logged = false;
        roomed = false;
        while (loop) {
            System.out.println("1. SignIn");
            System.out.println("2. SignUp");
            System.out.println("3. Exit");
            System.out.print("> ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        synchronized (Main.class) {
                            logged = signInFunction(client);
                            if (!logged) break;
                            loop = showRoomMenu(client);
                        }
                        break;
                    case 2:
                        signUpFunction(client);
                        break;
                    case 3:
                        System.out.println("Exiting the program.");
                        return;
                    default:
                        System.out.println("Invalid input. Please choose a menu item.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
    }

    private static boolean showRoomMenu(Client client) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        boolean loop = true;
        while (loop) {
            System.out.println("1. Create room");
            System.out.println("2. Choose room");
            System.out.println("3. Exit");
            System.out.print("> ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        createRoom(client);
                        break;
                    case 2:
                        loop = chooseRoom(client);
                        break;
                    case 3:
                        System.out.println("Exiting the room menu.");
                        loop = false;
                        break;
                    default:
                        System.out.println("Invalid input. Please choose a menu item.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
        return false;
    }

    private static void createRoom(Client client) {
        System.out.println("Enter chatroom name:");
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("type", "Create room")
                .add("name", name)
                .build();
        client.sendMessageToServer(jsonToString(jsonObject));
        String answer = client.getMessageFromServer();
        System.out.println(answer);
    }

    private static boolean chooseRoom(Client client) {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("type", "Get rooms")
                .build();
        client.sendMessageToServer(jsonToString(jsonObject));
        String answer = client.getMessageFromServer();

        JsonReader jsonReader = Json.createReader(new StringReader(answer));
        JsonObject jsonChatroomsObject = jsonReader.readObject();
        jsonReader.close();
        JsonArray jsonArray = jsonChatroomsObject.getJsonArray("array");
        List<String> resultList = new ArrayList<>();
        for (JsonValue jsonValue : jsonArray) {
            String valueWithoutQuotes = jsonValue.toString().replace("\"", "");
            resultList.add(valueWithoutQuotes);
        }
        resultList.add("Exit");
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (loop) {
            System.out.println("Rooms:");
            for (long i = 0; i < resultList.size(); i++) {
                System.out.println(i + 1 + ". " + resultList.get((int)i));
            }
            System.out.print("> ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice > resultList.size() || choice < 1) {
                    System.out.println("Invalid input. Please choose a menu item.");
                    continue;
                }

                if (choice == resultList.size()) {
                    loop = false;
                    continue;
                }

                JsonObject jsonRoomObject = Json.createObjectBuilder()
                        .add("type", "Choose room")
                        .add("name", resultList.get(choice - 1))
                        .build();
                client.sendMessageToServer(jsonToString(jsonRoomObject));
                String response = client.getMessageFromServer();
                JsonReader jsonListReader = Json.createReader(new StringReader(response));
                JsonObject jsonListObject = jsonListReader.readObject();
                jsonListReader.close();
                System.out.println(jsonListObject.getString("text"));
                roomed = true;
                loop = messageLoop(client);
            } else {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
        return false;
    }

    private static boolean messageLoop(Client client) {
        while (true) {
            System.out.print("> ");
            Scanner sc = new Scanner(System.in);
            String m = sc.nextLine();
            if (m.equals("Exit")) {
                client.closeConnection();
                break;
            } else {
                sendMessage(client, m);
            }
        }
        return false;
    }
}