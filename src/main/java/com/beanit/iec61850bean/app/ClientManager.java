package com.beanit.iec61850bean.app;

import com.beanit.iec61850bean.internal.cli.ActionException;

import java.io.IOException;

public class ClientManager {

    private static final String SERVER_IP  = "127.0.0.1";
    private static final int SERVER_PORT = 6760;

    public static void main(String[] args) throws IOException, ActionException, InterruptedException {
        int lineIndex = 12;
        ParsingSolarData parser = new ParsingSolarData(SERVER_IP, SERVER_PORT);
        GetPVData solarPower = new GetPVData(SERVER_IP, SERVER_PORT);
        CheckGridConnection gridConn = new CheckGridConnection(SERVER_IP, SERVER_PORT);
        GetModel printModel = new GetModel(SERVER_IP, SERVER_PORT);

        boolean active = true;
        while (active){
            boolean status_parser = parser.run(lineIndex);
            lineIndex ++;
//            String solarPowerValue = solarPower.run().split(":")[1];
            parser.writeHeater(lineIndex);
            parser.writeHouse(lineIndex);
            parser.writeSensor(lineIndex);
//            String home = solarPower.runHouse();
//            System.out.println("sheeps");
//            System.out.println(solarPowerValue);
//            System.out.println(home.substring(34));
//            System.out.println(solarPower.runHeater());
//            System.out.println(solarPower.runSensor());
//            gridConn.run(Float.parseFloat(solarPowerValue), Float.parseFloat(home.substring(34)));
//            printModel.run();
            Thread.sleep(3000);
        }
        System.out.println("Finished");
    }
}
