package com.beanit.iec61850bean.app;

/*import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;*/

public class Serverlet {
}

//public class PersonServlet extends HttpServlet {

/*    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String requestUrl = request.getRequestURI();
        String name = requestUrl.substring("/get/".length());
        if (name.equals("solarData")) {
            GetPVData pvData = new GetPVData("127.0.0.1", 6760);
            String json = "{\n";
            json += "\"solar data\": " + JSONObject.quote(pvData.run() + "\n");
            json += "}";
            response.getOutputStream().println(json);
        } else {
            response.getOutputStream().println("{}");
        }
    }
}*/