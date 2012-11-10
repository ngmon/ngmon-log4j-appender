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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class Log4JSONTest {

    
    private class UnsupportedObject{
        private String stringAttr;
        private int intAttr;

        public UnsupportedObject(String stringAttr, int intAttr) {
            this.stringAttr = stringAttr;
            this.intAttr = intAttr;
        }

        public String getStringAttr() {
            return stringAttr;
        }

        public void setStringAttr(String stringAttr) {
            this.stringAttr = stringAttr;
        }

        public int getIntAttr() {
            return intAttr;
        }

        public void setIntAttr(int intAttr) {
            this.intAttr = intAttr;
        }
    }
    
    private Log4JSON log4JSON;
    private String filePath = "C:\\Users\\Stefan\\Desktop\\School Projects\\Bachelor thesis\\Log4jProject\\src\\test\\java\\com\\mycompany\\log4jproject\\testingOutput.txt";

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        PatternLayout layout = new PatternLayout("%m");

        FileAppender appender = null;
        try {
            appender = new FileAppender(layout, filePath);
        } catch (IOException ex) {
            System.out.println("problem while initialization");
        }

        log4JSON = new Log4JSONImpl("l", appender);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testLogCorrectParams() {

        String type = "fast";
        Level level = Level.DEBUG;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("param1", "value1");
        map.put("param2", new Integer(12));
        
        log4JSON.log(type, Level.DEBUG, map);

        String expectedString = "{" + "\"type\":\"" + type +
                "\",\"payload\":["+
                "{\"param1\":\"value1\"},"+
                "{\"param2\":12}"+
                "]}";
        try {

            String lastLine = getLastLineFrom(filePath);   
            
            assertEquals(expectedString, lastLine);
        } catch (IOException ex) {
            fail("problem while accessing file");
        }

    }

    @Test
    public void testLogWithNonexistingLevel() {
        try{
            log4JSON.log("type",null,new HashMap<String,Object>());
            fail("exception wasn't thrown");
        }
        catch(IllegalArgumentException e){
            //correct;
        }
    }

    @Test
    public void testLogWithUnsupportedDataTypeInPayload() {
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("strangeParam", new UnsupportedObject("abc",15));
        try{
            log4JSON.log("type",null,new HashMap<String,Object>());
            fail("exception wasn't thrown");
        }catch(IllegalArgumentException e){
            //correct
        }
    }


    @Test
    public void testLogWithNullEntryInPayload() {
    }

    private String getLastLineFrom(String filepath) throws FileNotFoundException, IOException {
        FileInputStream fileIS = new FileInputStream(filePath);
        InputStreamReader isReader = new InputStreamReader(fileIS);
        BufferedReader br = new BufferedReader(isReader);

        String line = null;
        String previousLine = null;
        while ((line = br.readLine()) != null) {
            previousLine = line;
        }
        String lastLine = previousLine;
        
        return lastLine;
    }
}
