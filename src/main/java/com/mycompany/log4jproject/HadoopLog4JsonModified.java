/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.mycompany.log4jproject;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ContainerNode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This offers a log layout for JSON, with some test entry points. It's purpose
 * is to allow Log4J to generate events that are easy for other programs to
 * parse, but which are somewhat human-readable.
 *
 * Some features.
 *
 * <ol> <li>Every event is a standalone JSON clause</li> <li>Time is published
 * as a time_t event since 1/1/1970 -this is the fastest to generate.</li>
 * <li>An ISO date is generated, but this is cached and will only be accurate to
 * within a second</li> <li>the stack trace is included as an array</li> </ol>
 *
 * A simple log event will resemble the following
 * <pre>
 *     {"name":"test","time":1318429136789,"date":"2011-10-12 15:18:56,789","level":"INFO","thread":"main","message":"test message"}
 * </pre>
 *
 * An event with an error will contain data similar to that below (which has
 * been reformatted to be multi-line).
 *
 * <pre>
 *     {
 *     "name":"testException",
 *     "time":1318429136789,
 *     "date":"2011-10-12 15:18:56,789",
 *     "level":"INFO",
 *     "thread":"quoted\"",
 *     "message":"new line\n and {}",
 *     "exceptionclass":"java.net.NoRouteToHostException",
 *     "stack":[
 *         "java.net.NoRouteToHostException: that box caught fire 3 years ago",
 *         "\tat org.apache.hadoop.log.TestLog4Json.testException(TestLog4Json.java:49)",
 *         "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
 *         "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)",
 *         "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)",
 *         "\tat java.lang.reflect.Method.invoke(Method.java:597)",
 *         "\tat junit.framework.TestCase.runTest(TestCase.java:168)",
 *         "\tat junit.framework.TestCase.runBare(TestCase.java:134)",
 *         "\tat junit.framework.TestResult$1.protect(TestResult.java:110)",
 *         "\tat junit.framework.TestResult.runProtected(TestResult.java:128)",
 *         "\tat junit.framework.TestResult.run(TestResult.java:113)",
 *         "\tat junit.framework.TestCase.run(TestCase.java:124)",
 *         "\tat junit.framework.TestSuite.runTest(TestSuite.java:232)",
 *         "\tat junit.framework.TestSuite.run(TestSuite.java:227)",
 *         "\tat org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:83)",
 *         "\tat org.apache.maven.surefire.junit4.JUnit4TestSet.execute(JUnit4TestSet.java:59)",
 *         "\tat org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.executeTestSet(AbstractDirectoryTestSuite.java:120)",
 *         "\tat org.apache.maven.surefire.suite.AbstractDirectoryTestSuite.execute(AbstractDirectoryTestSuite.java:145)",
 *         "\tat org.apache.maven.surefire.Surefire.run(Surefire.java:104)",
 *         "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
 *         "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)",
 *         "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)",
 *         "\tat java.lang.reflect.Method.invoke(Method.java:597)",
 *         "\tat org.apache.maven.surefire.booter.SurefireBooter.runSuitesInProcess(SurefireBooter.java:290)",
 *         "\tat org.apache.maven.surefire.booter.SurefireBooter.main(SurefireBooter.java:1017)"
 *         ]
 *     }
 * </pre>
 */
public class HadoopLog4JsonModified extends Layout {

    /**
     * Jackson factories are thread safe when constructing parsers and
     * generators. They are not thread safe in configure methods; if there is to
     * be any configuration it must be done in a static intializer block.
     */
    private static final JsonFactory factory = new MappingJsonFactory();
    public static final String JSON_TYPE = "application/json";
    public static final String OCCURENCE_TIME = "time";
    public static final String APPLICATION = "application";
    public static final String PROCESS = "process";
    public static final String PROCESS_ID = "processId";
    public static final String TYPE = "type";
    public static final String PAYLOAD = "payload";
    private final DateFormat dateFormat;

    
    private class ParsedEventMessage {      
        private String type;
        private Map<String, Object> payload;
        public String getType() {   return type;      }
        public void setType(String type) {   this.type = type;     }
        public Map<String, Object> getPayload() {   return payload;     }
        public void setPayload(Map<String, Object> payload) {     this.payload = payload;      }
    }
    
    
    public HadoopLog4JsonModified() {
        dateFormat = new ISO8601DateFormat();
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
        Date date = new Date(timeStamp);
        json.writeStringField("occurenceTime", dateFormat.format(date));
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

    /**
     * For use in tests
     *
     * @param json incoming JSON to parse
     * @return a node tree
     * @throws java.io.IOException on any parsing problems
     */
    public static ContainerNode parse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode jsonNode = mapper.readTree(json);
        if (!(jsonNode instanceof ContainerNode)) {
            throw new IOException("Wrong JSON data: " + json);
        }
        return (ContainerNode) jsonNode;
    }
}

