/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;



/**
 *
 * @author Stefan Uhercik
 */
public class JsonLogger {

    private Logger logger;
    
    public JsonLogger(String name) {
        this.logger = Logger.getLogger(name);
    }
    
    public void logJson(String type,int severity, int priority){
        String outputString = "{" + "\"type\":" + type + ",\"severity\":" + severity + ",\"priority\":" + priority + "}";
        logger.log(Level.DEBUG, outputString);
        System.out.println("this was done");
    }
    
    public void logJson(String message){
        logger.log(Level.DEBUG, message);
    }
    
    public void addAppender(Appender appender){
        logger.addAppender(appender);
    }
    
    
}
