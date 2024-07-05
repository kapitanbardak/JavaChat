package edu.school21.sockets.app;

import edu.school21.sockets.client.Client;
import org.apache.commons.cli.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        System.out.print("> ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (!input.equals("signUp")) {
            System.err.println("Incorrect input!");
        }
        System.out.println("Enter username:");
        System.out.print("> ");
        input = sc.nextLine();
        client.sendMessageToServer(input);
        System.out.println("Enter password:");
        System.out.print("> ");
        input = sc.nextLine();
        client.sendMessageToServer(input);
        System.out.println(client.getMessageFromServer());
    }
}