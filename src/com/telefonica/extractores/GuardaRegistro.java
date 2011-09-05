/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.telefonica.extractores;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NCN00973
 */
public class GuardaRegistro {
    final StringBuffer valores = new StringBuffer();
     private String mensaje;
    //Constructor
    public GuardaRegistro(){

    }

    public void des(String valor)
    {
        try {
            FileWriter logFile = new FileWriter("C:\\StatisticsSMSC.log", true);
        } catch (IOException ex) {
            Logger.getLogger(GuardaRegistro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
