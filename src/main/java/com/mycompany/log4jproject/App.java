package com.mycompany.log4jproject;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException {
        log4j1();
    }
    
    public static void log4j1() throws IOException{
        
        BasicConfigurator.configure();

        String filePath = "C:\\Users\\Stefan\\Desktop\\School Projects\\Bachelor thesis\\Log4jProject\\src\\main\\java\\com\\mycompany\\log4jproject\\newfile.txt";
  
        Log4JSONImpl log = new Log4JSONImpl("l");
        
        NewJsonLayout layout = new NewJsonLayout();
        
        FileAppender appender = new FileAppender(layout,filePath);
        
        log.addAppender(appender);
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("xx","yy");
        map.put("zz", "aaa");
        
        log.log("this is my message",Level.DEBUG,map);
        
        
    }

 
}
