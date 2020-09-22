package com.beanit.iec61850bean.app;

import com.beanit.iec61850bean.internal.cli.ActionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket) throws IOException {
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
                String code = null;
                request = in.readLine();
                if(request == null) {
                    //out.println("No data");
                    code = request.substring(0, 4);
                }else{
                    code = request.substring(0, 4);
                    switch (code) {
                        case "WSPV":
                            String var = ConsoleServer.writeToSolar(request.substring(5));
                            out.println(var);
                            break;

                        case "GSPV":
                            out.println(ConsoleServer.getSolarPower());
                            break;

                        case "CH2G":
                            out.println(ConsoleServer.connectToGrid());
                            break;

                        case "DH2G":
                            out.println(ConsoleServer.disconnectFromGrid());
                            break;

                        case "GETM":
                            String str = ConsoleServer.printModel();
                            out.println(str.replace('\n', '^'));
                            break;


                        default:
                            out.println("No data");
                            break;
                    }
                }
//                if(request.substring(0,4).equals("WSPV")){
//                    out.println(ConsoleServer.writeToSolar(request.substring(5)));
//                }
            } catch (IOException | ActionException e) {
                e.printStackTrace();
            }
        }
    }
}
