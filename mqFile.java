import com.ibm.mq.*;            // Include the MQ package   
import com.ibm.mq.MQException;
import java.awt.AWTException;
//import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerard Doets      
    verzoek Joost 16 juni2009  , sommige logging verwijders
    
 
 */
public class mqFile {

    private static File mqBaseDir;
    private static File mqBaseOutboundDir;
    private static File mqDataDir;
    private static File mqLogDir;
    private static File mqArchiveDir;
    private static FileWriter fwlog = null;
    private mqBaanMess mqBM;
//    private static File tDir;
    private String mqQueueManager,  mqQueueNaam;
	private Integer  mqRobot;
    private static int done;

//	public File fwlogFile;
    public static void main(String args[]) {
        mqFile gg = new mqFile();
        gg.work(args);
    }

    private void work(String[] args) {



        done = 0;


        try {
            if (args.length != 4) {
                System.out.println(" first param should be the quemanager");
                System.out.println(" second param should be the queue name ");
                System.out.println(" third param should be base directory ");
                System.out.println(" forth  param should be the pause between file checks in milly seconds");
                return;
            }

            mqBaseDir = new File(args[2]);
            if (!mqBaseDir.isDirectory()) {
                System.out.println(" No base directory found, quiting");
                return;
            }

	        mqBaseOutboundDir = new File(mqBaseDir, "outbound");
	        if (!mqBaseOutboundDir.isDirectory()) {
	            System.out.print(" No outbound directory found, quiting");
	            return;
	        }

            mqLogDir = new File(mqBaseOutboundDir, "log");
            if (!mqLogDir.isDirectory()) {
                System.out.println(" No log directory found, quiting");
                return;
            }
            mqDataDir = new File(mqBaseOutboundDir, "data");
            if (!mqDataDir.isDirectory()) {
                write2log(" No data directory found, quiting");
                return;
            }

            mqArchiveDir = new File(mqBaseOutboundDir, "archive");
            if (!mqArchiveDir.isDirectory()) {
                write2log(" No archive  directory found, quiting");
                return;
            }
            mqRobot = Integer.valueOf(args[3]);
//             mqRobot = new ong(args[3]);
/*            if (mqRobot < 10 ) {
                write2log(" Wrong value for pause between file checks in milly seconds");
                return;
            }
            if (mqRobot > 59999) {
                write2log(" Value to large for pause between file checks in milly seconds");
                return;
            }
*/

//            mqBaanMess   mqBM = new mqBaanMess(args[0],args[1]);
            setmqQueueManager(args[0]);
            setmqQueueNaam(args[1]);
            mqBM = new mqBaanMess();
//            System.out.println("manager " +  mqQueueManager);
//            System.out.println("queueNaam " + mqQueueNaam);

            mqBM.init(mqQueueManager, mqQueueNaam,mqLogDir);




        while (done == 0) {


            Vector flCandidate = getFiles(mqDataDir, ".baan.ready");
            int targetMax = 0;
            targetMax = flCandidate.size();
//            System.out.println("Processing " + targetMax + " files");
            for (int c = targetMax - 1; c >= 0; c--) {
                File fc = (File) flCandidate.get(c);
                if (fc.isFile()) {
                    String dataFileName = fc.getName().substring(0, fc.getName().length() - 11);
                    Vector baanDataFiles = getBaanFiles(mqDataDir, dataFileName);
                    int nrBaanDataFiles = 0;
                    nrBaanDataFiles = baanDataFiles.size();
                    for (int d = nrBaanDataFiles - 1; d >= 0; d--) {
                        File fd = (File) baanDataFiles.get(d);
                        write2log(fd.getName());
                        if (fd.isFile()) {
                        	if (!fd.getName().endsWith(".baan.ready")) {
//  		                        write2log("Sending to queue:"+ fd.getName());

	                            mqBM.mqSendFile(fd);
									 }
//                                sendFileToQ(fd);
                            moveFileToArchive(fd);
                            write2log("done processing  : " + dataFileName);

                        }
                    }
                }
            }



//            Robot r = null;
//            try {
//                fwlog.close();
//                r = new Robot();
//            } catch (IOException ex) {
//                Logger.getLogger(mqFile.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (AWTException ex) {
//                Logger.getLogger(mqFile.class.getName()).log(Level.SEVERE, null, ex);
//            }
// verzoek Joost 16 juni2009            write2log("execution suspended for "+ mqRobot + " milli seconds");
				Wait.manySec(mqRobot.longValue());

//             r.delay(mqRobot);
//            r.delay(4000);
        }
        write2log("Excecution endded");
//                } catch (IOException e) {
            //Logger.getLogger(mqFile.class.getName()).log(Level.SEVERE, null, e);
        } catch (MQException ex) {
            write2log("MQ exception: CC = " + ex.completionCode + " RC = " + ex.reasonCode);
        }


    }

     Vector getFiles(File f, String ex) {
        Vector outFiles = new Vector();
        File[] files = f.listFiles();
        for (int k = 0; k < files.length; k++) {
            File file = files[k];
            if (file.isFile()) {
                if (file.getName().endsWith(ex)) {
                    outFiles.add(file);
                }
                if (file.getName().startsWith("stop")) {
                    done = 1;
                }

            }

        }
        return outFiles;
    }

     Vector getBaanFiles(File f, String prefix) {
        Vector outFiles = new Vector();
        File[] files = f.listFiles();
        for (int k = 0; k < files.length; k++) {
            File file = files[k];
            if (file.isFile()) {
                if (file.getName().startsWith(prefix)) {
                    outFiles.add(file);
                }
            }
        }
        return outFiles;
    }

    private  void moveFileToArchive(File fd) {
    	String newName = fd.getName() + "-"+getDateTime();
        if (fd.renameTo(new File(mqArchiveDir, newName))) {
//        if (fd.renameTo(new File(mqArchiveDir, fd.getName()))) {
            write2log("Succesfull moved file to archive file : " + newName);
        } else {
       	
            write2log("Error moving file to archive file : " + fd.getName());
        }
    }

    private  void sendFileToQ(File fa) throws FileNotFoundException {
//        System.out.println(fa.getAbsolutePath());
        try {
            FileReader fr = new FileReader(fa);
            BufferedReader frb = new BufferedReader(fr);
            String record = frb.readLine();
            while (record != null) {
//                System.out.println(record);
                record = frb.readLine();
            }
            write2log("Send to queue, file : " + fa.getName());
//            System.out.println("done, thank you ");
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(mqFile.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

     void write2log(String msg) {
        try {
				File fwlogFile = new File(mqLogDir, "Log" + getDate());
            FileWriter fwlog = new FileWriter(fwlogFile, true);

            fwlog.write(getDateTime() + " " + msg);
            fwlog.write("\n");
            fwlog.flush();
            fwlog.close();
            
        } catch (IOException ex) {
            Logger.getLogger(mqFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
 private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }   
    

    private void setmqQueueManager(String erin) {
        mqQueueManager = erin;
    }

    private void setmqQueueNaam(String erin) {
        mqQueueNaam = erin;
    }
}

