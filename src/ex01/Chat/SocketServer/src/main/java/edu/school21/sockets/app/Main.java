package edu.school21.sockets.app;

import edu.school21.sockets.server.Server;
import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.UsersService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ComponentScan;
import org.apache.commons.cli.*;

@Component
@ComponentScan("edu.school21.sockets.config")
public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", "port", true, "Specify the port number");

        CommandLineParser parser = new DefaultParser();
        int port = -1;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("port")) {
                port = Integer.parseInt(cmd.getOptionValue("port"));
            } else {
                System.err.println("Port not specified");
                System.exit(1);
            }

        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            System.exit(1);
        }
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        try {
            UsersService usersService = context.getBean("usersServiceImpl", UsersService.class);
            MessagesService messagesService = context.getBean("messagesServiceImpl", MessagesService.class);
            Server server = new Server(port, usersService, messagesService);

            Thread connectionThread = new Thread(() -> {
                try {
                    server.waitForClientConnection();
                } catch (Exception e) {
                    System.err.println(e);
                }
            });

            connectionThread.start();

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            context.close();
        }
    }
}