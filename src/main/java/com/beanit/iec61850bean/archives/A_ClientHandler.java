package com.beanit.iec61850bean.archives;

import com.beanit.iec61850bean.app.ConsoleServer;
import com.beanit.iec61850bean.internal.cli.ActionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class A_ClientHandler implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public A_ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader((client.getInputStream())));
        //out  = new PrintWriter(client.getOutputStream(), true);
        this.out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        while (true) {
            String request = null;
            try {
                request = in.readLine();
                if(request.substring(0,4).equals("WSPV")){
                    out.println(ConsoleServer.writeToSolar(request.substring(5)));
                }
                //System.out.println("REQUEST: " + Server.actionAdded(request));
            } catch (IOException | ActionException e) {
                e.printStackTrace();
            }
        }
    }
}
