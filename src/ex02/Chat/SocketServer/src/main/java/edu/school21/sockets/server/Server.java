package edu.school21.sockets.server;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.services.ChatroomsService;
import edu.school21.sockets.services.MessagesService;
import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.models.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.util.Optional;
import java.util.Date;


public class Server {

    private ServerSocket server;
    private List<Socket> clientSockets;
    private List<BufferedReader> ins;
    private List<BufferedWriter> outs;
    private List<Optional<User>> userList;
    private List<Optional<Chatroom>> chatroomList;
    private UsersService usersService;
    private MessagesService messagesService;
    private ChatroomsService chatroomsService;
    private List<Date> startMessageDates;


    public Server(int port, UsersService usersService, MessagesService messagesService, ChatroomsService chatroomsService) {
            this.usersService = usersService;
            this.messagesService = messagesService;
            this.chatroomsService = chatroomsService;
            clientSockets = new ArrayList<>();
            userList = new ArrayList<>();
            chatroomList = new ArrayList<>();
            ins = new ArrayList<>();
            outs = new ArrayList<>();
            startMessageDates = new ArrayList<>();
            try {
                server = new ServerSocket(port);
                System.out.println("Server is running!");
            } catch (IOException e) {
                System.err.println(e);
            }

    }

    public void waitForClientConnection() {
        while(true) {
            try {
                Socket client = server.accept();
                clientSockets.add(client);
                ins.add(new BufferedReader(new InputStreamReader(clientSockets.get(clientSockets.size() - 1).getInputStream())));
                outs.add(new BufferedWriter(new OutputStreamWriter(clientSockets.get(clientSockets.size() - 1).getOutputStream())));
                userList.add(Optional.empty());
                chatroomList.add(Optional.empty());
                startMessageDates.add(messagesService.GetLastMessageDate());
                sendMessageToClient("Hello from server!", clientSockets.size() - 1);
                Thread thread = new Thread(() -> {
                    serverLoop(clientSockets.size() - 1);
                });
                thread.start();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    public String waitForClientMessage(int position) {
        String message = "";
        try {
            message = ins.get(position).readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        return message;
    }

    public void sendMessageToClient(String message, int position) {
        try {
            outs.get(position).write(message + "\n");
            outs.get(position).flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void serverLoop(int clientIndex) {
        try {
            boolean looping = true;
            boolean autorised = false;
            Optional<User> user = Optional.empty();
            while (looping) {
                String message = waitForClientMessage(clientIndex);
                JsonReader jsonReader = Json.createReader(new StringReader(message));
                JsonObject jsonObject = jsonReader.readObject();
                jsonReader.close();
                switch (jsonObject.getString("type")) {
                    case "signUp":
                        usersService.SignUp(jsonObject.getString("username"), jsonObject.getString("password"));
                        sendMessageToClient("Successful!", clientIndex);
                        break;
                    case "Create room":
                        CreateRoom(clientIndex, jsonObject.getString("name"));
                        sendMessageToClient("Room " + jsonObject.getString("name") + " was created.", clientIndex);
                        break;
                    case "Get rooms":
                        List<Chatroom> chatrooms = chatroomsService.GetChatrooms();
                        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

                        for (Chatroom item : chatrooms) {
                            arrayBuilder.add(item.toString());
                        }

                        JsonArray jsonArray = arrayBuilder.build();

                        JsonObject jsonChatroomsObject = Json.createObjectBuilder()
                                .add("type", "chatrooms")
                                .add("array", jsonArray)
                                .build();
                        sendMessageToClient(jsonToString(jsonChatroomsObject), clientIndex);
                        break;
                    case "Choose room":
                        ChooseRoom(clientIndex, jsonObject.getString("name"));
                        List<Message> list = messagesService.GetRoomHistory(chatroomList.get(clientIndex).get());
                        String result = chatroomList.get(clientIndex).get().getName() + " ---\n";
                        for (Message m : list) {
                            result += m + "\n";
                        }
                        result = result.substring(0, result.length() - 1);

                        JsonObject jsonHistoryObject = Json.createObjectBuilder()
                                .add("type", "history")
                                .add("text", result)
                                .build();
                        sendMessageToClient(jsonToString(jsonHistoryObject), clientIndex);
                        break;
                    case "signIn":
                        autorised = usersService.SignIn(jsonObject.getString("username"), jsonObject.getString("password"));
                        if (autorised) {
                            sendMessageToClient("Start messaging", clientIndex);
                            userList.set(clientIndex, usersService.GetUserByName(jsonObject.getString("username")));
                        } else {
                            looping = false;
                            sendMessageToClient("You were disconnected", clientIndex);
                        }
                        break;
                    case "message":
                        if (userList.get(clientIndex).isPresent() && autorised) {
                            long id = messagesService.CreateMessage(userList.get(clientIndex).get(), chatroomList.get(clientIndex).get(), jsonObject.getString("text"));
                            SendMessageToOthers(clientIndex, id);
                        }
                        break;
                    case "Exit":
                        autorised = false;
                        looping = false;
                        System.out.println("You have left the chat.");
                    default:
                        System.out.println("Incorrect type!");
                        sendMessageToClient("Error!", clientIndex);
                }
            }
        } catch (Exception e) {
            ;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            for (int i = 0; i < clientSockets.size(); i++) {
                clientSockets.get(i).close();
                ins.get(i).close();
                outs.get(i).close();
            }
            server.close();
            System.out.println("Server was stopped!");
        } finally {
            super.finalize();
        }
    }

    private void SendMessageToOthers( long clientId, long messageId) {
        for (int i = 0; i < userList.size(); i++) {
            try {
                Optional<User> user = userList.get(i);
                Optional<Chatroom> room = chatroomList.get(i);
                if (!user.isPresent() || !room.isPresent()) {
                    continue;
                }
                List<Message> messageList = messagesService.GetMessage(messageId);
                if (messageList.size() == 0) {
                    continue;
                }
                if (room.get().getIdentifier() != messageList.get(0).getRoom().getIdentifier()) {
                    continue;
                }
                String result = "";
                for (Message message : messageList) {
                    result += message + "\n";
                }
                result = result.substring(0, result.length() - 1);

                JsonObject jsonObject = Json.createObjectBuilder()
                        .add("type", "message")
                        .add("text", result)
                        .build();
                sendMessageToClient(jsonToString(jsonObject), i);
            } catch (SQLException e) {
                //System.err.println(e);
            }


        }
    }

    private static String jsonToString(JsonObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        return stringWriter.toString();
    }

    private void CreateRoom( long clientId, String name) {
        try {
            chatroomsService.CreateChatroom(name, userList.get((int) clientId).get());
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private void ChooseRoom( long clientId, String name) {
        try {
            chatroomList.set((int)clientId, chatroomsService.GetRoomByName(name));
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}
