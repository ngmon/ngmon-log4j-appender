/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Stefan
 */
public class JsonLoggerTest extends TestCase {
    
    private JsonLogger testingLogger;
    private String filePath = "C:\\Users\\Stefan\\Desktop\\School Projects\\Bachelor thesis\\Log4jProject\\src\\main\\java\\com\\mycompany\\log4jproject\\newfile.txt";
    
    public JsonLoggerTest(String testName) {
        super(testName);
    }
    
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        testingLogger = new JsonLogger("testing");
        
    }
    
    @After
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of logJson method, of class JsonLogger.
     */
    @org.junit.Test
    public void testLogJson3args() {               
        String type = "asd";
        int severity = 5;
        int priority = 11;
        
        String expectedString = "{" + "\"type\":\"" + type + "\",\"severity\":" + severity + ",\"priority\":" + priority + "}";
        try {
            //attaching FileAppender to logger
            FileAppender appender = new FileAppender(new PatternLayout("%m%n"), filePath);
            testingLogger.addAppender(appender);
            testingLogger.logJson(type,severity,priority);        
            
            //browsing the log file to till we get last line, what should be our record
            FileInputStream fileIS = new FileInputStream(filePath);
            InputStreamReader isReader = new InputStreamReader(fileIS);
            BufferedReader br = new BufferedReader(isReader);
            
            String line = null;
            String previousLine = null;            
            while((line=br.readLine())!=null){
                
                previousLine = line;
            }
            String lastLine = previousLine;
            
            System.out.println(expectedString);
            System.out.println(lastLine);
            
            assertEquals(expectedString,lastLine);
        }
        catch (IOException ex) {        
            fail("problem while accessing file");
        }        
    }

    /**
     * Test of logJson method, of class JsonLogger.
     */
    @org.junit.Test
    public void testLogJsonString() {
        String message = "{\"type\":\"asdf\",\"severity\":1,\"priority\":2}";
        try {
            //attaching FileAppender to logger
            FileAppender appender = new FileAppender(new PatternLayout("%m%n"), filePath);
            testingLogger.addAppender(appender);
            testingLogger.logJson(message);        
            
            //browsing the log file to till we get last line, what should be our record
            FileInputStream fileIS = new FileInputStream(filePath);
            InputStreamReader isReader = new InputStreamReader(fileIS);
            BufferedReader br = new BufferedReader(isReader);
            
            String line = null;
            String previousLine = null;            
            while((line=br.readLine())!=null){
                previousLine = line;
            }
            String lastLine = previousLine;
                    
            assertEquals( message, lastLine);        
        } catch (IOException ex) {
            fail("problem while accessing file");
        }
    }
   
    @org.junit.Test
    public void testLogJsonStringWrongParam(){
        String wrongJson = "{\"severity\":6}";
        try{
            testingLogger.logJson(wrongJson);
            fail("illegalargumentexception wasn't thrown");
        }catch(IllegalArgumentException e){
            //good
        }
    }

}
