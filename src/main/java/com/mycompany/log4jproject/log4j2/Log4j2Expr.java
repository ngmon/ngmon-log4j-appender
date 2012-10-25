/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.log4jproject.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Stefan
 */
public class Log4j2Expr {
    public static void main(String[] args) {

     
        Logger l = LogManager.getLogger("L");
        l.fatal("interesting message");
    }
}
