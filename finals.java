/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timemachine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author tzxj7b
 */
public class finals {

//     static String DIR = "\\ariba\\testTimeMachine";
     static String DIR = "xleegx";
//     static String BACKUPDIR = "\\tmp\\backUp";
     static String BACKUPDIR = "xleegx";
    final static String FILEDATA = "timeMachineDB";
    final static String NEWFILEDATA = "timeMachineDB_New";
    final static String DIRDATA = "timeMachineDB_DIR";
    final static String LEEG = "xleegx";
    final static String RECORD_SEPERATOR = "|";
//    final static String CONF_RECORD_SEPERATOR = " ";
    final static String CONF_FIELD_SEPERATOR = ";";
    final static String CONF_FILE = "timeMachine.conf";
    final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss_SSS", Locale.getDefault());
    final static Date CURRENTDATE = new Date();
    final static long CURRENTTIME = new Date().getTime();
    final static String STRINGDATE = formatter.format(CURRENTDATE);
    final static String ST_BACKUPDIR = "BACKUPDIR";
    final static String ST_DIR = "DIR";
    
//    final static String BACHUPDIRECTORY = 
}
