package jach.msthesis.scheduler.regist;

import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.statistics.Statistics;
import junit.framework.TestCase;

public class RegistSchedulerImprovedMBMTest extends TestCase {
	RegistSchedulerImprovedMBM scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	
	protected void setUp() throws Exception {
		super.setUp();
		String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
		offering = new RegistOffering(prefix + "CLASSES-SAMPLE");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND");
		wis.load();
		offering.load();
		scheduler = new RegistSchedulerImprovedMBM(wis,offering);
		
	}
	
	public void testAssign(){
		scheduler.assign();
		Statistics stat=new Statistics(scheduler);
		stat.display();
	}

}
