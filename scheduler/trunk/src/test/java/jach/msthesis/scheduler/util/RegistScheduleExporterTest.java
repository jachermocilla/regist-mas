package jach.msthesis.scheduler.util;

import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jach.msthesis.scheduler.regist.RegistSchedulerImprovedMBM;
import jach.msthesis.scheduler.regist.RegistSchedulerImprovedMBM2;
import jach.msthesis.scheduler.regist.RegistSchedulerImprovedMBM3;
import jach.msthesis.scheduler.statistics.Statistics;
import junit.framework.TestCase;

public class RegistScheduleExporterTest extends TestCase {
	RegistSchedulerImprovedMBM3 scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
	
	protected void setUp() throws Exception {
		
		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND-CMSC");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND-CMSC");
		wis.load();
		offering.load();
		scheduler = new RegistSchedulerImprovedMBM3(offering, wis.getWriteIns());
		
	}
	
	public void testAssign(){
		scheduler.assign();
		Statistics stat=new Statistics(scheduler);
		stat.display();
		/*
		 * Export the result in an external file
		 */
		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,prefix+"FORM5-2004-SECOND-CMSC-RSI","2004","SECOND");		
		exporter.export();
	}

}
