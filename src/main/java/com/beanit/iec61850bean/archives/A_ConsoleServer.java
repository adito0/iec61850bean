/*
 * Copyright 2011 The IEC61850bean Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.beanit.iec61850bean.archives;

import com.beanit.iec61850bean.*;
import com.beanit.iec61850bean.app.ClientHandler;
import com.beanit.iec61850bean.internal.cli.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A_ConsoleServer {

  private static final String SERVER_IP  = "127.0.0.1";
  private static final int SERVER_PORT = 6760;

  private static final String WRITE_VALUE_KEY = "w";
  private static final String WRITE_VALUE_KEY_DESCRIPTION = "write value to model node";

  private static final String GET_VALUE_KEY = "r";

  private static final String PRINT_SERVER_MODEL_KEY = "p";
  private static final String PRINT_SERVER_MODEL_KEY_DESCRIPTION = "print server's model";

  private static final IntCliParameter portParam =
      new CliParameterBuilder("-p")
          .setDescription(
              "The port to listen on. On unix based systems you need root privilages for ports < 1000.")
          .buildIntParameter("port", 102);

  private static final StringCliParameter modelFileParam =
      new CliParameterBuilder("-m")
          .setDescription("The SCL file that contains the server's information model.")
          .setMandatory()
          .buildStringParameter("model-file");
  private static final ActionProcessor actionProcessor = new ActionProcessor(new ActionExecutor());
  private static ServerModel serverModel;
  private static ServerSap serverSap = null;

  private static final int PORT = 6760;
  private static ArrayList<ClientHandler> clients = new ArrayList<>();
  private static ExecutorService pool = Executors.newFixedThreadPool(10);

  public static void main(String[] args) throws IOException, ActionException {

    List<CliParameter> cliParameters = new ArrayList<>();
    cliParameters.add(modelFileParam);
    cliParameters.add(portParam);

    CliParser cliParser =
            new CliParser("iec61850bean-console-server", "An IEC 61850 MMS console server.");
    cliParser.addParameters(cliParameters);

    try {
      cliParser.parseArguments(args);
    } catch (CliParseException e1) {
      System.err.println("Error parsing command line parameters: " + e1.getMessage());
      System.out.println("hi there");
      System.out.println(cliParser.getUsageString());
      System.exit(1);
    }

    List<ServerModel> serverModels = null;
    try {
      serverModels = SclParser.parse(modelFileParam.getValue());
    } catch (SclParseException e) {
      System.out.println("Error parsing SCL/ICD file: " + e.getMessage());
      return;
    }

    serverSap = new ServerSap(102, 0, null, serverModels.get(0), null);
    serverSap.setPort(portParam.getValue());

    Runtime.getRuntime()
            .addShutdownHook(
                    new Thread() {
                      @Override
                      public void run() {
                        if (serverSap != null) {
                          serverSap.stop();
                        }
                        System.out.println("Server was stopped.");
                      }
                    });

    serverModel = serverSap.getModelCopy();

    ServerSocket listener = new ServerSocket(PORT);
    //ServerSocket welcomeSocket = new ServerSocket(6788);
    //ServerSocket switchSocket = new ServerSocket(6790);
    while (true) {
      /*Socket connectionSocket = welcomeSocket.accept();
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      try {
        String str = inFromClient.readLine();
        System.out.println("From parser: " + str);
        ActionExecutor exe = new ActionExecutor();
        exe.actionCalled(WRITE_VALUE_KEY, str);


      } catch (IOException e) {
        e.printStackTrace();
      }*/
      System.out.println("[SERVER] Waiting for client connection...");
      Socket client = listener.accept();
      System.out.println("[SERVER] Connected to client!");
      //PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      ClientHandler clientThread = new ClientHandler(client);
      clients.add(clientThread);
      //out.println("You are connected");
      pool.execute(clientThread);
    }
  }

  public static String writeToSolar(String value) throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    exe.actionCalled(WRITE_VALUE_KEY, value, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    exe.actionCalled(PRINT_SERVER_MODEL_KEY);
    return "Updated!";
  }

  public static void printModel() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    exe.actionCalled(PRINT_SERVER_MODEL_KEY);
  }


