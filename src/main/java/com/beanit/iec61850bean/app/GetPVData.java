package com.beanit.iec61850bean.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GetPVData {

    private String SERVER_IP = null;
    private int SERVER_PORT = -1;

    public GetPVData(String ip, int port) {
        this.SERVER_IP = ip;
        this.SERVER_PORT = port;
    }

    public String run() throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String command = "GSPV ";
        out.println(command);
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return serverResponse;
    }

    public String runHeater() throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String command = "GHEA";
        out.println(command);
        System.out.println("Boo");
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return serverResponse;
    }

    public String runSensor() throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String command = "GSEN";
        out.println(command);
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return serverResponse;
    }

    public String runHouse() throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String command = "GHEC";
        out.println(command);
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return serverResponse;
    }
}
/*

            String  str = null;
            try {
                System.out.println("Started");
                str=inFromClient.readLine();
                System.out.println("From server: " + str);

            } catch (IOException e) {
                e.printStackTrace();
            }
            str  =  str.split(":")[1].substring(1);
            try {
                System.out.println(Float.parseFloat(str));
                if (Float.parseFloat(str) < housePowerConsumption) {
                    Socket clientSocket = new Socket("localhost", 6790);
                    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
                    System.out.println("Grid connected mode ");
                    toServer.writeBytes("true\n");
                } else {
                    Socket clientSocket = new Socket("localhost", 6790);
                    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
                    System.out.println("Grid disconnected");
                    toServer.writeBytes("false\n");
                }
            }catch (NumberFormatException e){
                System.out.println("CSW1 state: " +  str);
            }
        }
    }
}
*/