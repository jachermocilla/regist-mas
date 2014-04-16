package jach.msthesis.scheduler.regist;

import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;
import junit.framework.TestCase;

public class RegistSchedulerImprovedMBM3Test extends TestCase {

	RegistSchedulerImprovedMBM3 scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
	
	protected void setUp() throws Exception {
		super.setUp();
		super.setUp();

		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND-CMSC");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND-CMSC");
		wis.load();
		offering.load();
		scheduler = new RegistSchedulerImprovedMBM3(offering,wis.getWriteIns());
	}

	public void testAssign(){
		scheduler.assign();
		Statistics stat=new Statistics(scheduler);
		stat.display();
		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,prefix+"FORM5-2004-SECOND-CMSC-MBM3","2004","SECOND");		
		exporter.export();
	}

}
