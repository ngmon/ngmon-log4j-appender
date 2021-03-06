/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 *
 * @author Stefan
 */
public class Log4JSONImpl implements Log4JSON {

    private Logger logger;

    public Log4JSONImpl(String loggerName, Appender appender) {
        this.logger = Logger.getLogger(loggerName);
        this.logger.addAppender(appender);
    }

    public void log(String type, Level level, Map<String, Object> payload) {

        if(level==null) throw new IllegalArgumentException("no level specified");

        MyEvent event = new MyEvent();
        event.setType(type);
        event.setPayload(payload);
        
        this.logger.log(level,event);      

    }

    public void log(String type, Level level, Object... variables) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
