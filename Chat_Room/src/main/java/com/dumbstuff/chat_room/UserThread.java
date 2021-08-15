/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dumbstuff.chat_room;

/**
 *
 * @author David
 */

import java.io.*;
import java.net.*;
import java.util.*;


public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    
    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            
            printUsers();
            
            String userName = reader.readLine();
            server.addUserName(userName);
            
            String serverMessage = "New User Connected: " + userName;
            server.broadcast(serverMessage, this);
            
            String clientMessage;
            
             do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("bye"));
 
            server.removeUser(userName, this);
            socket.close();
            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("bye"));
 
                server.removeUser(userName, this);
                socket.close();
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();      
        }
    }
    
    //send list of users to new connected slaves
    void printUsers() {
        if(server.hasUsers()) {
            writer.println("Connected Slaves: " + server.getUserNames());
        } else { 
            writer.println("No other slaves connected");
        }
    }
    
    //send message to chat
    void sendMessage(String message) {
        writer.println(message);
    }
}
