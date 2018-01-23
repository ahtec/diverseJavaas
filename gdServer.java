/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdcopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author doets
 */
public class gdServer {

    public gdServer() {

        ServerSocket s = null;
        int returnVal;
        final JFileChooser fc = new JFileChooser();
        returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                s = new ServerSocket(5432);
//                s = new ServerSocket(0);
                ip();
                System.out.println("Connection on port " + s.getLocalPort());

            } catch (IOException ex) {
                Logger.getLogger(gdServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (true) {
                try {
                    Socket s1 = s.accept();
                    System.out.println("sending " + f.getName());
                    OutputStream s1out = s1.getOutputStream();
//                    File f = new File(args[0]);
//                    System.out.print(f.getName());
                    FileInputStream fis = new FileInputStream(f);
//                    byte[] buffer1 = new byte[10];
                    byte[] buffer = new byte[1024];
                    buffer = (f.getName() + "XeindeFileNaamX").getBytes();
                    s1out.write(buffer);
                    int byteRead = 0;
                    while ((byteRead = fis.read(buffer)) != -1) {
//                        System.out.print("sending");
                        s1out.write(buffer, 0, byteRead);
                    }
//                    System.out.println("done, thank you ");
 //statusPanel statusMessageLabel.setText("Klaar met zenden");
                    s1.close();
                    break;
                } catch (IOException iOException) {
                    System.out.print("error in sending file");
                }
            }
        }
        
    }

//	public static void main(String... args)
    static void ip() //		throws Exception
    {
        
        
        try {
            InetAddress thisIp = InetAddress.getLocalHost();
            System.out.println("Your computers IP number is :" + thisIp.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }



//        try {
//            Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
//            System.out.println("your network configuration");
//
//            while (e1.hasMoreElements()) {
//                NetworkInterface ni = e1.nextElement();
//
//                System.out.print(ni.getName());
//                System.out.print(" : [");
//                Enumeration<InetAddress> e2 = ni.getInetAddresses();
//                while (e2.hasMoreElements()) {
//                    InetAddress ia = e2.nextElement();
//                    System.out.print(ia);
//                    if (e2.hasMoreElements()) {
//                        System.out.print(",");
//                    }
//                }
//                System.out.println("]");
//            }
//        } catch (SocketException ex) {
//            Logger.getLogger(gdServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}

