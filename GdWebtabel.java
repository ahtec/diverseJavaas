/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gdwebtabel;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author da
 */
public class GdWebtabel {

    public static int filenr;
    public static String extension = "";
    static private int aantalKolommen = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(" first  param should be extension");
            System.out.println(" second param should be nr collms");
            System.out.println(" thirt param should be the directory");

            return;
        }
        filenr = 0;
        aantalKolommen = Integer.valueOf(args[1]).intValue();

//        target  === FROM
//        canddate=== TO
        String fromDir = args[2];
        extension = args[0];
        String prefix = args[0];
        File fDir = new File(fromDir);
        if (!fDir.isDirectory()) {
            System.out.println(" first param should be the FROM directory");
            return;
        }


        Robot r;
        r = null;
        try {
            r = new Robot();
        } catch (AWTException ex) {
//            Logger.getLogger(gdRen.class.getName()).log(Level.SEVERE, null, ex);
        }
//        while (true) {
        Vector fromFilesVector = getFiles(fDir);
//            Collections.sort(fromFilesVector); 

//            String fileName = "";
//            int fromMax = 0;
//            fromMax = fromFilesVector.size();
    }

    static Vector getFiles(File f) {
        int kolompostite = 1;
        int aantalperhtml = 1;
        long grootteVanFiles = 0 ;
        int filenummer = 1;
        File webtabel;
        String returnhref;
        Vector outFiles = new Vector();
        FileWriter wr = null;
        int fileteller = 1;
        String naamwebfile;
        String vorige = null;
        String volgende = null;
        try {
//            System.out.println("<table border = '1' width='100%' > <tr> ");
            naamwebfile = "awebtabel" + String.format("%03d", fileteller++) + ".html";

            webtabel = new File(f, naamwebfile);

            wr = new FileWriter(webtabel);
            vorige = "<td><a href=" + webtabel.getName() + "><div><button type='button'>vorige</div></a></td>" + "<table border = '1' width='100%' > <tr> ";
//            volgende = "<td><a href=" + f.getParent() + "><div>terug</div></a></td>" + "<table border = '1' width='100%' > <tr> ";
//            wr.write(vorige);
            wr.write("<td><a href=");
            wr.write(f.getParent());
            wr.write("><div>terug</div></a></td>");
            wr.write("<table border = '1' width='100%' > <tr> ");
            wr.write("/n");


            //        Vector files = f.listFiles();
            File[] files = f.listFiles();
            Arrays.sort(files);
            for (int k = 0; k < files.length; k++) {
                File file = files[k];
                if (file.isFile()) {
                    //                if (file.getName ().toLowerCase ().endsWith (".jpg")){
                    if (file.getName().toLowerCase().endsWith(extension)) {
                        //                    System.out.print(k);
                        //                    if ((k + 1) % aantalKolommen == 0) {
                        //
                        //                        System.out.println("<tr>");
                        //                    }
                        grootteVanFiles = grootteVanFiles + file.length();
//                        System.out.println(grootteVanFiles);
//                        39962985
//                        if (aantalperhtml++ > 9921) {
//                            aantalperhtml = 1;
                        if (grootteVanFiles > 39962985 ) {
                            grootteVanFiles = file.length() ;
                            wr.write("</tr></table>");
                            wr.write(vorige);
//                            vorige = "<td><a href=" + webtabel.getName() + "><div><button type='button' width='100%' >vorige</div>  </a></td>" ;
                            vorige = "<a href=" + webtabel.getName() + "><div><button type='button' width='100%' >vorige</div>  </a>";

//                            returnhref = "<td><a href=" + webtabel.getName() + "><div><button type='button'>terug</div></a></td>" + "<table border = '1' width='100%' > <tr> ";
                            naamwebfile = "awebtabel" + String.format("%03d", fileteller++) + ".html";
                            webtabel = new File(f, naamwebfile);
                            volgende = "<td><a href=" + webtabel.getName() + "><div><button type='button'>volgende</div></a></td>";
                            wr.write(volgende);
                            wr.close();
                            wr = new FileWriter(webtabel);
                            wr.write(vorige);
                            wr.write("<table border = '1' width='100%' > <tr> ");

//                            wr.write(returnhref);
                            kolompostite = 1;
                        }

//                        wr.write("<td><img src='" + file.getName() + "' width=100%></td>");
            wr.write("\n");
                        wr.write("<td> <a href=" + file.getName() + "><img src='" + file.getName()+ "'TITLE='"+file.getAbsolutePath() + "' width=100%> </a>   </td>");
//                        System.out.println("<td><img src='" + file.getName() + "' width=100%></td>");
                                        wr.write("\n");
                                        wr.write("<td> <a>  gevaarlijke <br>situatie    </a>   </td>");
                        if (kolompostite >= aantalKolommen) {
                            wr.write("</tr>");
//                                        wr.write("\n");
                            wr.write("<tr>");

//                            System.out.println("</tr><tr>");
                            kolompostite = 0;
                        }
                        kolompostite++;
                        //                                System.out.println("<td><img src='"+ fb.getAbsolutePath() + "'width=100%></td></tr>");

                        //                    outFiles.add(file);
                    }
                } else {
                    if (file.isDirectory() & !file.isHidden()) {
                        //					System.out.println(file.getAbsolutePath());
//                        outFiles.add(file);
                        Vector dirFiles = getFiles(file);
                    }
                }
            }
//            System.out.println("</tr></table>");
            wr.write("</tr></table>");
            wr.write(vorige);


        } catch (IOException ex) {
            Logger.getLogger(GdWebtabel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                wr.close();
            } catch (IOException ex) {
                Logger.getLogger(GdWebtabel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return outFiles;
    }
}
