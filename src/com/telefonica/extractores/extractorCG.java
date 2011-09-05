/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.telefonica.extractores;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NCN00973
 */
public class extractorCG {
    static String comando1;
    static String comando2;
    static String comando3;
    static String comando4;
    static String comando5;
    static String comando6;
    static String comando7;
    //variables de conexion
    private static String user; //= "operpana";//"opercsi";
    private static String host; //= "10.225.171.144";//"90.104.35.148";
    private static String password; //= "panapana06";//"fullcdr23";
    private static Channel channel;
    private static PipedOutputStream pos;
    //variables de creacion de archivos
    private static FileWriter logFile;
    private static FileWriter ErrorFile;

    public static void main (String []args){
        try {
            logFile = new FileWriter("C:\\StatisticsSMSC.log", true);
            ErrorFile = new FileWriter("C:\\Error.log", true);
            try {
                GPRS01();
            } catch (JSchException ex) {
                Logger.getLogger(extractorCG.class.getName()).log(Level.SEVERE, null, ex);
                ErrorFile.append("Error al conectarse al servidor " + ex);

            }
            logFile.close();
        } catch (IOException ex) {
            Logger.getLogger(extractorCG.class.getName()).log(Level.SEVERE, null, ex);
        }

        

    }
      public static void GPRS01() throws JSchException, IOException {
        user = "medpawen";
        host = "10.225.w129.60";
        password = "medpewan";

        comando1 = "cd ";//"cd  /opt/ifwca/pan/Marcel/ConciIncollet ";
        comando2 = "";
        comando3 = "";
        comando4 = "ls -l";
        comando5 = "exit";
        //comando4 ="ls -l  | grep "+'"'+mes2+'"'+" | awk '$7 >= 1 && $7 <= "+fin+" {print $9}'>>lista.txt";
        //comando5 = "exit";
        JSch jsch = new JSch();

        final Session session = jsch.getSession(user, host, 22);

        UserInfo ui = new MyUserInfo(password);
        session.setUserInfo(ui);
        session.connect();

        channel = session.openChannel("shell");
        pos = new PipedOutputStream();

        channel.setInputStream(new PipedInputStream(pos));
        new Thread(new Runnable() {

            final PrintWriter stdin = new PrintWriter(new OutputStreamWriter(pos, "utf-8"));


            @Override
            public void run() {
                //se le envia los comandos al servidor para que este los ejecute
                //stdin.println(primero);
                stdin.println(comando1);
                stdin.println(comando2);
                stdin.println(comando3);
                stdin.println(comando4);
                stdin.println(comando5);
                stdin.close();
            }
        }).start();

        final PipedInputStream pis = new PipedInputStream();
        channel.setOutputStream(new PipedOutputStream(pis));
        new Thread(new Runnable() {

            private final BufferedReader br = new BufferedReader(new InputStreamReader(pis, "utf-8"));
            @Override

            public void run() {
                try {
                    for (String line = null; (line = br.readLine()) != null;) {
                        //System.out.println("" + line);
                        logFile.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    session.disconnect();
                    try {
                        logFile.close();
                    } catch (IOException ex) {
                        Logger.getLogger(extractorCG.class.getName()).log(Level.SEVERE, null, ex);
                        try {
                            ErrorFile.append("Error al cerrar el archivo " + ex);
                        } catch (IOException ex1) {
                            Logger.getLogger(extractorCG.class.getName()).log(Level.SEVERE, null, ex1);
                       System.out.println("Error de archivo " + ex1);
                        }
                    }
                    final int exitStatus = channel.getExitStatus();
                    System.out.println("Estado de la salida : " + exitStatus);
                }
            }
        }).start();

        channel.connect(3 * 1000);
    }

        public boolean isConnected() {
        return (channel != null && channel.isConnected());
    }

    public static class MyUserInfo implements UserInfo//, UIKeyboardInteractive
    {

        MyUserInfo(String password) {
            this.password = password;
        }
        private final String password;

        public String getPassword() {
            return password;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return false;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {
        }
    }
}