//      Socket conSwitchSocket = switchSocket.accept();
//      BufferedReader fromClient = new BufferedReader(new InputStreamReader(conSwitchSocket.getInputStream()));
//      try {
//        String str = fromClient.readLine();
//        System.out.println("From parser: " + str);
//        ActionExecutor exe  = new ActionExecutor();
//        exe.actionCalled(WRITE_VALUE_KEY, str);
//
//
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }

//    serverSap.startListening(new EventListener());
//
//    actionProcessor.addAction(
//        new Action(PRINT_SERVER_MODEL_KEY, PRINT_SERVER_MODEL_KEY_DESCRIPTION));
//    actionProcessor.addAction(new Action(WRITE_VALUE_KEY, WRITE_VALUE_KEY_DESCRIPTION));
//
//    actionProcessor.start();


  private static class EventListener implements ServerEventListener {

    @Override
    public void serverStoppedListening(ServerSap serverSap) {
      System.out.println("The SAP stopped listening");
    }

    @Override
    public List<ServiceError> write(List<BasicDataAttribute> bdas) {
      for (BasicDataAttribute bda : bdas) {
        System.out.println("got a write request: " + bda);
      }
      return null;
    }
  }

  private static class ActionExecutor implements ActionListener {

    @Override
    public void actionCalled(String actionKey) throws ActionException {
      try {
        switch (actionKey) {
          case PRINT_SERVER_MODEL_KEY:
            System.out.println("** Printing model.");

            System.out.println(serverModel);

            break;
          case WRITE_VALUE_KEY:
//            System.out.println("Enter reference to write (e.g. myld/MYLN0.do.da.bda): ");
//            String reference = actionProcessor.getReader().readLine();
//            System.out.println("Enter functional constraint of referenced node: ");
//            String fcString = actionProcessor.getReader().readLine();
            String reference = "ied1lDevice1/MMXU1.TotW.mag.f";
            String fcString = "MX";

            Fc fc = Fc.fromString(fcString);
            if (fc == null) {
              System.out.println("Unknown functional constraint.");
              return;
            }

            ModelNode modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
            if (modelNode == null) {
              System.out.println(
                  "A model node with the given reference and functional constraint could not be found.");
              return;
            }

            if (!(modelNode instanceof BasicDataAttribute)) {
              System.out.println("The given model node is not a basic data attribute.");
              return;
            }

            BasicDataAttribute bda =
                (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

            System.out.println("Enter value to write: ");
            // String valueString = actionProcessor.getReader().readLine();
            String valueString = "toWrite";

            try {
              setBdaValue(bda, valueString);
            } catch (Exception e) {
              System.out.println(
                  "The console server does not support writing this type of basic data attribute.");
              return;
            }

            List<BasicDataAttribute> bdas = new ArrayList<>();
            bdas.add(bda);
            serverSap.setValues(bdas);

            System.out.println("Successfully wrote data.");
            Socket clientSocket = new Socket("localhost", 6789);
            System.out.println("hi there");
            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
            toServer.writeBytes(bda.toString()+'\n');
            System.out.println("BDA " + bda);

            break;

          default:
            break;
        }
      } catch (Exception e) {
        throw new ActionException(e);
      }
    }

    public void actionCalled(String actionKey, String toWrite, String reference, String fcString) throws ActionException {
      try {
        switch (actionKey) {
          case PRINT_SERVER_MODEL_KEY:
            System.out.println("** Printing model.");

            System.out.println(serverModel);

            break;
          case GET_VALUE_KEY:
            Fc fc = Fc.fromString(fcString);
            if (fc == null) {
              System.out.println("Unknown functional constraint.");
              return;
            }

            ModelNode modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
            if (modelNode == null) {
              System.out.println(
                      "A model node with the given reference and functional constraint could not be found.");
              return;
            }

            if (!(modelNode instanceof BasicDataAttribute)) {
              System.out.println("The given model node is not a basic data attribute.");
              return;
            }

            BasicDataAttribute bda =
                    (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

            break;
          case WRITE_VALUE_KEY:
//            System.out.println("Enter reference to write (e.g. myld/MYLN0.do.da.bda): ");
//            String reference = actionProcessor.getReader().readLine();
//            System.out.println("Enter functional constraint of referenced node: ");
//            String fcString = actionProcessor.getReader().readLine();

//            String reference = "ied1lDevice1/CSWI1.Pos.Oper.ctlVal";
//            String fcString = "CO";
            String valueString = toWrite;

//            if(toWrite.substring(0,1).equals("M")) {
//              reference = "ied1lDevice1/MMXU1.TotW.mag.f";
//              fcString = "MX";
//              valueString = toWrite.substring(1);
//            }
            fc = Fc.fromString(fcString);
            if (fc == null) {
              System.out.println("Unknown functional constraint.");
              return;
            }

            modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
            if (modelNode == null) {
              System.out.println(
                      "A model node with the given reference and functional constraint could not be found.");
              return;
            }

            if (!(modelNode instanceof BasicDataAttribute)) {
              System.out.println("The given model node is not a basic data attribute.");
              return;
            }

            bda =
                    (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

            //System.out.println("Enter value to write: ");
            // String valueString = actionProcessor.getReader().readLine();


            try {
              setBdaValue(bda, valueString);
            } catch (Exception e) {
              System.out.println(
                      "The console server does not support writing this type of basic data attribute.");
              return;
            }

            List<BasicDataAttribute> bdas = new ArrayList<>();
            bdas.add(bda);
            serverSap.setValues(bdas);

            System.out.println("Successfully wrote data.");
//            Socket clientSocket = new Socket("localhost", 6789);
//            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
//            toServer.writeBytes(bda.toString()+'\n');
            System.out.println(bda);

            break;

          default:
            break;
        }
      } catch (Exception e) {
        throw new ActionException(e);
      }
    }

    private void setBdaValue(BasicDataAttribute bda, String valueString) {
      if (bda instanceof BdaFloat32) {
        float value = Float.parseFloat(valueString);
        ((BdaFloat32) bda).setFloat(value);
      } else if (bda instanceof BdaFloat64) {
        double value = Float.parseFloat(valueString);
        ((BdaFloat64) bda).setDouble(value);
      } else if (bda instanceof BdaInt8) {
        byte value = Byte.parseByte(valueString);
        ((BdaInt8) bda).setValue(value);
      } else if (bda instanceof BdaInt8U) {
        short value = Short.parseShort(valueString);
        ((BdaInt8U) bda).setValue(value);
      } else if (bda instanceof BdaInt16) {
        short value = Short.parseShort(valueString);
        ((BdaInt16) bda).setValue(value);
      } else if (bda instanceof BdaInt16U) {
        int value = Integer.parseInt(valueString);
        ((BdaInt16U) bda).setValue(value);
      } else if (bda instanceof BdaInt32) {
        int value = Integer.parseInt(valueString);
        ((BdaInt32) bda).setValue(value);
      } else if (bda instanceof BdaInt32U) {
        long value = Long.parseLong(valueString);
        ((BdaInt32U) bda).setValue(value);
      } else if (bda instanceof BdaInt64) {
        long value = Long.parseLong(valueString);
        ((BdaInt64) bda).setValue(value);
      } else if (bda instanceof BdaBoolean) {
        boolean value = Boolean.parseBoolean(valueString);
        ((BdaBoolean) bda).setValue(value);
      } else {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public void quit() {
      System.out.println("** Stopping server.");
      serverSap.stop();
      return;
    }
  }
}

