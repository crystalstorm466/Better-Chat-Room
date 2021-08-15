package com.dumbstuff.chat_room;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
import java.io.*;
import java.net.*; 
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream; 


public class ChatServer  {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    
    public ChatServer(int port) {
        this.port = port;
    }
    
    public void execute() throws IOException {
    	
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
            System.out.println("Server is now listening on " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New Slave Connected");
                
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
            
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
   
    
    public static void main(String[] args) throws IOException, FileNotFoundException {
    	PrintStream o = new PrintStream(new File("chatoutput.txt"));
    	PrintStream console = System.out;
    	System.setOut(o);
    	System.setOut(console);
    	
        if (args.length < 1) {
            System.out.println("Syntax: Dumb Chat Server <port>");
            System.exit(0);    
        }
        
        
        int port = Integer.parseInt(args[0]);
        ChatServer server = new ChatServer(port);
        server.execute();
    }
    
    // sends message to all 
    void broadcast(String message, UserThread excludeUser) {
        for(UserThread aUser : userThreads) {
            if(aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }
        void addUserName(String userName) {
        userNames.add(userName);
    }
        void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }
        
        Set<String> getUserNames() {
        return this.userNames;
    }
        
        
        boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
        
    }
    
    

     