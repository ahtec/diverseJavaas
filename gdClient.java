
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class gdClient {

    public static void main(String args[]) {
        try {
            Socket s1 = new Socket(args[0], 5432);
//            Socket sFileName1 = new Socket("10.0.2.193", 5432);
            
//            Socket s1 = new Socket("10.0.2.193", 5432);
//            Socket s1 = new Socket("10.0.2.193", 80);
            int in = 0;
            InputStream is = s1.getInputStream();
//            FileOutputStream fos = new FileOutputStream(new File(args[1]));
            String naamFile = new String();
            String eonMarker="";

            int arrayInt[] = new int[1024];
            int i = 0;
//            while ((in = is.read()) != -1) {
//                arrayInt[i++] = in;
//            }
            for (i = 0; i < arrayInt.length; i++) {
                in = is.read();
                arrayInt[i] = in;
//                System.out.println(in);
                char c =  (char)in;
//                System.out.println(c);
                naamFile = naamFile + c;
                eonMarker = naamFile;
                if (eonMarker.endsWith("XeindeFileNaamX")){
                    naamFile = naamFile.substring(0, naamFile.length() - 15);
                    i = arrayInt.length;
                }
//                naamFile = naamFile.concat(naamFile) + new String(intToByteArray(arrayInt[i]));
            }
            
//            System.out.println(naamFile);
            naamFile = naamFile.trim();
            System.out.println(" "+naamFile);
//            File outFile = new File("/home/gerard/NetBeansProjects/networking/build/classes", naamFile);
            File outFile = new File(naamFile);
//            File outFile = new File("doel");

            
//            File outFile = new File("naamFile");
//            FileOutputStream fos = new FileOutputStream(new File(naamFile));
            FileOutputStream fos = new FileOutputStream(outFile);
            while ((in = is.read()) != -1) {
                fos.write(in);
            }
            
            fos.close();
//            outFile.renameTo(new File(naamFile));
            is.close();
            s1.close();
            System.out.println(" done");

        } catch (IOException ex) {
            Logger.getLogger(gdServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
    
/*    public static byte[] intToByteArray(int value) {
//        byte[] b = new byte[4];
        byte[] b = new byte[2];
//        for (int i = 0; i < 4; i++) {
        for (int i = 0; i < 2; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
 */ 
}
