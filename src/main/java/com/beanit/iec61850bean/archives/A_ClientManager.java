package com.beanit.iec61850bean.archives;

import com.beanit.iec61850bean.internal.cli.ActionException;

import java.io.IOException;

public class A_ClientManager {

    private static final String SERVER_IP  = "127.0.0.1";
    private static final int SERVER_PORT = 6760;

    public static void main(String[] args) throws IOException, ActionException, InterruptedException {
        int lineIndex = 12;
        A_ParsingSolarData parser = new A_ParsingSolarData(SERVER_IP, SERVER_PORT);
        boolean active = true;
        while (active){
            active = parser.run(lineIndex);
            lineIndex++;
            Thread.sleep(3000);
        }
        System.out.println("Finished");
    }
}
