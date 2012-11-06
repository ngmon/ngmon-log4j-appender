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
public class Log4JSONImpl implements Log4JSON
{
    
    private Logger logger;

    public Log4JSONImpl(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }
    
    public void addAppender(Appender appender){
        this.logger.addAppender(appender);
    }
    
    public void log(String type, Level level, Map<String, Object> payload) { 
        
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        
        try {
            JsonGenerator gen = factory.createJsonGenerator(writer);
            gen.writeStartObject();
           
            gen.writeStringField("type",type);
            gen.writeStringField("level", level.toString());
            gen.writeArrayFieldStart("payload");
            
            Set<String> set = payload.keySet();
            
            for(String s:set){
                gen.writeStartObject();
                gen.writeStringField(s,(String)payload.get(s));
                gen.writeEndObject();
            }
            
            gen.writeEndArray();
            
            gen.writeEndObject();
            gen.close();
            writer.append((CharSequence) "\n");
        } catch(IOException e){
            System.out.println("problem while creating JsonGenerator, writing or while closing JsonGenerator");
        }
        
        
        this.logger.log(Level.INFO,writer.toString());    
        
    }

    public void log(String type, Level level, Object... variables) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
