package com.beanit.iec61850bean.app;

import com.beanit.iec61850bean.internal.cli.ActionException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class ParsingSolarData {

    private String SERVER_IP = null;
    private String filePath = null;
    private String filePath_house = null;
    private int SERVER_PORT = -1;

    public ParsingSolarData(String ip, int port) {
        this.SERVER_IP = ip;
        this.SERVER_PORT = port;
        this.filePath = "/home/aram485/Downloads/iec61850bean/solar.csv";
        this.filePath_house = "/home/aram485/Downloads/iec61850bean/house.csv";
    }

    public boolean run(int lineIndex) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String line = "";
        String cvsSplitBy = ",";

        if (lineIndex > 11){
            try (Stream<String> lines = Files.lines(Paths.get(filePath))){
                line = lines.skip(lineIndex-1).findFirst().get();
            }
            if (line != null){
                String[] power = line.split(cvsSplitBy);
                String command = "WSPV " + power[power.length - 1];
                out.println(command);
                String serverResponse = input.readLine();
                System.out.println("Server says: " + serverResponse);
                socket.close();
                return true;
            }else{
                socket.close();
                return false;
            }
        }
        socket.close();
        return false;
    }

    public boolean writeHeater(int lineIndex) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String command = "SHEA " + "40";
        out.println(command);
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return true;
    }

    public boolean writeSensor(int lineIndex) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String command = "SSEN " + "22";
        out.println(command);
        String serverResponse = input.readLine();
        System.out.println("Server says: " + serverResponse);
        socket.close();
        return true;
    }

    public boolean writeHouse(int lineIndex) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new FileReader(filePath_house));
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = "";
        String cvsSplitBy = ",";

        if (lineIndex > 1){
            try (Stream<String> lines = Files.lines(Paths.get(filePath_house))){
                line = lines.skip(lineIndex-1).findFirst().get();
            }
            if (line != null){
                String[] power = line.split(cvsSplitBy);
                float power_int = (Float.parseFloat(power[power.length - 1])*1000);
                System.out.println(power_int);
                String command1 = "SHEC " + String.valueOf(power_int);
                out.println(command1);
                String serverResponse = input.readLine();
                System.out.println("Server says: " + serverResponse);
                socket.close();
                return true;
            }else{
                socket.close();
                return false;
            }
        }
        socket.close();
        return true;
    }
}

        /*
        while ((line = br.readLine()) != null) {
            if (lineToRead > 10) {
                String[] power = line.split(cvsSplitBy);
                String command = "WSPV " + power[power.length - 1];
                out.println(command);
                String serverResponse = input.readLine();
                System.out.println("Server says: " + serverResponse);
            }
            lineToRead++;
        }
}


    public static void main(String[] args) throws IOException, ActionException, InterruptedException {

        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader br = new BufferedReader(new FileReader("/home/aram485/Downloads/iec61850bean/solar.csv"));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String csvFile = "/aram485/Downloads/solar.csv";
        String line = "";
        String cvsSplitBy = ",";
        int lineToRead = 0;

        while((line = br.readLine())!= null){
            if (lineToRead > 10){
                String[] power = line.split(cvsSplitBy);
                String command = "WSPV " + power[power.length - 1];
                out.println(command);
                String serverResponse = input.readLine();
                System.out.println("Server says: " + serverResponse);
                Thread.sleep(3000);
            }
            lineToRead++;

        }
    }
}


        String csvFile = "/aram485/Downloads/solar.csv";
        String line = "";
        String cvsSplitBy = ",";
        //File fileToRead = new File(homedir, "/Downloads/iec61850bean/solar.csv");

       try (BufferedReader br = new BufferedReader(new FileReader("/home/aram485/Downloads/iec61850bean/solar.csv"))) {
            int count = -1;

            while ((line = br.readLine()) != null) {
                count++;
//                // use comma as separator

                if(count > 10) {
                    String[] power = line.split(cvsSplitBy);
                    Socket clientSocket = new Socket("localhost", 6788);
                    DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
                    System.out.println("Solar reading sent: " + power[power.length - 1]);
                    toServer.writeBytes('M' + power[power.length - 1]+ '\n');
                    Thread.sleep(3000);
                }
           }
//
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/

