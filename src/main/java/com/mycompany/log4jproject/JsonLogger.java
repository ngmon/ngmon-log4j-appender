/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Stefan Uhercik
 */
public class JsonLogger {

    private Logger logger;

    public JsonLogger(String name) {
        this.logger = Logger.getLogger(name);
    }

    public void logJson(String type, int severity, int priority) {
        String outputString = "{" + "\"type\":\"" + type + "\",\"severity\":" + severity + ",\"priority\":" + priority + "}";
        logger.log(Level.DEBUG, outputString);
    }

    public void logJson(String message) {
        if (!isJsonInStringValid(message)) {
            System.out.println("here");
            System.out.println("this is done");
            throw new IllegalArgumentException("wrong json string format");
        }
        System.out.println("this is not done");
        logger.log(Level.DEBUG, message);
    }

    public void addAppender(Appender appender) {
        logger.addAppender(appender);
    }

    private boolean isJsonInStringValid(String str) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> items = new HashMap<String, Object>();
        try {
            items = mapper.readValue(str, HashMap.class);
        } catch (IOException ex) {
            System.out.println("error while string parsing occured occured");
            return false;
        }

        if (!items.containsKey("type")
                || !items.containsKey("severity")
                || !items.containsKey("priority")) {
            return false;
        }
        return true;

    }
}
