/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 *
 * @author Stefan
 */
public class NewJsonLayout extends Layout{

    @Override
    public String format(LoggingEvent event) {
        
        System.out.println(event.getMessage());
        Map<String, Object> messageData = retrieveJsonFromString((String)event.getMessage());
        System.out.println("safsd"+messageData);
        
        List<Map<String,String>> payloadList = (List<Map<String,String>>) messageData.get("payload");   
        messageData.remove("payload");
        
        
        
        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        
        try {
            JsonGenerator gen = factory.createJsonGenerator(writer);
            gen.writeStartObject();
            
            gen.writeFieldName("Event");
            
            gen.writeStartObject();
            
            Set<String> messageDataKeys = messageData.keySet();
            
            for(String s:messageDataKeys){
                gen.writeStringField(s,(String)messageData.get(s));
            }
            
            gen.writeStringField("occurenceTime", new Long(event.getTimeStamp()).toString());
            gen.writeStringField("application", "....");
            gen.writeStringField("process", "....");
            gen.writeStringField("processId", "....");
            
            
            gen.writeArrayFieldStart("payload");
            for(Map<String,String> m:payloadList){
                if(m.size()==1){
                    gen.writeStartObject();
                    String onlyKey = (String) m.keySet().toArray()[0];
                    gen.writeStringField(onlyKey,m.get(onlyKey));
                    gen.writeEndObject();
                }
            }
            gen.writeEndArray();
            
            gen.writeEndObject();
            gen.writeEndObject();
            gen.close();
            writer.append((CharSequence) "\n");
        } catch(IOException e){
            System.out.println("problem while creating JsonGenerator, writing or while closing JsonGenerator");
        }
        
        System.out.println("layout json:"+writer.toString());
        
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
    
}
