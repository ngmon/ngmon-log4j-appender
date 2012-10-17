package com.mycompany.log4jproject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();

        String filePath = "C:\\Users\\Stefan\\Desktop\\School Projects\\Bachelor thesis\\Log4jProject\\src\\main\\java\\com\\mycompany\\log4jproject\\newfile.txt";

        JsonLogger l = new JsonLogger("myLogger");
        
        JsonLayout jsonLayout = new JsonLayout();
        
        FileAppender fileAppender = new FileAppender(jsonLayout, filePath);
        
        l.addAppender(fileAppender);
        
        l.logJson("specialType",5,6);
       
  
    }
}
