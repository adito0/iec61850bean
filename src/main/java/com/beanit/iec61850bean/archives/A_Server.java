package com.beanit.iec61850bean.archives;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A_Server {

    private static final int PORT = 6760;
    private static ArrayList<A_ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static ArrayList<String> actions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        while(true){
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Connected to client!");
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            A_ClientHandler clientThread = new A_ClientHandler(client);
            clients.add(clientThread);
            //out.println("You are connected");
            pool.execute(clientThread);
        }

    }

    public static String actionAdded(String action){
        actions.add(action);
        return "Added action " + action;
    }
}
