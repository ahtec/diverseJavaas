package config.java.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import ariba.base.core.Base;
import ariba.base.core.Partition;
import ariba.base.core.aql.AQLOptions;
import ariba.base.core.aql.AQLQuery;
import ariba.base.core.aql.AQLResultCollection;
import ariba.procure.core.Requisition;
import ariba.server.objectserver.SimpleScheduledTask;
import ariba.util.core.Hashtable;
import ariba.util.core.Util;
import ariba.util.core.Vector;



public class MVGReqUpdateActive extends SimpleScheduledTask {
	
	private Requisition req;
	

	
	public Partition thisPartition;
	
	public Vector argsVector;
	
	public Hashtable argumenten;
	String   get_Date;

	
	public void init(Partition partition, String scheduledTaskName,
			Hashtable arguments) {
		
		super.init(partition, scheduledTaskName, arguments);
		thisPartition = partition;
		argumenten = arguments;
		argsVector = argumenten.keysVector();
		
	       config.java.log.Log.gerardTest.setDebugOn();
	       config.java.log.Log.gerardTest.debug("MVGReqUpdateActive "  );

		
	}
	
	public void run() {
		try {
			String WA;
			BufferedReader inc = new BufferedReader(
					new FileReader("waFile"));
			while (((WA = inc.readLine()) != null)) {
				config.java.log.Log.gerardTest.debug("Record: " + WA);
				verwerkWA(WA);
			}
			
			inc.close();
		} catch (IOException e) {
			config.java.log.Log.gerardTest.debug("IOException " + e);
			
		} catch (Exception ee) {
			config.java.log.Log.gerardTest.debug("Exception " + ee);
		}
	}
	
	private void verwerkWA(String wa) {
		{
			AQLOptions options = new AQLOptions(thisPartition);
			options.setBatchSize(500);
			AQLQuery query = AQLQuery.parseQuery("SELECT * FROM ariba.procure.core.Requisition WHERE UniqueName = :1");
			options.setActualParameters(Util.vector(wa));
			AQLResultCollection aqlresults = Base.getService().executeQuery(
					query, options);
			try {
				if (aqlresults.getFirstError() != null) {
					config.java.log.Log.gerardTest.debug("getFirstError fout: " + wa);
				}
				int m = 0;
				while (aqlresults.next() && (m++ < 500000)) {
					try {
						req = (Requisition) Base.getSession().objectFromId(	aqlresults.getBaseId("Requisition"));
						ariba.util.core.Date now = new ariba.util.core.Date();
		                if(now != null)
		                	req.setDottedFieldValue("LastModified", now);
						req.save();
						Base.getSession().transactionCommit();
						
					} catch (Exception e) {
						config.java.log.Log.gerardTest.debug("IOfout: " + e);
					}
				}
				
			} finally {
				aqlresults.close();
			}			
		}		
	}	
}
