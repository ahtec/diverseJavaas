/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timemachine;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author tzxj7b
 */
public class BackUpFiledb {
//    public String selectedDir;
    public String canonicalPath;
    public long lastModified;
    public String backUpLocation;
    public File file;
//    public public File f = new File();
//    f.
    public BackUpFiledb(File f) {
        super();
        try {
//            selectedDir    = "";
            canonicalPath = f.getCanonicalPath();
            lastModified = f.lastModified();
            backUpLocation = "";
            file = f;
        } catch (java.io.IOException e) {
            System.out.println("io Exception in creating backUpFile object " + e);
        }
    }

    @Override
    public String toString(){
        return canonicalPath  + finals.RECORD_SEPERATOR+ lastModified +finals.RECORD_SEPERATOR+backUpLocation+finals.RECORD_SEPERATOR+"\n";
    }
    
    public BackUpFiledb(String record) {
        super();
        StringTokenizer TOK = new StringTokenizer(record, finals.RECORD_SEPERATOR);
        try {
            if (record != null) {
                canonicalPath  = TOK.nextToken();
                file = new File(canonicalPath);
                lastModified   = Long.parseLong(TOK.nextToken());
                backUpLocation = TOK.nextToken();
            }
        } catch (java.lang.NumberFormatException ne) {
            lastModified = 0;
            backUpLocation = TOK.nextToken();
        }catch (java.util.NoSuchElementException nse) {
            backUpLocation = "";
        }
    }
    
    public boolean needBackup(long toCheck){
        Date dateBackUp = new Date(this.lastModified);
        boolean eruit = false;
        if (dateBackUp.before(new Date(toCheck)))
            eruit = true;
        
        return eruit;
    }
}











































