/* This program has been tested with MQSeries V5.1 and JDK 1.1.7.          */
/*                                                                         */

/***************************************************************************/
/*  leest queue , zolang er messages zijn
maakt outputfile


stelt 1 record samen, schrijft naar file
 */
/*                                                                         */
/*  mqReadMess has 2 parameters:                                           */
/*    queue manager name (optional)                                        */
///*    queue name (required)                                                */                                                                      */
///*    name outputfile                                                      */
///*                                                                         */
///*                                                                         */
// 2009-07-01 wo 1-7-2009 11:59
// 2009-07-01 Kan je een extra (niet verplichte) rubriek toevoegen aan je BWART.CONFIG?
// 2009-07-01 Als hier een waarde instaat moet die de waarde van de SAP-invoer van het
// 2009-07-01 veld E1MBXYI-GRUND overschrijven.
// 2009-07-01 Het blijkt nl. dat SAP dit niet als verplicht veld ziet, terwijl dit aan
// 2009-07-01 Baan kant wel zo is gedefinieerd./*
// 2009-08-18 Wijz 7646120 redencode verwerkt                                                                      */ 
// 2009-09-01 methode grund uitgebreid 
//   amqmsrvn.exe                                                                      */
/***************************************************************************/
import com.ibm.mq.*;            // Include the MQ package
import java.awt.AWTException;
import java.awt.Robot;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mqReadMess {

    private MQQueueManager qMgr;
    String outFileName = "baanmessages";
    private static File mqBaseDir;
    private static File mqBaseInboundDir;
    private static File mqDataDir;
    private static File mqLogDir;
    private static File mqArchiveDir;
    private static FileWriter fwlog = null;
    static private Integer mqRobot;
    public String msgtest,  budat;
    public String theSign, GRUNDdefaultWaarde;
    private static File fbwart ;

    public static void main(String args[]) throws IOException {
        if (args.length != 4) {
            System.out.println(" first  param should be the quemanager");
            System.out.println(" second param should be the queue name ");
            System.out.println(" third  param should be base directory ");
            System.out.println(" forth  param should be the name from the output file");
            return;
        }

        mqBaseDir = new File(args[2]);
        if (!mqBaseDir.isDirectory()) {
            System.out.print(" No base directory found, quiting");
            return;
        }

        fbwart   = new File( mqBaseDir, "bwart.config")  ;
        if (!fbwart.exists()) {
            System.out.print(" No bwart.config found, please add first, quiting");
            return;
        }


        mqBaseInboundDir = new File(mqBaseDir, "inbound");
        if (!mqBaseInboundDir.isDirectory()) {
            System.out.print(" No inbound directory found, quiting");
            return;
        }

        mqLogDir = new File(mqBaseInboundDir, "log");
        if (!mqLogDir.isDirectory()) {
            System.out.print(" No log directory found, quiting");
            return;
        }
        mqDataDir = new File(mqBaseInboundDir, "data");
        if (!mqDataDir.isDirectory()) {
            write2log(" No data directory found, quiting");
            return;
        }

        mqArchiveDir = new File(mqBaseInboundDir, "archive");
        if (!mqArchiveDir.isDirectory()) {
            write2log(" No archive  directory found, quiting");
            return;
        }




        mqReadMess mySample = new mqReadMess();
        mySample.start(args);


        System.exit(0);

    }

    public void start(String args[]) {
        try {
            if (args.length > 1) {
//                System.out.println("in de args 1: " + args[0]);

                qMgr = new MQQueueManager(args[0]);
//            qMgr = new MQQueueManager("QM_W2TZXJ7B01");
            }
            outFileName = args[3];
            int openOptions = MQC.MQOO_INPUT_EXCLUSIVE | MQC.MQOO_BROWSE;
//            System.out.println("voor myQueue instantiering  ");
            MQQueue myQueue = qMgr.accessQueue(args[1], openOptions, null, null, null);
//            System.out.println("voor de options ");
            MQGetMessageOptions gmo = new MQGetMessageOptions();
//         gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_BROWSE_FIRST;
            gmo.options = MQC.MQGMO_NO_WAIT | MQC.MQGMO_CONVERT | MQC.MQGMO_SYNCPOINT;
//            System.out.println("voor de bew message ");
            MQMessage myMessage = new MQMessage();

            /***************************/
            /* Set up a loop exit flag */
            /***************************/
            boolean done = false;
            long messCount = 0;
            do {
                try {
                    System.out.println("Voor de clear messages");
                    myMessage.clearMessage();
                    System.out.println("Na  de clear mssagas");
                    myMessage.correlationId = MQC.MQCI_NONE;
                    myMessage.messageId = MQC.MQMI_NONE;
                    System.out.println("Na  messageId");
                    /**************************************************/
                    /* Browse the message, display it, and ask if the */
                    /* message should actually be gotten              */
                    /**************************************************/
                    myQueue.get(myMessage, gmo);
                    System.out.println("Na  get");
                    String msg = myMessage.readString(myMessage.getMessageLength());
                    write2log("processing message: " );
                    moveStringToArchiveFile(msg);
                    verwerkMsg(msg);
                    messCount++;
                } catch (MQException ex) {
                    /**************************************************/
                    /* Probably encountered our no message available: */
                    /* write out error and mark loop to be exited     */
                    /**************************************************/
                    write2log("MQ exception on messages: iCC = " + ex.completionCode + " RC = " + ex.reasonCode);
                    done = true;
//               out.close();
                } catch (java.io.IOException ex) {
                    write2log("Java io exception: " + ex);
//                    System.out.println("Java io exception: " + ex);
                    qMgr.backout();   // Fout in IO , roling back the que
                    done = true;
                }

            } while (!done);
            qMgr.commit();
            myQueue.close();
            if (messCount == 0) {
//           outputFile.delete();
                write2log("No messages on que.");
            } else {
       			write2log("Processed "+messCount+" messages from queue.");
 				}
            qMgr.disconnect();
        } catch (MQException ex) {
            write2log("MQ exception in outer try : eCC = " + ex.completionCode + " RC = " + ex.reasonCode);
        }
    }

    private void verwerkMsg(String erin) throws java.io.IOException {
        String messtxt = "";
        messtxt = erin;
        int ret = -1;

        try {
            File outputFile = new File(mqDataDir,outFileName);
            FileWriter fwout = new FileWriter(outputFile, true);

            // read de budat
            budat = verwerkTag(erin, "BUDAT");
            ret = messtxt.indexOf("<E1MBXYI");

            do {

                messtxt = messtxt.substring(ret + 8);

                try {
                    fwout.write(maakBaanRecord(messtxt));
                } catch (OverslaanException overslaanException) {
                }

                ret = messtxt.indexOf("<E1MBXYI");

//                System.out.println("  messtxt=" + messtxt);
            } while ((ret != -1) && (ret != 0));
            fwout.close();
        } catch (IndexOutOfBoundsException ie) {
            write2log(" index out of bound while processing message: "+erin + " , index is " + ret);
        }
    }

    private String maakBaanRecord(String erin)   throws OverslaanException {
        String eruit = "nognix ";

        eruit = "";
        eruit = eruit + bwart(verwerkTag(erin, "BWART")) + ";";        //-1- type, wordt omgezet in een 1 of 3 of 4
        eruit = eruit + budat + ";";                           // -2- datum van transactie, kan zijn : tdinv100.idat, tdinv100.odat, tdinv100.trdt   of
        																			// tdpur045.date  of tdrpl100rrdt, tdrpl100.rddt

        eruit = eruit + verwerkTag(erin, "MATNR") + ";";       //-3-  tdrpl100.item (Artikel), when length = 5, then add 'C' in front OF
        																			// tdpur045.item (Artikel), when length = 5, then add 'C' in front OF
        																			// tdinv100.item, when length = 5; add 'C' in front

        eruit = eruit + theSign + verwerkTag(erin, "ERFMG") + ";";       //-4- tdinv100.dqan    OF tdpur045.dqan (Ontvangen aantal)  OF
        																			//tdrpl100.dqua (Aantal geleverd)

        eruit = eruit + verwerkTag(erin, "LGORT") + ";";       // -5-tdinv100.cwar    of tdpur045.cwar (Magazijn)

        eruit = eruit + verwerkTag(erin, "LFBNR") + ";";       // -6-tdrpl100.orno + tdrpl100.pono (Ordernummer + positienummer)  OF
        																			// tdpur045.orno + tdpur045.pono (Ordernummer + positienummer)

        eruit = eruit + verwerkTag(erin, "ERFME") + ";";       // -7- via (Org=CIS) tcedi442.codm controle of
        																			// tcedit442.codt gelijk is aan voorraadeenheid artikel,
        																			// zo niet aantal omrekenen via tiitm004

        eruit = eruit + grund(verwerkTag(erin, "GRUND")) + "\n";      // -8- tdinv100.recd; subtract 700 from SAP value

        return eruit;
    }
     
     private String grund(String erin) {
        if (erin.compareTo("") == 0 ) {
            erin = GRUNDdefaultWaarde;
        }
        try {
            int erinI = java.lang.Integer.parseInt(erin);
            if (erinI == 0) {
                erin = GRUNDdefaultWaarde;
            }
        } catch (NumberFormatException numberFormatException) {
            erin = GRUNDdefaultWaarde;
        }


        return erin;
    }
     
/* old method sept 01 2009    private String grund(String erin){
    	if (erin == "") {
    		erin =  GRUNDdefaultWaarde;
    		}
    		return erin;
   }

*/
    private String bwart(String erin) throws  OverslaanException{
    	String eruit="0";
    	BufferedReader jnputStream = null;

    	try {

   	 	FileReader fr = new FileReader(fbwart);
	    	jnputStream   = new BufferedReader(fr);
         String record;


			while ((record = jnputStream.readLine()) != null) {
         	    String gield[] = record.split(";");
         	    if (gield[0].compareTo(erin) == 0) {

         	   	eruit = gield[1];

         	   	jnputStream.close();
         	   	int i = java.lang.Integer.parseInt(gield[0]);
         	   	fr.close();

                  if (gield.length >= 3) {
							theSign = gield[2].trim();
						}	else {
							  	write2log("Not right configured for code : "+ erin+ " in bwart.config" );
							}

                  if (gield.length >= 4){
                  	GRUNDdefaultWaarde = gield[3];
						}  else {
							GRUNDdefaultWaarde = "";
							}
                  if (gield.length >= 5){
                  	if (gield[4].startsWith("ignore")   )
                    { throw new   OverslaanException(); }


						}


         	   	return(eruit);
         	   }
			}
    		write2log("Can't find code "+ erin+ " in bwart.config" );
//         System.exit(0);


	     } catch (FileNotFoundException fioex) {
				write2log("error from mqReadMess: bwart.config not found" );
//				System.exit(0);

   	  } catch (IOException ex) {
				write2log("IO error from mqReadMess on reading bwart.config" );
//				System.exit(0);
        }
        			return eruit;
		}

		//101, 102, 161, 162, 951 of 952  purchase reciepts            type 1
		//711, 712, 713, 714, 715, 716, 717 of 718 551, 552; 333, 334 ;  goods movement     type 3
 		//  even numbers are positive, odd are negative

		//601 of 602, 641, 642, 653 of 654  rpllevering                type 4
//      if (erin.startsWith("1")) eruit = "1";
//      if (erin.startsWith("9")) eruit = "1";
//      if (erin.startsWith("7")) eruit = "3";
//      if (erin.startsWith("6")) eruit = "4";



/*		switch(erin) {
			case startsWith("1"): eruit = "1"; break;
			case startsWith("9"): eruit = "1"; break;
			case startsWith("7"): eruit = "3"; break;
			case startsWith("6"): eruit = "4"; break;
			default : eruit = "";

			}
*/


    private String verwerkTag(String erin, String tag) {
        String eruit = "";
        int curentIndex = 0, beginIndex, endIndex, lenTag = tag.length();
        beginIndex = erin.indexOf("<" + tag + ">") + lenTag + 2;
        if (beginIndex != -1) {
            endIndex = erin.indexOf("</" + tag + ">");
            if (endIndex != -1) {
                eruit = erin.substring(beginIndex, endIndex);
                curentIndex = endIndex;
            }
        }

        return eruit.trim();
    }

    static void write2log(String msg) {
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

    static private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    static private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private  void moveStringToArchiveFile(String erin) {
	// verz joost 16 juni 2009 bericht als file in de archive
 	String newName = "message" + "_"+getDateTime();
 	write2log("Message   to archive: "+ newName );
	try {
			File fArchiveFile = new File(mqArchiveDir, newName);
			FileWriter fwout  = new FileWriter(fArchiveFile, true);
			fwout.write(erin);
			fwout.write("\n");
			fwout.close();
			write2log("Succesfull wrote message to archive file : " + newName);
		} catch (java.io.IOException ex) {
				 write2log("Java io exception " + ex + " moving the message to an archive file: " + newName );
		}

	}
}



class OverslaanException extends Exception {}
//   public boolean init() {
//   try {
//			String filenaam = "D://progs//mq//base//data//99068mqs.xml";
//			String filenaam = "D://progs//mq//base//data//testdata";
//		BufferedReader BR = new BufferedReader(new FileReader(filenaam));
//		String record  ="";
//            while (record != null) {
//
//                record = BR.readLine();
//                msgtest = msgtest + record ;
//            }
//          		System.out.println("msgtest="+ msgtest) ;
//          	   File outputFile = new File(outFileName);
//					FileWriter out = new FileWriter(outputFile, true);
//					frout = new FileWriter(outputFile, true);
//
//         verwerkMsg(msgtest);
//        return true;
//     } catch (IOException e) {   System.out.println("Can't open file , Java IOException: " + e);
//     }
//     return false;
//   }