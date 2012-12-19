/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketException;

/**
 *
 * @author Stefan
 */
public class UnixSocketAppender extends AppenderSkeleton {

    private static final int DEFAULT_RECONNECTION_DELAY = 3000;
    private int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
    private AFUNIXSocketAddress socketAddress;
    private ObjectOutputStream oos;
    private Connector connector;
    private int i = 0;

    public UnixSocketAppender(File socketFile, Layout layout) {
        if ((socketFile == null) || (layout == null)) {
            throw new IllegalArgumentException("socketFile or layout cannot be null");
        }
        try {
            this.socketAddress = new AFUNIXSocketAddress(socketFile);
            this.layout = layout;
            this.connect();
        } catch (IOException ex) {
            System.out.println("Problem while creating socket address");
        }
    }

    public int getReconnectionDelay() {
        return reconnectionDelay;
    }

    public void setReconnectionDelay(int reconnectionDelay) {
        this.reconnectionDelay = reconnectionDelay;
    }

    void connect() {
        try {
            // First, close the previous connection if any
            cleanUp();
            oos = new ObjectOutputStream(AFUNIXSocket.connectTo(socketAddress).getOutputStream());

        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            String msg = "Could not connect to file: "
                    + socketAddress.getHostName() + ".";
            if (reconnectionDelay > 0) {
                msg += " We will try again later.";
                fireConnector(); // fire the connector thread
            } else {
                msg += " We are not retrying.";
                errorHandler.error(msg, e, ErrorCode.GENERIC_FAILURE);
            }
            LogLog.error(msg);
        }
    }

    synchronized public void close() {
        if (this.closed) {
            return;
        }

        this.closed = true;
        cleanUp();
    }

    public void cleanUp() {
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("Could not close oos.", e);
            }
            oos = null;
        }
        if (connector != null) {
            //LogLog.debug("Interrupting the connector.");
            connector.interrupted = true;
            connector = null;  // allow gc
        }
    }

    public void append(LoggingEvent event) {
        if (event == null) {
            return;
        }

        if (oos != null) {
            try {
                String output = this.layout.format(event);
                if (i == 0) {
                    ++i;
                    throw new IOException(" sdfa");
                }
                oos.writeObject(output);
                oos.flush();
            } catch (IOException e) {
                System.out.println("Problem while appending");
            }
        }

    }

    void fireConnector() {
        if (connector == null) {
            connector = new Connector();
            connector.setDaemon(true);
            connector.setPriority(Thread.MIN_PRIORITY);
            connector.start();
        }
    }

    public boolean requiresLayout() {
        return true;
    }

    class Connector extends Thread {

        boolean interrupted = false;

        public void run() {
            while (!interrupted) {
                try {
                    sleep(reconnectionDelay);
                    LogLog.debug("Attempting connection to " + socketAddress.getHostName());
                    synchronized (this) {
                        oos = new ObjectOutputStream(AFUNIXSocket.connectTo(socketAddress).getOutputStream());
                        connector = null;
                        LogLog.debug("Connection established. Exiting connector thread.");
                        break;
                    }
                } catch (InterruptedException e) {
                    LogLog.debug("Connector interrupted. Leaving loop.");
                    return;
                } catch (java.net.ConnectException e) {
                    LogLog.debug("Remote host " + socketAddress.getHostName()
                            + " refused connection.");
                } catch (IOException e) {
                    if (e instanceof InterruptedIOException) {
                        Thread.currentThread().interrupt();
                    }
                    LogLog.debug("Could not connect to " + socketAddress.getHostName()
                            + ". Exception is " + e);
                }
            }
        }
    }
}