package com.beanit.iec61850bean.archives;

import java.io.*;
import java.net.Socket;

public class A_GetPVData {}

/*    private String SERVER_IP = null;
    private int SERVER_PORT = -1;

    public GetPVData(String ip, int port) {
        this.SERVER_IP = ip;
        this.SERVER_PORT = port;
    }

    public boolean run(){
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
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
}*/
