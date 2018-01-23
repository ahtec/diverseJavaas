
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerard1
 */
public class gdDiff {

    private static String extension;
    public static File fromDir;
    public static File doelDir;
    public static int lengteDoelDir,  lengteFromDir;

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println(" no name specified");
            return;
        }
        fromDir = new File(args[0]);
        doelDir = new File(args[1]);
        lengteFromDir = (int) args[0].length();
        lengteDoelDir = (int) args[1].length();


        run();


    }

    public static int run() {
        int eruit = 0;
//        Vector fla = getFiles(f);
//        Vector fla = getFiles(fromDir);
        List fla = getFiles(fromDir);
        Collections.sort(fla, Collections.reverseOrder());
//        List list = new Vector();
//        fla = fla.s
        int bMax = 0;
        bMax = fla.size();
//        System.out.println("got the files, now I start comparing : " + bMax + " files");

        for (int a = bMax - 1; a >= 0; a--) {
            try {
//            System.out.println(a);
                File fa = (File) fla.get(a);
                String doelFilNaam = doelDir.getCanonicalFile() + fa.getCanonicalPath().substring(lengteFromDir);

                File testIfBestaat = new File(doelFilNaam);
                if (!testIfBestaat.exists()) {
                    if (fa.isFile()) {
//                    System.out.println("cp '" + fa.getCanonicalPath() + "'  '" + doelDir.getCanonicalFile() + fa.getCanonicalPath().substring(lengteFromDir) + "'");
                        if (doelFilNaam.contains("'")) {
                            System.out.println("cp \"" + fa.getCanonicalPath() + "\"  \"" + doelFilNaam + "\"");

                        } else {
                            System.out.println("cp '" + fa.getCanonicalPath() + "'  '" + doelFilNaam + "'");
                        }
                    } else {

                        System.out.println("mkdir \"" + doelDir.getCanonicalFile() + fa.getCanonicalPath().substring(lengteFromDir) + "\"");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(gdDiff.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return eruit;
    }

    static Vector getFiles(File f) {
        Vector outFiles = new Vector();
        File[] files = f.listFiles();
        for (int k = 0; k < files.length; k++) {
            File file = files[k];
            if (file.isFile()) {
                outFiles.add(file);
            } else {
                if (file.isDirectory() & !file.isHidden()) {
                    outFiles.add(file);
                    Vector dirFiles = getFiles(file);
                    for (int i = 0; i < dirFiles.size(); i++) {
                        outFiles.add(dirFiles.elementAt(i));
                    }
                }
            }
        }
        return outFiles;
    }
}