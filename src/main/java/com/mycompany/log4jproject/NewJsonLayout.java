/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Stefan
 */
public class NewJsonLayout extends Layout {
    
    private static final JsonFactory factory = new MappingJsonFactory();
    public static final String JSON_TYPE = "application/json";
    public static final String LEVEL = "level";
    public static final String OCCURENCE_TIME = "time";
    public static final String APPLICATION = "application";
    public static final String PROCESS = "process";
    public static final String PROCESS_ID = "processId";
    public static final String TYPE ="type";
    public static final String PAYLOAD = "payload";    
    private final DateFormat dateFormat;
    
    public NewJsonLayout() {
        dateFormat = new ISO8601DateFormat();
    }
    
    private class ParsedEventMessage {      
        private String type;
        private Map<String, Object> payload;
        public String getType() {   return type;      }
        public void setType(String type) {   this.type = type;     }
        public Map<String, Object> getPayload() {   return payload;     }
        public void setPayload(Map<String, Object> payload) {     this.payload = payload;      }
    }
    
    /**
     * Parses event message and fills the ParsedEventMessage by data from it
     * 
     * @param message message
     * @return parsedEventMessage
     */
    private ParsedEventMessage parseEventMessage(String message) {
        
        ParsedEventMessage parsedEventMessage = new ParsedEventMessage();
        
        Map<String, Object> messageData = retrieveJsonFromString(message);
        List<Map<String, Object>> payloadList = (List<Map<String, Object>>) messageData.get("payload");
        messageData.remove("payload");
        
        parsedEventMessage.setType((String) messageData.get("type"));
        
        Map<String, Object> payloadMap = new HashMap<String, Object>();
        
        for (Map<String, Object> m : payloadList) {
            if (m.size() == 1) {
                String onlyKey = (String) m.keySet().toArray()[0];
                payloadMap.put(onlyKey, m.get(onlyKey));
            }
        }
        
        parsedEventMessage.setPayload(payloadMap);
        
        return parsedEventMessage;
    }
    
    /**
     * Converts string in json format to map
     * 
     * @param s string in correct json format
     * @return map
     */
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

    /**
     * @return the mime type of JSON
     */
    @Override
    public String getContentType() {
        return JSON_TYPE;
    }
    
    @Override
    public String format(LoggingEvent event) {
        try {
            return toJson(event);
        } catch (IOException e) {
            //this really should not happen, and rather than throw an exception
            //which may hide the real problem, the log class is printed
            //in JSON format. The classname is used to ensure valid JSON is 
            //returned without playing escaping games
            return "{ \"logfailure\":\"" + e.getClass().toString() + "\"}";
        }
    }

    /**
     * Convert an event to JSON
     *
     * @param event the event -must not be null
     * @return a string value
     * @throws java.io.IOException on problems generating the JSON
     */
    public String toJson(LoggingEvent event) throws IOException {
        StringWriter writer = new StringWriter();
        toJson(writer, event);
        return writer.toString();
    }    
    
    /**
     * Convert an event to JSON
     *
     * @param writer the destination writer
     * @param event the event -must not be null
     * @return the writer
     * @throws java.io.IOException on problems generating the JSON
     */
    public Writer toJson(final Writer writer, final LoggingEvent event)
            throws IOException {
        
        ParsedEventMessage parsedEventMessage = this.parseEventMessage((String) event.getMessage());
        toJson(writer,
                event.getLevel().toString(),
                parsedEventMessage.getType(),
                event.getTimeStamp(),
                "app",
                "proc",
                "procId",
                parsedEventMessage.getPayload());
        
        return writer;
    }    
    
    
    /**
     * Build a JSON entry from the parameters. This is public for testing.
     *
     * @param writer destination
     * @param level level
     * @param type type
     * @param timeStamp timestamp
     * @param application application
     * @param process process
     * @param processId processId
     * @return writer
     * @throws IOException
     */
    public Writer toJson(final Writer writer,
            final String level,
            final String type,
            final long timeStamp,
            final String application,
            final String process,
            final String processId,
            final Map<String, Object> payload) throws IOException {
        
        JsonGenerator json = factory.createJsonGenerator(writer);
        json.writeStartObject();
        json.writeFieldName("Event");
        json.writeStartObject();
        json.writeStringField("level", level);
        json.writeStringField("type", type);
        //Date date = new Date(timeStamp);
        //json.writeStringField("occurenceTime", dateFormat.format(date));
        json.writeStringField("application", application);
        json.writeStringField("process", process);
        json.writeStringField("processId", processId);
        json.writeArrayFieldStart("payload");
        
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            json.writeStartObject();
            if(entry.getValue() instanceof String) {
                json.writeStringField(entry.getKey(), (String) entry.getValue());
            }
            else {
                System.out.println("the value is: "+entry.getValue());
                json.writeNumberField(entry.getKey(), (Integer) entry.getValue());
            }
            json.writeEndObject();
        }
        
        json.writeEndArray();
        json.writeEndObject();
        json.writeEndObject();
        json.flush();        
        json.close();
        writer.append('\n');
        return writer;
    }

    /**
     * This appender does not ignore throwables
     *
     * @return false, always
     */
    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    /**
     * Do nothing
     */
    @Override
    public void activateOptions() {
    }
}
