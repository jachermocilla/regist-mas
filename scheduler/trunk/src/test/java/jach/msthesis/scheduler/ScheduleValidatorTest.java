package jach.msthesis.scheduler;

import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jach.msthesis.scheduler.statistics.Statistics;
import junit.framework.TestCase;

public class ScheduleValidatorTest extends TestCase {

	ScheduleValidator scheduler;
	RegistOffering offering;
	RegistWriteInSource wis;
	
	
	protected void setUp() throws Exception {
		String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND");
		wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND");
		wis.load();
		offering.load();
		scheduler = new ScheduleValidator(offering, wis.getWriteIns());
		scheduler.loadForm5(prefix+"FORM5-2004-SECOND");
		Statistics stat=new Statistics(scheduler);
		stat.display();
	}
	
	public void testAssign(){
		
	}

}
