package jach.msthesis.scheduler.regist;

import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;
import junit.framework.TestCase;

public class RegistSchedulerImprovedMBM2Test extends TestCase {
	RegistSchedulerImprovedMBM2 scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
	
	protected void setUp() throws Exception {
		super.setUp();
		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND-CMSC");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND-CMSC");
		wis.load();
		offering.load();
		scheduler = new RegistSchedulerImprovedMBM2(offering,wis.getWriteIns());
	}

	public void testAssign(){
		scheduler.assign();
		Statistics stat=new Statistics(scheduler);
		stat.display();
		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,prefix+"FORM5-2004-SECOND-CMSC-MBM2","2004","SECOND");		
		exporter.export();
	}
}
