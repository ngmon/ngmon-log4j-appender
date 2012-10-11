package com.mycompany.log4jproject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        BasicConfigurator.configure();
        
        String filePath = "C:\\Users\\Stefan\\Desktop\\School Projects\\Bachelor thesis\\Log4jProject\\src\\main\\java\\com\\mycompany\\log4jproject\\newfile.txt";
        
        JsonLogger l = new JsonLogger("myLogger");
        
        PatternLayout simplifiedLayout = new PatternLayout("%m%n");
        
        ConsoleAppender appender = new ConsoleAppender(simplifiedLayout);
        FileAppender fileAppender;
        try {
            fileAppender = new FileAppender(simplifiedLayout, filePath);
            l.addAppender(fileAppender);
            l.logJson("xyak",7,11);
        } catch (IOException ex) {
            System.out.println("some problem");
        }

        
        FileInputStream fileIS;
        try {
            fileIS = new FileInputStream(filePath);
            InputStreamReader isReader = new InputStreamReader(fileIS);
            BufferedReader br = new BufferedReader(isReader);
            
            String line = null;
            String previousLine = null;            
            while((line=br.readLine())!=null){
                previousLine = line;
            }
            String lastLine = previousLine;
            
            
            
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 
       
        
    }
}
