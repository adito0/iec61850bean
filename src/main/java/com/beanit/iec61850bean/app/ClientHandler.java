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
            String var1 = "0";
            String request = null;
            try {
                String code = null;
                request = in.readLine();
                if(request == null) {
                    //out.println("No data");
                    code = request.substring(0, 4);
                }else{
                    code = request.substring(0, 4);
                    System.out.println("code");
                    System.out.println(code);
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

                        case "GHEC":
                            out.println(ConsoleServer.getHouseConsumption());
                            break;

                        case "GHEA":
                            System.out.println("hi!!!!!!");
                            System.out.println(ConsoleServer.getHeater());
                            System.out.println("bye!!!!!!");
                            out.println(ConsoleServer.getHeater());
                            break;

                        case "GSEN":
                            out.println(ConsoleServer.getSensor());
                            break;

                        case "SHEC":
                            var1 = ConsoleServer.writeToHouse(request.substring(5));
                            out.println(var1);
                            break;

                        case "SHEA":
                            var1 = ConsoleServer.writeToHeater(request.substring(5));
                            out.println(var1);
                            break;

                        case "SSEN":
                            var1 = ConsoleServer.writeToSensor(request.substring(5));
                            out.println(var1);
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
