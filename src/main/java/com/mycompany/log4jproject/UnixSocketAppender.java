/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketException;

/**
 *
 * @author Stefan
 */
public class UnixSocketAppender extends AppenderSkeleton {

    private PrintStream ps;
    private String host;
    private int port;
    private File socketFile = null;
    private AFUNIXSocket socket;
    private PrintWriter writer;

    public UnixSocketAppender(File socketFile,Layout layout) {
        this.layout = layout;
        try {
            this.socketFile = socketFile;
            socket = AFUNIXSocket.newInstance();
            socket.connect(new AFUNIXSocketAddress(socketFile));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (AFUNIXSocketException e) {
            System.out.println("Cannot connect to server. Have you started it?");
        } catch (IOException ex) {
            Logger.getLogger(UnixSocketAppender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void append(LoggingEvent event) {
        writer.println(this.layout.format(event));
        writer.flush();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("problems with closing");
        }
        writer.close();
    }

    public boolean requiresLayout() {
        return true;
    }
}