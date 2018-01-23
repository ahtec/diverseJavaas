/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timemachine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static Vector listBachUpObjects;
    static int nrListBachUpObjects;
    static Vector dirs;
    static int nrCurrentInDirs;


//    FileReader a = null;
    public static void main(String[] args) {
//        verwerkFiles(getDirectory(new File("m")));

        /*
         * lees lijst met directories
         * while er nog 1 directory is
         *      vul array met BackUpFile's
         *      verwerkDirectory
         * 
         * 
         */
//System.out.println("na main f :" + args[0]);  
        if (readConf(args[0])) {


            File fo = new File(finals.DIR, finals.NEWFILEDATA);
            try {
                File renameToThisFile = File.createTempFile("timeMachine", ".prev", new File(finals.DIR));
                move(fo, renameToThisFile);
            } catch (java.io.IOException e) {
//                System.out.println("io Exception in opening " + finals.DIRDATA + " " + e);
            }

            // create backup directory
            File bu = new File(finals.BACKUPDIR, finals.STRINGDATE);
            if (!bu.mkdir()) {
                System.out.println("io Exception in creating bachUp directory " + finals.BACKUPDIR + " " + finals.STRINGDATE + " ");
            }

            // start process now
            dirs = new Vector();
            try {
                dirs = getDirectories();
                for (int i = 0; i < dirs.size(); i++) {
                    nrCurrentInDirs = i;
                    verwerkDirectory((String) dirs.elementAt(i));
                }
                // deal with old file data
                File fi = new File(finals.DIR, finals.FILEDATA);
                fi.delete();
                copy(new File(finals.DIR, finals.NEWFILEDATA), new File(finals.DIR, finals.FILEDATA));

            } catch (java.io.IOException e) {
                System.out.println("io Exception in opening " + finals.DIRDATA + " " + e);
            }
        }

    }

    static Vector getDirectories() throws IOException {
        // reads the file containing the list of directories witch shoul be backed up
        // returns an vacot of string elements, each containing the directory
        Vector eruit = new Vector();
        String line;

        FileReader aa = new FileReader(new File(finals.DIR, finals.DIRDATA));
        BufferedReader tmp = new BufferedReader(aa);
        while ((line = tmp.readLine()) != null) {
            eruit.add(line);
        }

        tmp.close();
        return eruit;
    }

    static void verwerkDirectory(String dirCanonical) {
        File d = new File(dirCanonical);
        if (d.isDirectory()) {
            System.out.println("working on  " + dirCanonical);
            listBachUpObjects = readBackUpFileList(dirCanonical);
            processBackUpDirectory(d);
            writeListBackUpObjectsToSeqFile();
        } else {
            System.out.println("SKIPPED " + dirCanonical);
        }
    }

    static Vector processBackUpDirectory(File f) {
        Vector outFiles = new Vector();
        File[] files = f.listFiles();
        for (int k = 0; k <
                files.length; k++) {
            File file = files[k];
            if (file.isFile()) {
                verwerkFile(file);
            } else {
                if (file.isDirectory() & !file.isHidden()) {
                    Vector dirFiles = processBackUpDirectory(file);
                    for (int i = 0; i <
                            dirFiles.size(); i++) {
                        outFiles.add(dirFiles.elementAt(i));
                    }
                }
            }
        }
        return outFiles;
    }

    static void verwerkFile(File f) {
        /* 
         * lees file
         * zoek op in db
         *   is file reeds werwerkt?
         *      zo ja sla over
         *      zo nee
         *          maak bachUp
         *          store file prop in db
         * 
         */

        int i = getFileData(f);
        BackUpFiledb bufdb;
        if (i >= 0) {
            bufdb = (BackUpFiledb) listBachUpObjects.elementAt(getFileData(f));
            if (bufdb.needBackup(f.lastModified())) {
                if (doBachUpFile(bufdb)) {
                    updateListBackUpObjects(bufdb);
                }
            }
        } else {
            // so no no backUp data is known, than we add this object to the array
            bufdb = new BackUpFiledb(f);
            listBachUpObjects.add(bufdb);
            nrListBachUpObjects = listBachUpObjects.size() - 1;
            if (doBachUpFile(bufdb)) {
                updateListBackUpObjects(bufdb);
            }
        }
    }

    static int getFileData(File f) {
        // zoekt de BackUp object op uit de vector listBachUpObjects
        // returns the BackUpFile object from the vector
//        BackUpFiledb bufdb = null;
        int eruit = -1;
        nrListBachUpObjects = -1;
        try {
            String fileCanonicalPath = f.getCanonicalPath();
            for (int i = 0; i < listBachUpObjects.size(); i++) {
                BackUpFiledb listElementBufdb = (BackUpFiledb) listBachUpObjects.elementAt(i);
                if (listElementBufdb.canonicalPath.compareTo(fileCanonicalPath) == 0) {
                    nrListBachUpObjects = i;
                    eruit = i;
                    break;
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("io Exception in getting canonical path from file while attempting to compare it with the saved data" + e);
            System.out.println("This file wil be skiped");

        }
        return eruit;
    }

    private static File createBackUpCanonicalPath(BackUpFiledb bufdb) throws IOException {
//        throw new UnsupportedOperationException("Not yet implemented");
        String backUpCanonicalPath = "", temp;
        File targetDir = null;
        bufdb.file.getParent();
        temp = bufdb.file.getParent();
        if (temp.contains(":")) {
            temp = temp.substring(temp.indexOf(":") + 1);
        }

        backUpCanonicalPath = finals.BACKUPDIR + File.separatorChar + finals.STRINGDATE + temp;
        targetDir = new File(backUpCanonicalPath);
        if (!targetDir.mkdirs()) {
//        if (true) {
            backUpCanonicalPath = finals.BACKUPDIR + File.separatorChar + finals.STRINGDATE + File.separatorChar;
            StringTokenizer TOK = new StringTokenizer(temp, File.separator);
            while (TOK.hasMoreTokens()) {
                backUpCanonicalPath = backUpCanonicalPath + TOK.nextToken() + File.separatorChar;
                targetDir = new File(backUpCanonicalPath);
                if (!targetDir.exists()) {
                    if (!targetDir.mkdir()) {
                        throw new IOException("Cant create subdirectory " + backUpCanonicalPath);
                    }
                }
            }
        }
        return targetDir;
    }

    static private boolean doBachUpFile(BackUpFiledb bufdb) {
        boolean eruit = false;
//         try {
        try {
            copy(bufdb.file, new File(createBackUpCanonicalPath(bufdb), bufdb.file.getName()));
            bufdb.lastModified = finals.CURRENTTIME;
            eruit =
                    true;
//            return true;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        //      }
        return eruit;
    }

    private static boolean readConf(String commandLineDirectory) {
//        throw new UnsupportedOperationException("Not yet implemented");
//System.out.println("In readConf :" + commandLineDirectory);        
        boolean eruit = false;
//        Vector lines = new Vector();
        StringTokenizer TOK;

        FileReader fr;

        String temp,
                line;
        try {
            fr = new FileReader(new File(commandLineDirectory, finals.CONF_FILE));
            BufferedReader tmp = new BufferedReader(fr);
            while ((line = tmp.readLine()) != null) {
                try {
                    TOK = new StringTokenizer(line, finals.CONF_FIELD_SEPERATOR);
                    temp =
                            TOK.nextToken();
                    if (temp.equalsIgnoreCase(finals.ST_BACKUPDIR)) {
                        finals.BACKUPDIR = TOK.nextToken();
                    }

                    if (temp.equalsIgnoreCase(finals.ST_DIR)) {
                        finals.DIR = TOK.nextToken();
                    }

                } catch (java.util.NoSuchElementException tokError) {
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("io Exception reading " + finals.CONF_FILE + e);

        }

        if ((finals.BACKUPDIR.equalsIgnoreCase(finals.LEEG)) || (finals.BACKUPDIR.equalsIgnoreCase(finals.LEEG))) {

        } else {
            eruit = true;
        }

        return eruit;

    }

    static private void updateListBackUpObjects(BackUpFiledb bufdb) {
        listBachUpObjects.remove(nrListBachUpObjects);
        listBachUpObjects.add(nrListBachUpObjects, bufdb);
    }

    static private void writeListBackUpObjectsToSeqFile() {

        FileWriter out;
        BackUpFiledb listElementBufdb;

        try {
            out = new FileWriter(new File(finals.DIR, finals.NEWFILEDATA), true);
            for (int i = 0; i <
                    listBachUpObjects.size(); i++) {
                listElementBufdb = (BackUpFiledb) listBachUpObjects.elementAt(i);
                out.write(listElementBufdb.toString());

            }

            out.close();

        } catch (java.io.IOException e) {
            System.out.println("io Exception in writing vector to seq file" + e);
        }

//        listBachUpObjects.remove(nrListBachUpObjects);
//        listBachUpObjects.add(nrListBachUpObjects, bufdb);
    }

    static private Vector readBackUpFileList(String targetSelectedDir) {
        Vector uit = new Vector();
        String selectedDir;

        int nrOfLInesInDB = 0;
        String line;

        StringTokenizer TOK;

        FileReader fr;

        try {
            fr = new FileReader(new File(finals.DIR, finals.FILEDATA));
            BufferedReader tmp = new BufferedReader(fr);
            while ((line = tmp.readLine()) != null) {
                nrOfLInesInDB++;
//                System.out.println(" line = " + line);
                try {
                    TOK = new StringTokenizer(line, finals.RECORD_SEPERATOR);
                    if (line != null) {
                        selectedDir = TOK.nextToken();
                        if (selectedDir.startsWith(targetSelectedDir)) {
                            uit.add(new BackUpFiledb(line));
                        }

                    }
                } catch (java.lang.NumberFormatException ne) {
                    System.out.println("NumberFormatException" + ne);
                }

            }
        } catch (IOException e) {
            System.out.println("io Exception in processing" + e);
        }

        return uit;
    }
// Move file (src) to File/directory dest.
    public static synchronized void move(File src, File dest) throws FileNotFoundException, IOException {
        copy(src, dest);
        src.delete();
    }

// Copy file (src) to File/directory dest.
    public static synchronized void copy(File src, File dest) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

// Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }
}
