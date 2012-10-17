/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Stefan
 */
public class JsonLayout extends Layout {

    @Override
    public String format(LoggingEvent event) {
        String message = (String) event.getMessage();
        Map<String, Object> messageData = retrieveJsonFromString(message);
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        try {
            JsonGenerator gen = factory.createJsonGenerator(writer);
            gen.writeStartObject();
            
            writeDataByJsonGenerator(messageData, gen);
            
            gen.writeStringField("threadName", event.getThreadName());
            gen.writeStringField("occurenceTime", convertMillisToISO8601(event.getTimeStamp()));
            gen.writeEndObject();
            gen.close();
            writer.append((CharSequence) "\n");
        } catch(IOException e){
            System.out.println("problem while creating JsonGenerator, writing or while closing JsonGenerator");
        }
        return writer.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    public void activateOptions() {
    }

    private Map<String, Object> retrieveJsonFromString(String s) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> messageData = new HashMap<String, Object>();
        try {
            messageData = mapper.readValue(s, HashMap.class);
        } catch (IOException ex) {
            System.out.println("error while string parsing occured occured");
        }
        return messageData;
    }

    private void writeDataByJsonGenerator(Map<String, Object> messageData, JsonGenerator gen) {
        Iterator it = messageData.keySet().iterator();

        try {
            while (it.hasNext()) {
                String item = (String) it.next();
                if (messageData.get(item) instanceof String) {
                    String value = (String) messageData.get(item);
                    gen.writeStringField(item, value);
                } else {
                    Integer i = (Integer) messageData.get(item);
                    gen.writeNumberField(item, i.intValue());
                }
            }
        } catch (IOException e) {
            System.out.println("error while writing to file");
        }
    }
    
    private String convertMillisToISO8601(long millis){
        DateFormat ISO_8601_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        Date date = new Date(millis);
        String outputString = ISO_8601_DATE_TIME.format(date);
        return outputString;        
    }
}