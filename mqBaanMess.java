import com.ibm.mq.*;            // Include the MQ package
import java.io.*;
import java.lang.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
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


public class mqBaanMess {

    private MQQueueManager qMgr;
    private File outputFile;
    private FileWriter out;
    private File mqLogDir;
    private MQQueue myQueue;

    public void init(String qmanager, String queue, File flog) throws com.ibm.mq.MQException {
        qMgr = new MQQueueManager(qmanager);
//        int openOptions = MQC.MQOO_INPUT_EXCLUSIVE | MQC.MQOO_OUTPUT;
        int openOptions =  MQC.MQOO_OUTPUT;
        myQueue = qMgr.accessQueue(queue, openOptions, null, null, null);
			mqLogDir = flog;
			write2log("Init completed");
    }

    public void mqSendFile(File sendFile) {
        String records = "";
        String record  = "";
        String recordHead  = "";
        try {
	         MQMessage myMessage = new MQMessage();

//            System.out.println("Voor vullen message");
            myMessage.clearMessage();


//            try {
                FileReader fr = new FileReader(sendFile);
                BufferedReader frb = new BufferedReader(fr);
//                String record = frb.readLine();
					
                record = "";
                record = frb.readLine();
                recordHead = record;
 					 write2log("Trying processing  Message :<"+ recordHead +"> file succesfully read");
                while (record != null) {
//                    System.out.println("Voor schrijven  message");
                    records = records + record;
                    record = frb.readLine();
                }
//                System.out.println(records);
//                    write2log("Klaar met schrijven naar message");

                fr.close();
//            } catch (IOException ex) {
//                Logger.getLogger(mqBaanMess.class.getName()).log(Level.SEVERE, null, ex);
//            }

//            System.out.println("voor de getLength");
//          	String msg = myMessage.readString(myMessage.getMessageLength());
//            System.out.println("message: " + msg);
              myMessage.writeString(records);

//            System.out.println("Voor de put");
// methode bestaat niet bok klm            myQueue.putReportMessage(myMessage);
            myQueue.put(myMessage);
				write2log("Message :<"+ recordHead +"> on the queue");
//            System.out.println("Na  putReportMessage");


        } catch (MQException ex) {
				write2log("MQ exception: CC = " + ex.completionCode + " RC = " + ex.reasonCode);

				try {
	        	
	            write2log("Error IO exception in mqBaanMess while processing message  :roling back the queue, closing queue ");
	            qMgr.backout();   // Fout in IO, roling back the que
	            qMgr.commit();
	            myQueue.close();
	            qMgr.disconnect();
	        } catch (MQException ex1) {
	            write2log("MQ again exception in roling back and committing queue" );
	        }
        } catch (IOException e) {
            write2log("File IO exception:");
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

    
    
}
