package jach.msthesis.scheduler.regist;

import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;
import junit.framework.TestCase;

public class RegistSchedulerImprovedTest extends TestCase {
	RegistSchedulerImproved scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
	
	protected void setUp() throws Exception {
		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND");
		wis.load();
		offering.load();
		scheduler = new RegistSchedulerImproved(offering, wis.getWriteIns());
		
	}
	
	public void testAssign(){
		scheduler.assign();
		Statistics stat=new Statistics(scheduler);
		stat.display();
		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,prefix+"FORM5-2004-SECOND-CMSC-RSI","2004","SECOND");		
		exporter.export();
	}

}
