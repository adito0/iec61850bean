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
package com.beanit.iec61850bean.app;

import com.beanit.iec61850bean.BasicDataAttribute;
import com.beanit.iec61850bean.BdaBoolean;
import com.beanit.iec61850bean.BdaFloat32;
import com.beanit.iec61850bean.BdaFloat64;
import com.beanit.iec61850bean.BdaInt16;
import com.beanit.iec61850bean.BdaInt16U;
import com.beanit.iec61850bean.BdaInt32;
import com.beanit.iec61850bean.BdaInt32U;
import com.beanit.iec61850bean.BdaInt64;
import com.beanit.iec61850bean.BdaInt8;
import com.beanit.iec61850bean.BdaInt8U;
import com.beanit.iec61850bean.Fc;
import com.beanit.iec61850bean.ModelNode;
import com.beanit.iec61850bean.SclParseException;
import com.beanit.iec61850bean.SclParser;
import com.beanit.iec61850bean.ServerEventListener;
import com.beanit.iec61850bean.ServerModel;
import com.beanit.iec61850bean.ServerSap;
import com.beanit.iec61850bean.ServiceError;
import com.beanit.iec61850bean.internal.cli.Action;
import com.beanit.iec61850bean.internal.cli.ActionException;
import com.beanit.iec61850bean.internal.cli.ActionListener;
import com.beanit.iec61850bean.internal.cli.ActionProcessor;
import com.beanit.iec61850bean.internal.cli.CliParameter;
import com.beanit.iec61850bean.internal.cli.CliParameterBuilder;
import com.beanit.iec61850bean.internal.cli.CliParseException;
import com.beanit.iec61850bean.internal.cli.CliParser;
import com.beanit.iec61850bean.internal.cli.IntCliParameter;
import com.beanit.iec61850bean.internal.cli.StringCliParameter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleServer {

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
    while (true) {
      System.out.println("[SERVER] Waiting for client connection...");
      Socket client = listener.accept();
      System.out.println("[SERVER] Connected to client!");
      ClientHandler clientThread = new ClientHandler(client);
      clients.add(clientThread);
      pool.execute(clientThread);
    }
  }

  public static String writeToSolar(String value) throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //exe.actionCalled(WRITE_VALUE_KEY, value, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    exe.actionCalled(WRITE_VALUE_KEY, value, "myHomelDevice1/MMXU1.TotW.mag.f", "MX");

    return "Updated!";
  }

  public static String writeToHouse(String value) throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //exe.actionCalled(WRITE_VALUE_KEY, value, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    exe.actionCalled(WRITE_VALUE_KEY, value, "myHomefullhouse/MMXU1.TotW.mag.f", "MX");//need to change
    System.out.println("Wrote to house"+value.toString());
    return "Updated!";
  }

  public static String writeToHeater(String value) throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //exe.actionCalled(WRITE_VALUE_KEY, value, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    exe.actionCalled(WRITE_VALUE_KEY, value, "myHomeheater/MMXU1.TotW.mag.f", "MX");
    System.out.println("Wrote to heater"+value.toString());
    return "Updated!";
  }
  public static String writeToSensor(String value) throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //exe.actionCalled(WRITE_VALUE_KEY, value, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    exe.actionCalled(WRITE_VALUE_KEY, value, "myHomesensor/MMXU1.TotW.mag.f", "MX");
    System.out.println("Wrote to sensor"+value.toString());
    return "Updated!";
  }

  public static String connectToGrid() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    exe.actionCalled(WRITE_VALUE_KEY, "true", "myHomegridInterconnection/CSWI1.Pos.Oper.ctlVal", "CO");
    exe.actionCalled(WRITE_VALUE_KEY, "true", "myHomegridInterconnection/XSWI1.Pos.Oper.ctlVal", "CO");
    return "Connected to grid!";
  }

  public static String disconnectFromGrid() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    exe.actionCalled(WRITE_VALUE_KEY, "false", "myHomegridInterconnection/CSWI1.Pos.Oper.ctlVal", "CO");
    return "Disconnected from grid!";
  }

  public static String getSolarPower() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //return exe.actionCalled(GET_VALUE_KEY, null, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    return exe.actionCalled(GET_VALUE_KEY, null, "myHomelDevice1/MMXU1.TotW.mag.f", "MX");
  }

  public static String getHouseConsumption() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //return exe.actionCalled(GET_VALUE_KEY, null, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    return exe.actionCalled(GET_VALUE_KEY, null, "myHomefullhouse/MMXU1.TotW.mag.f", "MX");//need to change
  }

  public static String getHeater() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //return exe.actionCalled(GET_VALUE_KEY, null, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    System.out.println("This was called");
    return exe.actionCalled(GET_VALUE_KEY, null, "myHomeheater/MMXU1.TotW.mag.f", "MX");
  }

  public static String getSensor() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    //return exe.actionCalled(GET_VALUE_KEY, null, "ied1lDevice1/MMXU1.TotW.mag.f", "MX");
    return exe.actionCalled(GET_VALUE_KEY, null, "myHomesensor/MMXU1.TotW.mag.f", "MX");
  }

  public static String printModel() throws ActionException {
    ActionExecutor exe = new ActionExecutor();
    return (exe.actionCalled(PRINT_SERVER_MODEL_KEY, null, null, null));
  }


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
//        switch (actionKey) {
//          case PRINT_SERVER_MODEL_KEY:
//            System.out.println("** Printing model.");
//
//            System.out.println(serverModel);
//
//            break;
//        }
        ;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

/*    public String actionCalled(String actionKey) throws ActionException {
      try {
        switch (actionKey) {
          case PRINT_SERVER_MODEL_KEY:
            System.out.println("** Printing model.");

            System.out.println(serverModel);
            return serverModel.toString();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }*/

    public String actionCalled(String actionKey, String toWrite, String reference, String fcString) throws ActionException {
      try {
        Fc fc = null;
        ModelNode modelNode = null;
        BasicDataAttribute bd = null;

        switch (actionKey) {
          case PRINT_SERVER_MODEL_KEY:
            System.out.println("** Printing model.");
            String str = serverModel.toString();
            return str;

          case GET_VALUE_KEY:
            fc = Fc.fromString(fcString);
            if (fc == null) {
              System.out.println("Unknown functional constraint.");
              return null;
            }

            modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
            if (modelNode == null) {
              System.out.println(
                      "A model node with the given reference and functional constraint could not be found.");
              return null;
            }

            if (!(modelNode instanceof BasicDataAttribute)) {
              System.out.println("The given model node is not a basic data attribute.");
              return null;
            }

            BasicDataAttribute bda =
                    (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

            return bda.toString();
            //break;

          case WRITE_VALUE_KEY:
            String valueString = toWrite;
            fc = Fc.fromString(fcString);
            if (fc == null) {
              System.out.println("Unknown functional constraint.");
              return null;
            }

            modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
            if (modelNode == null) {
              System.out.println(
                      "A model node with the given reference and functional constraint could not be found.");
              return null;
            }

            if (!(modelNode instanceof BasicDataAttribute)) {
              System.out.println("The given model node is not a basic data attribute.");
              return null;
            }

            bda = (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));


            try {
              setBdaValue(bda, valueString);
            } catch (Exception e) {
              System.out.println(
                      "The console server does not support writing this type of basic data attribute.");
              return null;
            }

            List<BasicDataAttribute> bdas = new ArrayList<>();
            bdas.add(bda);
            serverSap.setValues(bdas);

            System.out.println("Successfully wrote data.");

            return "OK";
            //break;

          default:
            return null;
            //break;
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

