/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
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

    private class UnsupportedObject {

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
    private static Log4JSON log4JSON;
    private static String filePath = "src\\test\\java\\com\\mycompany\\log4jproject\\testingOutput.txt";
    private static Layout layout;
    private static FileAppender appender;

    @BeforeClass
    public static void setUpClass() {
        layout = new HadoopLog4Json();
        try {
            appender = new FileAppender(layout, filePath);
        } catch (IOException ex) {
            System.out.println("problem while initialization");
        }

        log4JSON = new Log4JSONImpl("l", appender);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testLogWithNonexistingLevel() {
        try {
            log4JSON.log("type", null, new HashMap<String, Object>());
            fail("exception wasn't thrown");
        } catch (IllegalArgumentException e) {
            //correct;
        }
    }

    @Test
    public void testLogWithUnsupportedDataTypeInPayload() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("strangeParam", new UnsupportedObject("abc", 15));
        try {
            log4JSON.log("type", null, map);
            fail("exception wasn't thrown");
        } catch (IllegalArgumentException e) {
            //correct
        }
    }

    @Test
    public void testLogCorrectParams() {

        String type = "fast";
        Level level = Level.DEBUG;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("param1", "xyz");
        map.put("param2", new Integer(12));

        log4JSON.log(type, Level.DEBUG, map);
        
        String expectedStringBegin = "\\{\"Event\":\\{\"level\":\"DEBUG\",\"type\":\"fast\",\"occurenceTime\":\"";
        String expectedStringEnd =  "\",\"application\":\"app\",\"process\":\"proc\",\"processId\":\"procId\",\"payload\":\\[\\{\"param1\":\"xyz\"\\},\\{\"param2\":12\\}\\]\\}\\}";
        
        String expectedString = expectedStringBegin + ".*" + expectedStringEnd;
        
        String lastLine = getLastLineFrom(filePath);
        Pattern p = Pattern.compile(expectedString);
        Matcher m = p.matcher(lastLine);
        boolean matches = m.matches();
        
        

        
        
        System.out.println(expectedString);
        System.out.println(lastLine);
        
        
        assertTrue(matches);

    }
   
    

    private String getLastLineFrom(String filepath) {

        FileReader fileRead = null;
        BufferedReader buReader = null;
        File rFile = null;
        String lastLine = null;

        try {
            rFile = new File(filePath);
            fileRead = new FileReader(rFile);
            buReader = new BufferedReader(fileRead);
            String line = null;
            String previousLine = null;
            while ((line = buReader.readLine()) != null) {
                previousLine = line;
            }
            lastLine = previousLine;
        } catch (IOException e) {
            System.out.println("Problem while initializing readers or while reading file");
        } finally {
            try {
                fileRead.close();
                buReader.close();
            } catch (IOException ioe) {
                System.out.println("Problem while closing readers");
            }
        }
        return lastLine;
    }
}
